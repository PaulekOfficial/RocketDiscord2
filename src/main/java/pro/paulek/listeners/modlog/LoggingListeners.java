package pro.paulek.listeners.modlog;

import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateAvatarEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdatePendingEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pro.paulek.IRocketDiscord;
import pro.paulek.objects.guild.DiscordMessage;

import java.time.Instant;
import java.util.Objects;

public class LoggingListeners extends ListenerAdapter {

    private final IRocketDiscord rocketDiscord;

    public LoggingListeners(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord, "rocketDiscord");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        var message = new DiscordMessage(
                event.getAuthor().getName(),
                event.getAuthor().getId(),
                event.getMessage().getId(),
                event.getMessage().getContentRaw(),
                DiscordMessage.MessageAction.NEW,
                event.getMessage().getTimeCreated().toInstant()
        );

        this.rocketDiscord.getDiscordMessages().save(event.getMessage().getIdLong(), message);
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        var message = new DiscordMessage(
                event.getAuthor().getName(),
                event.getAuthor().getId(),
                event.getMessage().getId(),
                event.getMessage().getContentRaw(),
                DiscordMessage.MessageAction.EDITED,
                event.getMessage().getTimeCreated().toInstant()
        );

        this.rocketDiscord.getDiscordMessages().save(event.getMessage().getIdLong(), message);
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        var message = new DiscordMessage(
                null,
                null,
                event.getMessageId(),
                null,
                DiscordMessage.MessageAction.DELETED,
                Instant.now()
        );

        this.rocketDiscord.getDiscordMessages().save(event.getMessageIdLong(), message);
    }
}
