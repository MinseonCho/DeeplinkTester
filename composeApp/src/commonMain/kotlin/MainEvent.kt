sealed interface MainEvent {

    data class ShowAdbPathDialog(
        val currentPath: String,
    ) : MainEvent

    data object ShowPage : MainEvent
}
