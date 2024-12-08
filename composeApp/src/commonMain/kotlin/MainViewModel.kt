import base.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import platform.AdbPathFinder
import ui.NavigationItem

class MainViewModel : BaseViewModel() {

    private val _eventChannel = Channel<MainEvent>(capacity = Channel.BUFFERED)
    val eventFlow: Flow<MainEvent> = _eventChannel.receiveAsFlow()

    private var _adbAbsolutePath: String = ""
    val adbAbsolutePath: String
        get() = _adbAbsolutePath

    init {
        findAdbPath()
    }

    fun onNavItemClicked(navItem: NavigationItem) {
        when (navItem) {
            NavigationItem.Page -> {
                _eventChannel.trySend(MainEvent.ShowPage)
            }
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

    private fun findAdbPath() {
        val adbPath = AdbPathFinder.findAdbPath()
        if (adbPath != null) {
            _adbAbsolutePath = adbPath
            println("Found ADB path: $_adbAbsolutePath")
        } else {
            println("Failed to find ADB path")
            viewModelScope.launch {
                _eventChannel.send(MainEvent.ShowAdbPathDialog(adbAbsolutePath))
            }
        }
    }
}
