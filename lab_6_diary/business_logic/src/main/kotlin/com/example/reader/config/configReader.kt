@file:Suppress("ktlint:standard:filename", "ktlint:standard:no-wildcard-imports")

package com.example.reader.config

import com.example.logging.Log4jLogger
import com.example.*
import java.io.FileInputStream
import java.util.*
import kotlin.system.exitProcess

object ConfigReader {
    private val logger = Log4jLogger(ConfigReader::class.java)

    private const val ERROR_CONFIG_LOADER = -2

    private val properties: Properties by lazy {
        logger.info("Starting config reader")

        val props = Properties()
        try {
            val configPath = System.getProperty("config.path", "C:\\Users\\Admin\\Desktop\\43\\63\\PPO\\PPO\\lab_6_diary\\config\\application.properties")
            props.load(FileInputStream(configPath))
            logger.info("Configuration loaded successfully from $configPath")
        } catch (e: Exception) {
            logger.error("Failed to load configuration", e)
            println("Failed to load configuration :${e.message}")
            exitProcess(ERROR_CONFIG_LOADER)
        }
        props
    }
    val db: String by lazy { getProperty("db") }

    val dbUrl: String by lazy { getProperty("db.url") }

    val dbUser: String by lazy { getProperty("db.user") }
    val dbPassword: String by lazy { getProperty("db.password") }

    val exitSuccess: Int by lazy { getProperty("exit.success").toIntOrNull() ?: 0 }
    val exitFailure: Int by lazy { getProperty("exit.failure").toIntOrNull() ?: -1 }

    private fun getProperty(propertyName: String): String {
        val value = properties.getProperty(propertyName)
        if (value != null) {
//            logger.info("Property '$propertyName' loaded successfully with value: $value")
            return value
        } else {
            logger.error("Property '$propertyName' was not loaded", LoadConfigError())
            println("Property '$propertyName' was not loaded")
            exitProcess(ERROR_CONFIG_LOADER)
        }
    }
}
