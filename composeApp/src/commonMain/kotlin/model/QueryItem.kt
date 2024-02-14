package model

data class QueryItem(
    val key: String,
    val value: String,
    val isChecked: Boolean = true
) {
    companion object {
        fun generateEmptyQueryItem(): QueryItem = QueryItem(
            key = "",
            value = "",
            isChecked = false
        )
    }
}