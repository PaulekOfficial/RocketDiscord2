package pro.paulek.commands.music;

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
import pro.paulek.objects.MusicManager;
import pro.paulek.util.PlaylistUtils;

import java.util.Objects;

public class QueueCommand extends Command {

    private final static Logger logger = LoggerFactory.getLogger(QueueCommand.class);

    private final IRocketDiscord rocketDiscord;

    public QueueCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("music-queue");
        this.setDescription("wyświetla kolejkę odtwarzanych utworów muzycznych.");
        this.setUsage("/music-queue");
        var commandData = Commands.slash("music-queue", "Shows music queue");
        this.setCommandData(commandData);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, TextChannel channel, Guild guild, Member member) {
        MusicManager musicPlayer;

        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isEmpty()) {
            logger.warn("Music manager is empty for guild {}", guild.getId());
            musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
            musicPlayer.init();
            rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);

            event.replyEmbeds(PlaylistUtils.generatePlaylistEmbed(musicPlayer, ":arrow_lower_left: Co gram :arrow_lower_right:").build()).queue();
            return;
        }


        musicPlayer = manager.get();
        event.replyEmbeds(PlaylistUtils.generatePlaylistEmbed(musicPlayer, ":arrow_lower_left: Co gram :arrow_lower_right:").build()).queue();
    }
}
