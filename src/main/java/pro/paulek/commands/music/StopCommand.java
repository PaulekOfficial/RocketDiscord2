package pro.paulek.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
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
        this.setCommandData(new CommandData("stop", "Stops music player and removes all tracks"));
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event, TextChannel channel, Guild guild, Member member) {
        var musicPlayer = rocketDiscord.getMusicManager(guild.getId());

        if (musicPlayer == null) {
            musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
            musicPlayer.init();
            rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
        }

        musicPlayer.removeAllTracks();
        musicPlayer.getAudioPlayer().stopTrack();
        event.reply(":o: Zatrzymano odtwarzenie muzyki").queue();
    }
}
