package base

import kotlinx.coroutines.CoroutineScope

expect open class BaseViewModel() {

    val viewModelScope: CoroutineScope

    open fun destroy()
}