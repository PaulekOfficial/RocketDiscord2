package pro.paulek.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.objects.MusicManager;
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
        this.setCommandData(new CommandData("skip", "Skips current played track"));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event, TextChannel channel, Guild guild, Member member) {
        var musicPlayer = rocketDiscord.getMusicManager(guild.getId());

        if (musicPlayer == null) {
            musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
            musicPlayer.init();
            rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
        }

        musicPlayer.getAudioPlayer().stopTrack();
        musicPlayer.nextTrack();


        if (musicPlayer.getCurrentTrack() == null) {
            event.reply(":face_with_monocle: Ale co ja mam pominąć, skoro nic nie leci").queue();
            return;
        }
        event.reply(":rewind: Pominięto utwór").queue();

        var embed = new EmbedBuilder()
                .setTitle("Następnie grane")
                .setDescription(musicPlayer.getCurrentTrack().getInfo().title)
                .setColor(Color.GREEN)
                .addField("Kanał", event.getChannel().getName(), true)
                .addField("Czas trwania", TimeUtils.millisecondsToMinutesFormat(musicPlayer.getCurrentTrack().getDuration()), true)
                .addField("Przewidywany czas odtworzenia utworu", "Teraz", true)
                .addField("Pozycja w kolejne", "Teraz", true)
                .setAuthor(musicPlayer.getCurrentTrack().getInfo().author, musicPlayer.getCurrentTrack().getInfo().uri, "https://cdn.discordapp.com/attachments/885206963598819360/927269255337087026/butelka.png")
                .setTimestamp(LocalDateTime.now())
                .build();
        event.getChannel().sendMessageEmbeds(embed).queue();
    }
}
