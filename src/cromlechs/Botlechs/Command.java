package cromlechs.Botlechs;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public abstract class Command {
    private String name;
    private String description;
    private String guide;
    private Member.Exclusivity level = Member.Exclusivity.STANDARD;

    //A message builder that disables pings which can be used mass mentions members.
    static MessageBuilder noMassPingMessage = new MessageBuilder().denyMentions(
            Message.MentionType.EVERYONE,
            Message.MentionType.ROLE,
            Message.MentionType.USER,
            Message.MentionType.HERE);

    public Command(String name, String description, String guide) {
        this.name = name;
        this.description = description;
        this.guide = guide;
    }

    public Command(String name, String description, String guide, Member.Exclusivity level) {
        this(name, description, guide);
        this.level = level;
    }

    public boolean canUse(String user) {
        return level.hasAccess(user);
    }

    public boolean execute(@NotNull GuildMessageReceivedEvent event, String args[]) {
        if(!level.hasAccess(event.getAuthor().getId())) {
            say(event.getChannel(), "You do not have access to this command");
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getGuide() {
        return Botlechs.prefix + guide;
    }

    public static void say(TextChannel ch, String message) {
        noMassPingMessage.clear();
        ch.sendMessage(noMassPingMessage.append(message).build()).queue();
    }
}
