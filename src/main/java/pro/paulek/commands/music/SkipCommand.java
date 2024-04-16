package pro.paulek.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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
        MusicManager musicPlayer = null;

        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isEmpty()) {
            logger.warn("Music manager is empty for guild {}", guild.getId());
            musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
            musicPlayer.init();
            rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
        }

        if (manager.isPresent()) {
            musicPlayer = manager.get();
        }

        var memberAudioChannel = event.getMember().getVoiceState().getChannel();
        if (!event.getMember().getVoiceState().inAudioChannel() || !memberAudioChannel.getId().equals(musicPlayer.getAudioChannel().getId())) {
            event.reply(":construction: Aby kontrolować bota, musisz byc na kanale z nim!").queue();
            return;
        }

        musicPlayer.getPlayer().stopTrack();
        musicPlayer.nextTrack();


        if (musicPlayer.getNowPlayingTrack().isEmpty()) {
            event.reply(":face_with_monocle: Ale co ja mam pominąć, skoro nic nie leci").queue();
            return;
        }
        event.reply(":rewind: Pominięto utwór").queue();

        var currentTrack = musicPlayer.getNowPlayingTrack().get();

        var embed = new EmbedBuilder()
                .setTitle("Następnie grane")
                .setDescription(currentTrack.getInfo().title)
                .setColor(Color.GREEN)
                .addField("Kanał", event.getChannel().getName(), true)
                .addField("Czas trwania", TimeUtils.millisecondsToMinutesFormat(currentTrack.getDuration()), true)
                .addField("Przewidywany czas odtworzenia utworu", "Teraz", true)
                .addField("Pozycja w kolejne", "Teraz", true)
                .setAuthor(currentTrack.getInfo().author, currentTrack.getInfo().uri, "https://cdn.discordapp.com/attachments/885206963598819360/927269255337087026/butelka.png")
                .setTimestamp(LocalDateTime.now())
                .build();
        event.getChannel().sendMessageEmbeds(embed).queue();
    }
}
