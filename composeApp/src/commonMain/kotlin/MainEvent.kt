sealed interface MainEvent {

    data object ShowAdbPathDialog : MainEvent

    data object ShowPage : MainEvent
}
