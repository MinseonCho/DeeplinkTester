package platform

actual object AdbPathFinder {
    actual fun findAdbPath(): String? {
        // Android 앱에서는 adb 경로를 찾을 필요가 없으므로 null 반환
        return null
    }
} 