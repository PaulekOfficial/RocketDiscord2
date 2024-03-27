package pro.paulek.listeners.fun;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;

import java.util.Objects;

public class MemesListeners extends ListenerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(MemesListeners.class);

    private final IRocketDiscord rocketDiscord;

    public MemesListeners(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        var guildConfiguration = rocketDiscord.getGuildConfigurations().get(event.getGuild().getId());
        if (guildConfiguration.isEmpty()) {
            return;
        }

        if (!guildConfiguration.get().getMemesChannels().contains(event.getChannel().getId())) {
            return;
        }

        for (Message.Attachment attachment : event.getMessage().getAttachments()) {
            if (!attachment.isImage() && !attachment.isVideo() && !attachment.isSpoiler()) {
                continue;
            }

            event.getMessage().addReaction(Emoji.fromUnicode("U+1F44C")).queue();
            event.getMessage().addReaction(Emoji.fromUnicode("U+1F602")).queue();
        }
    }
}
