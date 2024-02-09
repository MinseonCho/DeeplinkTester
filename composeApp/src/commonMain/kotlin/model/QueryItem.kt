package model

data class QueryItem(
    var key: String,
    var value: String,
    var isChecked: Boolean = true
) {
    companion object {
        fun generateEmptyQueryItem(): QueryItem = QueryItem(
            key = "",
            value = "",
            isChecked = false
        )
    }
}
