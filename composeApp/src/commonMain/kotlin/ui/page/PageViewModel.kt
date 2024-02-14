package ui.page

import base.BaseViewModel
import io.ktor.http.URLBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import model.QueryItem
import util.clearAndAddAll

class PageViewModel : BaseViewModel() {

    val urlUiState: MutableStateFlow<String> = MutableStateFlow("")

    private val _eventChannel = Channel<PageEvent>(capacity = Channel.BUFFERED)
    val eventFlow: Flow<PageEvent> = _eventChannel.receiveAsFlow()

    private val _queryList: MutableList<QueryItem> = mutableListOf()
    val queryList: List<QueryItem> = _queryList

    fun onUrlChanged(url: String) {
        this.urlUiState.value = url
        parseUrlAndUpdateState(url = url)
    }

    fun onSendButtonClicked() {
        viewModelScope.launch {
            _eventChannel.send(PageEvent.TriggerUrl(urlUiState.value))
        }
    }

    private fun parseUrlAndUpdateState(url: String) {
        _queryList.clearAndAddAll(parseQueryParameters(url))
        onQueryListRefreshed()
    }

    fun onCheckedChanged(position: Int, isChecked: Boolean) {
        if (_queryList.indices.contains(position).not()) return
        _queryList[position] = _queryList[position].copy(isChecked = isChecked)

        onQueryListItemChanged(
            changedPosition = position
        )
    }

    fun onQueryValueChanged(position: Int, value: String) {
        if (_queryList.indices.contains(position).not()) return
        _queryList[position] = _queryList[position].copy(value = value, isChecked = true)

        onQueryListItemChanged(
            changedPosition = position
        )
    }

    fun onQueryKeyChanged(position: Int, key: String) {
        if (_queryList.indices.contains(position).not()) return
        _queryList[position] = _queryList[position].copy(key = key, isChecked = true)

        onQueryListItemChanged(
            changedPosition = position
        )
    }

    private fun onQueryListRefreshed() {
        _queryList.add(QueryItem.generateEmptyQueryItem())
    }

    private fun onQueryListItemChanged(changedPosition: Int) {
        if (changedPosition == _queryList.lastIndex) {
            _queryList.add(QueryItem.generateEmptyQueryItem())
        }

        urlUiState.value = generateNewUrlWith(
            originUrl = urlUiState.value,
            newQueries = _queryList
        )
    }

    private fun generateNewUrlWith(
        originUrl: String,
        newQueries: List<QueryItem>
    ): String {
        val newUrlBuilder = URLBuilder(originUrl)
        // TODO: encode 고려 필요한지 체크
        newUrlBuilder
            .parameters
            .clear()

        newQueries.filter { it.isChecked }.forEach { query ->
            newUrlBuilder.parameters.append(query.key, query.value)
        }

        return newUrlBuilder.buildString()
    }

    private fun parseQueryParameters(url: String): List<QueryItem> {
        val queryStartIndex = url.indexOf("?")
        if (queryStartIndex == -1 || queryStartIndex == url.lastIndex) {
            return emptyList()
        }

        val fragmentIndex = url.indexOf("#")
        val queryString = when (fragmentIndex == -1) {
            true -> {
                url.substring(queryStartIndex + 1)
            }

            false -> {
                url.substring(queryStartIndex + 1, fragmentIndex)
            }
        }

        return queryString.split("&")
            .map { param ->
                val parts = param.split("=")
                val key = parts.getOrNull(0).orEmpty()
                val value = parts.getOrNull(1).orEmpty()
                QueryItem(
                    key = key,
                    value = value
                )
            }
    }
}