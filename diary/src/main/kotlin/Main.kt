package org.example

import java.security.MessageDigest

//fun bytesToHex(bytes: ByteArray): String {
//    val hexChars = CharArray(bytes.size * 2)
//    for (i in bytes.indices) {
//        val v = bytes[i].toInt() and 0xFF
//        hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
//        hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
//    }
//    return String(hexChars)
//}

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val name = "Kotlin"
    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
    // to see how IntelliJ IDEA suggests fixing it.
    println("Hello, " + name + "!")
    val a = "sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC"

    val sha384 = MessageDigest.getInstance("SHA-384")
    sha384.update(a.toByteArray(Charsets.UTF_8))
    val hash = sha384.digest()

    // Преобразование хеша в строку шестнадцатеричных символов
//    val hexHash = bytesToHex(hash).toLowerCase()

//    println("Хеш SHA-384 вашей строки: $hexHash")
}