package ui.page

import base.BaseViewModel
import io.ktor.http.URLBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import model.QueryItem

class PageViewModel : BaseViewModel() {

    val urlUiState: MutableStateFlow<String> = MutableStateFlow("")

    private val _queryUiState: MutableStateFlow<QueryUiState> = MutableStateFlow(QueryUiState.Loading)
    val queryUiState: StateFlow<QueryUiState> = _queryUiState.asStateFlow()

    private val _eventChannel = Channel<PageEvent>(capacity = Channel.BUFFERED)
    val eventFlow: Flow<PageEvent> = _eventChannel.receiveAsFlow()

    private var queryMap : MutableMap<Int, QueryItem> = mutableMapOf()

    fun onUrlChanged(url: String) {
        this.urlUiState.value = url
        parseUrlAndUpdateState(url = url)
    }

    fun onSendButtonClicked() {
        viewModelScope.launch {
            _eventChannel.send(PageEvent.TriggerUrl(urlUiState.value))
        }
    }

    fun onAddButtonClicked() {
        val queryItem = QueryItem(
            order = queryMap.count(),
            key = "",
            value = ""
        )

        queryMap[queryItem.order] = queryItem
        _queryUiState.value = QueryUiState.Success(queryMap.values.toList())
    }

    private fun parseUrlAndUpdateState(url: String) {
        viewModelScope.launch {
            runCatching {
                parseQueryParameters(url)
            }.onSuccess { queries ->
                println("mscho, queries: $queries")
                queryMap.clear()
                queryMap.putAll(queries.associateBy { it.order })
                _queryUiState.value = QueryUiState.Success(queries)
            }.onFailure {
                _queryUiState.value = QueryUiState.Error
            }
        }
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

        newQueries.forEach { query ->
            newUrlBuilder.parameters.append(query.key, query.value)
        }

        return newUrlBuilder.buildString()
    }

    fun onQueryValueChanged(value: String, queryItem: QueryItem) {
        queryMap[queryItem.order] = queryItem.apply { this.value = value }
        _queryUiState.value = QueryUiState.Success(queryMap.values.toList())

        urlUiState.value = generateNewUrlWith(
            originUrl = urlUiState.value,
            newQueries = queryMap.values.toList()
        )
    }

    fun onQueryKeyChanged(key: String, queryItem: QueryItem) {
        queryMap[queryItem.order] = queryItem.apply { this.key = key }
        _queryUiState.value = QueryUiState.Success(queryMap.values.toList())

        urlUiState.value = generateNewUrlWith(
            originUrl = urlUiState.value,
            newQueries = queryMap.values.toList()
        )
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
            .mapIndexed { index, param ->
                val parts = param.split("=")
                val key = parts.getOrNull(0).orEmpty()
                val value = parts.getOrNull(1).orEmpty()
                QueryItem(
                    order = index,
                    key = key,
                    value = value
                )
            }
    }
}

sealed interface QueryUiState {
    data class Success(val queries: List<QueryItem>) : QueryUiState
    data object Error : QueryUiState
    data object Loading : QueryUiState
}