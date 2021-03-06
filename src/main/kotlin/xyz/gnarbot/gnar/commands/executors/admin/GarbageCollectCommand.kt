package xyz.gnarbot.gnar.commands.executors.admin

import net.dv8tion.jda.core.entities.Message
import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor

@Command(
        aliases = arrayOf("gc"),
        description = "Request Java to garbage collect.",
        administrator = true,
        category = Category.NONE
)
class GarbageCollectCommand : CommandExecutor() {
    override fun execute(message: Message, args: Array<String>) {
        message.send().embed("Garbage Collection") {
            color = BotConfiguration.ACCENT_COLOR
            val interrupt = if (!args.isEmpty()) args[0].toBoolean() else false

            bot.shards.forEach { it.clearData(interrupt) }
            field("Wrappers", false, "Removed settings instances.")

            field("Guild Data Remaining", true, bot.shards.sumBy { it.guildData.size })

            System.gc()
            field("GC Request", false, "Garbage collection request sent to JVM.")
            bot.log.info("Garbage collection request sent to JVM.")
        }.rest().queue()
    }
}