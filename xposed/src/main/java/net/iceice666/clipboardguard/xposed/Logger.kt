package net.iceice666.clipboardguard.xposed

// Custom logging module
class Logger(packageName: String) {

    private var name: String = packageName

    fun debug(message: String){}
    fun info(message:  String) {}
    fun warn(message: String){}
    fun error(message: String){}
    fun fatal(message: String){}
}