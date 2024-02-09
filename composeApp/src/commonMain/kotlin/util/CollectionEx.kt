package util

fun <T> MutableList<T>.clearAndAddAll(list: List<T>) {
    clear()
    addAll(list)
}