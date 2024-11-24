package ui.page

sealed class PageEvent {

    class TriggerUrl(
        val url: String
    ) : PageEvent()
}
