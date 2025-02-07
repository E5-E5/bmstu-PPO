@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.logging

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import java.io.FileInputStream
import java.util.*

class Log4jLogger(private val clazz: Class<*>) : Logger {
    private val logger = LogManager.getLogger(clazz)

    init {
        val properties = Properties()
        try {
            val externalConfigPath = "C:\\Users\\Admin\\Desktop\\43\\63\\PPO\\PPO\\lab_6_diary\\config\\application.properties"
            properties.load(FileInputStream(externalConfigPath))
            val logLevel = properties.getProperty("log.level")
            if (logLevel != null) {
                val level = Level.toLevel(logLevel.uppercase(Locale.getDefault()))
                Configurator.setRootLevel(level)
                logger.info("Log level set to: $logLevel")
            } else {
                logger.warn("Log level not specified, using default level.")
            }
        } catch (e: Exception) {
            logger.error("Failed to load logging configuration, using default level.", e)
        }
    }

    override fun debug(message: String) {
        logger.debug(message)
    }

    override fun info(message: String) {
        logger.info(message)
    }

    override fun warn(message: String) {
        logger.warn(message)
    }

    override fun error(
        message: String,
        throwable: Throwable?,
    ) {
        logger.error(message, throwable)
    }
}
