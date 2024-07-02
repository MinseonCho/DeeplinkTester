package ui.page

sealed class PageEvent {

    class TriggerUrl(
        val absoluteAdbPath: String,
        val url: String
    ) : PageEvent()
}
