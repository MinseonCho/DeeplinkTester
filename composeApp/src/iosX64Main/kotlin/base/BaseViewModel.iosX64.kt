package base

import kotlinx.coroutines.CoroutineScope

actual open class BaseViewModel actual constructor() {
    actual val viewModelScope: CoroutineScope
        get() = TODO("Not yet implemented")

    actual open fun destroy() {
    }

}