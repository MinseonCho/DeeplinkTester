package ui.page

import base.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PageViewModel: BaseViewModel() {

    val url: MutableStateFlow<String> = MutableStateFlow("")
    private val _eventChannel = Channel<PageEvent>(capacity = Channel.BUFFERED)
    val eventFlow: Flow<PageEvent> = _eventChannel.receiveAsFlow()

    fun onUrlChanged(url: String) {
        this.url.value = url
    }

    fun onSendButtonClicked() {
        viewModelScope.launch {
            _eventChannel.send(PageEvent.TriggerUrl(url.value))
        }
    }
}