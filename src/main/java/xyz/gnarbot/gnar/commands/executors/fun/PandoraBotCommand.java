package xyz.gnarbot.gnar.commands.executors.fun;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.StringUtils;
import xyz.gnarbot.gnar.BotConfiguration;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;

@Command(
        aliases = "pbot",
        category = Category.FUN)
public class PandoraBotCommand extends CommandExecutor {
    private static final ChatterBotFactory factory = new ChatterBotFactory();
    private ChatterBot bot = null;

    private ChatterBotSession session = null;

    @Override
    public void execute(Message message, String[] args) {
        try {
            if (bot == null) {
                bot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
                session = bot.createSession();
                message.send().info("Pandora-Bot session created for the server.").queue();
            }

            String input = StringUtils.join(args, " ");

            String output = session.think(input);
            message.send().embed("PandoraBot")
                    .setColor(BotConfiguration.ACCENT_COLOR)
                    .setDescription(output)
                    .rest().queue();

        } catch (Exception e) {
            message.send().error("PandoraBot has encountered an exception. Resetting PandoraBot.").queue();
            bot = null;
        }
    }

}
