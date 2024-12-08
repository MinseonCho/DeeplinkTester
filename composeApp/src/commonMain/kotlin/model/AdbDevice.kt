package model

data class AdbDevice(
    val id: String,
    val description: String,
    val isSelected: Boolean = false
) 
