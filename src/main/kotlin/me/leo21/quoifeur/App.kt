package me.leo21.quoifeur

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import dev.kord.core.Kord
import dev.kord.core.behavior.reply
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import io.github.cdimascio.dotenv.dotenv
import java.io.File


suspend fun main() {
    val dotenv = dotenv {
        directory = {}.javaClass.getResource("/.env")?.path?.toString()!!
    }

    val client = Kord(dotenv["token"])

    val rows = csvReader { delimiter = '\t' }
        .readAllWithHeader(File({}.javaClass.getResource("/Lexique383.tsv")?.path?.toString()!!))

    client.on<ReadyEvent> {
        println("Bot is online !")
    }

    client.on<MessageCreateEvent> {

        if (message.author?.isBot == true) return@on

        val msg = message.content.replace(Regex("[^a-zA-Z0-9\\s]"), " ")
        val word = msg.split("\\s".toRegex()).filter { it != "" }.takeLast(1)[0].lowercase()
        val lastSyll = rows.first { it["ortho"] == word }["syll"]?.split(Regex("[- ]"))?.takeLast(1)?.get(0)

        val wordsToSend = rows.filter {
            it["syll"]?.split(Regex("[- ]"))?.get(0) == lastSyll && it["nbsyll"]?.toInt()!! > 1
        }

        val wordToSend1 = wordsToSend.random()

        val wordToSend = wordToSend1["orthosyll"]?.split(Regex("[- ]"))?.toMutableList()

        wordToSend?.removeFirst()

        message.reply {
            content = wordToSend?.joinToString("")
        }
    }

    client.login()
}