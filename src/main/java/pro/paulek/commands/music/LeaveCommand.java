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

public class LeaveCommand extends Command {

    private final static Logger logger = LoggerFactory.getLogger(PlayCommand.class);

    private final IRocketDiscord rocketDiscord;

    public LeaveCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("leave");
        this.setDescription("rozkazuje botu wyjście z kanału głosowego i zakończenie muzyki");
        this.setUsage("/leave");
        var commandData = new CommandData("leave", "Orders bot to leave voice channel");
        this.setCommandData(commandData);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event, TextChannel channel, Guild guild, Member member) {
        var musicPlayer = rocketDiscord.getMusicManager(guild.getId());

        if (musicPlayer == null) {
            musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
            musicPlayer.init();
            rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
        }

        var audioChannel = event.getMember().getVoiceState().getChannel();
        if (!guild.getAudioManager().isConnected()) {
            event.reply(":confused: Ale ja przecież nie jestem połączony z chatem głosowym").queue();
            return;
        }

        musicPlayer.removeAllTracks();
        musicPlayer.getAudioPlayer().stopTrack();
        guild.getAudioManager().closeAudioConnection();
        event.reply(":boomerang: Wyszedłem z kanału").queue();
    }
}
