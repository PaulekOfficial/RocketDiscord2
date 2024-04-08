package pro.paulek.listeners.modlog;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateAvatarEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pro.paulek.IRocketDiscord;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.Objects;

public class ModLogListener extends ListenerAdapter {
    private final IRocketDiscord rocketDiscord;

    public ModLogListener(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord, "rocketDiscord");
    }

    @Override
    public void onUserUpdateAvatar(@NotNull UserUpdateAvatarEvent event) {
        var updateEmbed = new EmbedBuilder()
                .setTitle("Zaktualizowano avatar użytkownika")
                .setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl())
                .setThumbnail(event.getUser().getAvatarUrl())
                .addField("Użytkownik", event.getUser().getAsMention(), true)
                .setFooter(String.format("ID: %s", event.getUser().getId()))
                .setColor(Color.CYAN)
                .setTimestamp(ZonedDateTime.now())
                .build();

        rocketDiscord.getJda().getGuildById("740276300815663105").getTextChannelById("1221222594997653594").sendMessageEmbeds(updateEmbed).queue();
    }

    @Override
    public void onUserUpdateOnlineStatus(@NotNull UserUpdateOnlineStatusEvent event) {
        super.onUserUpdateOnlineStatus(event);
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        MessageEmbed updateEmbed;
        if (event.getChannelLeft() == null) {
            updateEmbed = new EmbedBuilder()
                    .setTitle("Użytkownik dołączył do kanału głosowego")
                    .setAuthor(event.getMember().getUser().getName(), null, event.getMember().getUser().getAvatarUrl())
                    .addField("Użytkownik", event.getMember().getAsMention(), true)
                    .addField("Kanał", event.getChannelJoined().getAsMention(), true)
                    .setFooter(String.format("ID: %s", event.getMember().getId()))
                    .setColor(Color.GREEN)
                    .setTimestamp(ZonedDateTime.now())
                    .build();

            rocketDiscord.getJda().getGuildById("740276300815663105").getTextChannelById("1221222594997653594").sendMessageEmbeds(updateEmbed).queue();
            return;
        }

        updateEmbed = new EmbedBuilder()
                .setTitle("Użytkownik opuścił kanał głosowy")
                .setAuthor(event.getMember().getUser().getName(), null, event.getMember().getUser().getAvatarUrl())
                .addField("Użytkownik", event.getMember().getAsMention(), true)
                .addField("Kanał", event.getChannelLeft().getAsMention(), true)
                .setFooter(String.format("ID: %s", event.getMember().getId()))
                .setColor(Color.pink)
                .setTimestamp(ZonedDateTime.now())
                .build();

        rocketDiscord.getJda().getGuildById("740276300815663105").getTextChannelById("1221222594997653594").sendMessageEmbeds(updateEmbed).queue();
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        var updateEmbed = new EmbedBuilder()
                .setTitle("Wiadomość została edytowana przez użytkownika")
                .setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
                .addField("Autor", event.getAuthor().getAsMention(), true)
                .addField("Kanał", event.getChannel().getAsMention(), true)
                .addField("Stara wiadomość", "**missing data context**", false)
                .addField("Nowa wiadomość", event.getMessage().getContentRaw(), true)
                .setFooter(String.format("ID: %s", event.getMessageId()))
                .setColor(Color.YELLOW)
                .setTimestamp(ZonedDateTime.now())
                .build();

        rocketDiscord.getJda().getGuildById("740276300815663105").getTextChannelById("1221222594997653594").sendMessageEmbeds(updateEmbed).queue();
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        var deleteEmbed = new EmbedBuilder()
                .setTitle("Wiadomość została usunięta przez użytkownika")
                .addField("Autor", "**missing data context**", true)
                .addField("Kanał", event.getChannel().getAsMention(), true)
                .addField("Wiadomość", "**missing data context**", false)
                .setFooter(String.format("ID: %s", event.getMessageId()))
                .setColor(Color.RED)
                .setTimestamp(ZonedDateTime.now())
                .build();

        rocketDiscord.getJda().getGuildById("740276300815663105").getTextChannelById("1221222594997653594").sendMessageEmbeds(deleteEmbed).queue();
    }
}
