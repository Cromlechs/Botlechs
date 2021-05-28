package cromlechs.Botlechs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandManager {
    static ArrayList<Command> commandList = new ArrayList();

    public static void main(String[] args) {
        loadCommand();
    }

    public static void process(String request, @NotNull GuildMessageReceivedEvent event) {
        Scanner arguments = new Scanner(request);
        String commandName = arguments.next();
        Command command = getCommand(commandName);

        if(command == null) {
            Command.say(event.getChannel(), "Command not found.");
            return;
        }
        //Set argument to either an empty string or request minus the command name. This is to prevent IndexOutOfBoundException
        String argument = (request.length() <= commandName.length())?"":request.substring(commandName.length()+1);
        command.execute(event, argument.split(" "));
    }

    public static Command getCommand(String name) {
        for(Command c : commandList) {
            if(c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    private static void loadCommand() {
        Command say = new Command("Say","Repeats the message given","say (message)") {
            @Override
            public boolean execute(@NotNull GuildMessageReceivedEvent event, String[] args) {
                //If the user does not have access to this command, return
                if(!super.execute(event, args)) {
                    return false;
                }
                String msgArg = String.join(" ", args);
                System.out.println(event.getAuthor().getName() + " said " + msgArg);
                say(event.getChannel(), msgArg);
                return true;
            }
        };

        Command shutdown = new Command("Shutdown","Close the process for Botlechs","shutdown", Member.Exclusivity.DEVELOPER) {
            @Override
            public boolean execute(@NotNull GuildMessageReceivedEvent event, String[] args) {
                //If the user does not have access to this command, return
                if(!super.execute(event, args)) {
                    return false;
                }
                System.out.println("Shutting down");
                event.getChannel().sendMessage("Goodbye, Cruel World.").queue();
                event.getJDA().shutdown();
                return true;
            }
        };

        Command purgeReactions = new Command("PurgeReactions", "Purge all reactions from the last x line, currently doesn't work","purgeReactions (amount)", Member.Exclusivity.ADMIN) {
            @Override
            public boolean execute(@NotNull GuildMessageReceivedEvent event, String[] args) {
                if(!super.execute(event, args)) {
                    return false;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[0]);
                } catch(Exception e) {
                    say(event.getChannel(),"Invalid argument, please make sure that you follow the format "+getGuide());
                    return false;
                }
                System.out.printf("Purging %d lines of reaction in the %s channel\n",amount,event.getChannel().getName());
                RestAction<List<Message>> history = event.getChannel().getHistory().retrievePast(amount);

                int reactionCount = 0;

                for(Message m : history.complete()) {
                    List<MessageReaction> reactions = m.getReactions();
                    reactionCount += reactions.size();
                    m.clearReactions().queue();
                }
                say(event.getChannel(), String.format("Purged a total of %d reactions",reactionCount));
                return true;
            }
        };

        Command purge = new Command("Purge", "Purge an x amount of messages above this command","purge (amount)", Member.Exclusivity.ADMIN) {
            @Override
            public boolean execute(@NotNull GuildMessageReceivedEvent event, String[] args) {
                if(!super.execute(event, args)) {
                    return false;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[0]);
                } catch(Exception e) {
                    say(event.getChannel(),"Invalid argument, please make sure that you follow the format "+getGuide());
                    return false;
                }
                System.out.printf("Purging %d lines of reaction in the %s channel\n",amount,event.getChannel().getName());
                RestAction<List<Message>> history = event.getChannel().getHistory().retrievePast(++amount);

                int messageCount = 0;

                for(Message m : history.complete()) {
                    try {
                        m.delete().queue();
                        messageCount++;
                    } catch(Exception e) {
                    }
                }
                say(event.getChannel(), String.format("Purged a total of %d messages",--messageCount));
                return true;
            }
        };

        Command help = new Command("Help","Get a list of commands accessible to you","help") {
            @Override
            public boolean execute(@NotNull GuildMessageReceivedEvent event, String[] args) {
                //If the user does not have access to this command, return
                if(!super.execute(event, args)) {
                    return false;
                }

                EmbedBuilder embedBuild = new EmbedBuilder();
                String userID = event.getAuthor().getId();
                embedBuild.setColor(new Color(247,159,55));
                embedBuild.setTitle(Member.getAccessLevel(event.getAuthor().getId())+" commands");

                for(Command c : commandList) {
                    if(c.canUse(userID)) {
                        embedBuild.addField(c.getName(),
                                String.format("%s | %s", c.getDescription(), c.getGuide()),
                                false);
                    }
                }

                event.getChannel().sendMessage(embedBuild.build()).queue();
                return true;
            }
        };

        commandList.add(say);
        commandList.add(shutdown);
        commandList.add(help);
        commandList.add(purgeReactions);
        commandList.add(purge);
    }
}
