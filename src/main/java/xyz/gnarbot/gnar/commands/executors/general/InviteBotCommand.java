package xyz.gnarbot.gnar.commands.executors.general;

import net.dv8tion.jda.core.entities.Message;
import xyz.gnarbot.gnar.BotConfiguration;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;

@Command(aliases = {"invite", "invitebot"}, description = "Get a link to invite GN4R to your server.")
public class InviteBotCommand extends CommandExecutor {
    @Override
    public void execute(Message message, String[] args) {
        String link = "https://discordapp.com/oauth2/authorize?client_id=201492375653056512&scope=bot&permissions=8";

        message.send().embed("Get Gnar on your server!")
                .setColor(BotConfiguration.ACCENT_COLOR)
                .setDescription("__**[Click to invite Gnar to your server.](" + link + ")**__")
                .rest().queue();
    }
}
