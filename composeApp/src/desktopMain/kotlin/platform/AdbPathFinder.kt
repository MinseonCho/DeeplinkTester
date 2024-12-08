package platform

actual object AdbPathFinder {
    actual fun findAdbPath(): String? {
        return runCatching {
            val isWindows = System.getProperty("os.name").lowercase().contains("windows")
            val command = if (isWindows) "where adb" else "which adb"
            
            val process = Runtime.getRuntime().exec(command)
            val adbPath = process.inputStream.bufferedReader().use { it.readText() }.trim()
            
            if (adbPath.isNotEmpty()) {
                adbPath.lines().first() // Windows 에서는 여러 줄이 반환될 수 있음
            } else {
                null
            }
        }.getOrNull()
    }
} 
