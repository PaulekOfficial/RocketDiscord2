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

import java.util.Objects;

public class StopCommand  extends Command {

    private final static Logger logger = LoggerFactory.getLogger(StopCommand.class);

    private final IRocketDiscord rocketDiscord;

    public StopCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("stop");
        this.setDescription("zatrzymuje granie muzyki");
        this.setUsage("/stop");
        this.setCommandData(Commands.slash("stop", "Stops music player and removes all tracks"));
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
        if (!event.getMember().getVoiceState().inAudioChannel() ||  !memberAudioChannel.getId().equals(musicPlayer.getPlayingChannel().getId())) {
            event.reply(":construction: Aby kontrolować bota, musisz byc na kanale z nim!").queue();
            return;
        }

        musicPlayer.removeAllTracks();
        musicPlayer.getAudioPlayer().stopTrack();
        event.reply(":o: Zatrzymano odtwarzenie muzyki").queue();
    }
}
