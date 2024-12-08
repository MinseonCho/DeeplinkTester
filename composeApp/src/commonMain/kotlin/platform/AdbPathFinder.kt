package platform

import model.AdbDevice

expect object AdbPathFinder {
    fun findAdbPath(): String?
    fun getDevices(adbPath: String): List<AdbDevice>
} 
