package pro.paulek.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.commands.Command;
import pro.paulek.managers.MusicManager;
import pro.paulek.util.TimeUtils;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class SkipCommand extends Command {

    private final static Logger logger = LoggerFactory.getLogger(SkipCommand.class);

    private final IRocketDiscord rocketDiscord;

    public SkipCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("skip");
        this.setDescription("pomija aktualnie odtwarzany utwór");
        this.setUsage("/skip");
        this.setCommandData(Commands.slash("skip", "Skips current played track"));
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, TextChannel channel, Guild guild, Member member) {
        MusicManager musicPlayer = getOrCreateMusicManager(guild);

        try {
            if (!musicPlayer.skipTrack().get()) {
                event.reply(":face_with_monocle: Ale co ja mam pominąć, skoro nic nie leci").queue();
                return;
            }

            event.reply(":rewind: Pominięto utwór").queue();

            var audioTrack = musicPlayer.getNowPlayingTrack();

            if (audioTrack.isEmpty()) {
                return;
            }

            var embed = this.createEmbed(event, audioTrack.get());

            event.getChannel().sendMessageEmbeds(embed).queue();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private MusicManager getOrCreateMusicManager(Guild guild) {
        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isPresent()) {
            return manager.get();
        }

        logger.warn("Music manager is empty for guild {}", guild.getId());
        MusicManager musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
        musicPlayer.init();
        rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
        return musicPlayer;
    }

    private MessageEmbed createEmbed(SlashCommandInteractionEvent event, AudioTrack track) {
        return new EmbedBuilder()
                .setTitle("Następnie grane")
                .setDescription(track.getInfo().title)
                .setColor(Color.GREEN)
                .addField("Kanał", event.getChannel().getName(), true)
                .addField("Czas trwania", TimeUtils.millisecondsToMinutesFormat(track.getDuration()), true)
                .addField("Przewidywany czas odtworzenia utworu", "Teraz", true)
                .addField("Pozycja w kolejne", "Teraz", true)
                .setAuthor(track.getInfo().author, track.getInfo().uri, "https://cdn.discordapp.com/attachments/885206963598819360/927269255337087026/butelka.png")
                .setTimestamp(LocalDateTime.now())
                .build();
    }
}