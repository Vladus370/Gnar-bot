package xyz.gnarbot.gnar.commands

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.exceptions.PermissionException
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.BotConfiguration
import xyz.gnarbot.gnar.guilds.GuildData
import xyz.gnarbot.gnar.utils.Utils

class CommandHandler(private val guildData: GuildData, private val bot: Bot) {
    /** @returns Enabled command entries. */
    val enabled: List<CommandRegistry.CommandEntry> get() = bot.commandRegistry.entries.apply { removeAll(disabled) }.toList()

    /** @returns Disabled command entries. */
    val disabled: MutableList<CommandRegistry.CommandEntry> = mutableListOf()

    /**
     * @return the amount of successful requests on this command handler.
     */
    var requests = 0
        private set

    /**
     * Call the command based on the message content.
     *
     * @param message Message object.
     * @return If the call was successful.
     */
    fun callCommand(message: Message) : Boolean {
        val content = message.content
        if (!content.startsWith(BotConfiguration.PREFIX)) return false

        // Tokenize the message.
        val tokens = Utils.stringSplit(content, ' ')

        val label = tokens[0].substring(BotConfiguration.PREFIX.length).toLowerCase()

        val args = tokens.copyOfRange(1, tokens.size) //tokens.subList(1, tokens.size)
        
        val entry = bot.commandRegistry.getEntry(label) ?: return false

        if (disabled.contains(entry)) {
            message.send().error("This command is disabled by the server owner.").queue()
            return false
        }

        val cls = entry.cls
        val info = entry.info

        val member = message.member

        if (info.administrator) {
            if (!BotConfiguration.ADMINISTRATORS.contains(member.idLong)) {
                message.send().error("This command is for bot administrators only.").queue()
                return false
            }
        }
        if (info.guildOwner) {
            if (message.guild.owner != member) {
                message.send().error("This command is for server owners only.").queue()
                return false
            }
        }

        if (info.permissions.isNotEmpty()) {
            if (info.scope == Scope.VOICE) {
                if (member.voiceState.channel == null) {
                    message.send().error("This command requires you to be in a voice channel.").queue()
                    return false
                }
            }
            if (!info.scope.checkPermission(message, member, *info.permissions)) {
                val requirements = entry.info.permissions.map(Permission::getName)
                message.send().error("You lack the following permissions: `$requirements` in " + when (info.scope) {
                    Scope.GUILD -> "the guild `${message.guild.name}`."
                    Scope.TEXT -> "the text channel `${message.textChannel.name}`."
                    Scope.VOICE -> "the voice channel `${member.voiceState.channel.name}`."
                }).queue()
                return false
            }
        }

        try {
            requests++
            val cmd = cls.newInstance()

            cmd.guildData = guildData
            cmd.commandInfo = info

            cmd.execute(message, args)
            return true
        } catch (e: PermissionException) {
            message.send().error("The bot lacks the permission `${e.permission.getName()}` required to perform this command.").queue()
        } catch (e: RuntimeException) {
            message.send().error("**Exception**: " + e.message).queue()
            e.printStackTrace()
        }
        return false
    }

    /**
     * Enable the command [cmd].
     *
     * @param cmd Command entry.
     */
    fun enableCommand(cmd: CommandRegistry.CommandEntry) : CommandRegistry.CommandEntry? {
        if (!disabled.contains(cmd)) return null
        disabled -= cmd
        return cmd
    }

    /**
     * Enable the command named [label].
     *
     * @param label Command label.
     */
    fun enableCommand(label: String) : CommandRegistry.CommandEntry? {
        return bot.commandRegistry.getEntry(label)?.let(this::enableCommand)
    }

    /**
     * Disable the command [cmd].
     *
     * @param cmd Command entry.
     */
    fun disableCommand(cmd: CommandRegistry.CommandEntry) : CommandRegistry.CommandEntry? {
        if (disabled.contains(cmd)) return null
        if (!cmd.info.disableable) return null
        disabled += cmd
        return cmd
    }

    /**
     * Enable the command named [label].
     *
     * @param label Command label.
     */
    fun disableCommand(label: String) : CommandRegistry.CommandEntry? {
        return bot.commandRegistry.getEntry(label)?.let(this::disableCommand)
    }
}
