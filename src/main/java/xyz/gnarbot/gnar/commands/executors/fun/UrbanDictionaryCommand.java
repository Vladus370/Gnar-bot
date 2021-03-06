package xyz.gnarbot.gnar.commands.executors.fun;

import com.mashape.unirest.http.Unirest;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.gnarbot.gnar.BotConfiguration;
import xyz.gnarbot.gnar.Credentials;
import xyz.gnarbot.gnar.commands.Category;
import xyz.gnarbot.gnar.commands.Command;
import xyz.gnarbot.gnar.commands.CommandExecutor;

@Command(
        aliases = {"urbandict", "ub", "urbandictionary"},
        category = Category.FUN
)
public class UrbanDictionaryCommand extends CommandExecutor {
    @Override
    public void execute(Message message, String[] args) {
        try {
            String query = StringUtils.join(args, "+");

            JSONObject json = Unirest.get("https://mashape-community-urban-dictionary.p.mashape.com/define")
                    .queryString("term", query)
                    .header("X-Mashape-Key", Credentials.MASHAPE)
                    .header("Accept", "text/plain")
                    .asJson()
                    .getBody()
                    .getObject();

            JSONArray words = json.getJSONArray("list");

            if (words.length() < 1) {
                message.send().error("Could not find that word, rip u").queue();
                return;
            }

            JSONObject word = words.getJSONObject(0);

            message.send().embed("Urban Dictionary")
                    .setColor(BotConfiguration.ACCENT_COLOR)
                    .setThumbnail("https://s3.amazonaws.com/mashape-production-logos/apis/53aa4f67e4b0a9b1348da532_medium")
                    .field("Word", true, "[" + word.getString("word") + "](" + word.getString("permalink") + ")")
                    .field("Definition", true, word.optString("definition"))
                    .field("Example", true, word.optString("example"))
                    .rest().queue();

        } catch (Exception e) {
            message.send().error("Could not find that word, rip u").queue();
        }
    }

}
