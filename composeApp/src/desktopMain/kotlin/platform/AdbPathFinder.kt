package platform

import model.AdbDevice

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

    actual fun getDevices(adbPath: String): List<AdbDevice> {
        return runCatching {
            val process = Runtime.getRuntime().exec("$adbPath devices -l")
            val output = process.inputStream.bufferedReader().use { it.readText() }
            
            output.lines()
                .drop(1) // 첫 줄은 "List of devices attached" 이므로 제외
                .filter { it.isNotBlank() }
                .map { line ->
                    val parts = line.trim().split(Regex("\\s+"))
                    AdbDevice(
                        id = parts[0],
                        description = parts.drop(1).joinToString(" ")
                    )
                }
        }.getOrDefault(emptyList())
    }
} 
