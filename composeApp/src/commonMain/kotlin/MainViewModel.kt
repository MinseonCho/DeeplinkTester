import base.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import ui.NavigationItem

class MainViewModel : BaseViewModel() {

    private val _eventChannel = Channel<MainEvent>(capacity = Channel.BUFFERED)
    val eventFlow: Flow<MainEvent> = _eventChannel.receiveAsFlow()

    private var _adbAbsolutePath: String = ""
    val adbAbsolutePath: String
        get() = _adbAbsolutePath

    fun onNavItemClicked(navItem: NavigationItem) {
        when (navItem) {
            NavigationItem.Settings -> {
                _eventChannel.trySend(
                    MainEvent.ShowAdbPathDialog(
                        currentPath = adbAbsolutePath
                    )
                )
            }
        }
    }

    fun onAdbPathDialogConfirmButtonClicked(adbAbsolutePath: String) {
        _adbAbsolutePath = adbAbsolutePath
    }
}
