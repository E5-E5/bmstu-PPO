@file:Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
package com.example.logger

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Logger {
//    private val logger = KotlinLogging.logger(loggerName)
    private val logs = listOf("INFO", "DEBUG", "WARNING", "ERROR")
//    fun logInfo(message: String) {
//        logWithTimestamp("INFO", message)
//    }
//
//    fun logDebug(message: String) {
//        logWithTimestamp("DEBUG", message)
//    }
//
//    fun logWarning(message: String) {
//        logWithTimestamp("WARNING", message)
//    }
//
//    fun logError(message: String) {
//        logWithTimestamp("ERROR", message)
//    }

    fun logMessage(level: Int, message: String) {
        val currentDateTime = LocalDateTime.now()
        val formattedDateTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
//        val formattedMessage = "[$formattedDateTime] [${logs.get(level)}] $message"
            //println(formattedMessage)
//        logger.info { formattedMessage }
    }
}