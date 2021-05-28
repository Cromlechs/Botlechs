package cromlechs.Botlechs;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.EnumSet;
import java.util.Scanner;

/**
 * The main class used for the discord bot Botlechs.
 * Connects to the Java Discord API and the Listener class.
 */
public class Botlechs {
    static JDA jda;
    static String prefix = "!";

    public static void main(String[] args) throws LoginException {
        //Get bot token, exit if the file containing it is not found
        String token;
        try {
            token = new Scanner(new File("src/resource/Token.txt")).next();
        } catch (FileNotFoundException e) {
            System.out.println("Token.txt not found. " + e.getMessage());
            return;
        }

        jda = JDABuilder.createDefault(token,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES).disableCache(EnumSet.of(
                CacheFlag.CLIENT_STATUS,
                CacheFlag.ACTIVITY,
                CacheFlag.EMOTE
        )).enableCache(CacheFlag.VOICE_STATE).addEventListeners(new Listener())
                .setActivity(Activity.playing(String.format("with sanity | %shelp", prefix)))
                .build();
        //No idea why these main methods won't run on their own
        CommandManager.main(new String[]{});
        Member.main(new String[]{});
    }
}
