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
import pro.paulek.managers.MusicManager;

import java.util.Objects;

public class RepeatCommand extends Command {

    private final static Logger logger = LoggerFactory.getLogger(SkipCommand.class);

    private final IRocketDiscord rocketDiscord;

    public RepeatCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("repeat");
        this.setDescription("włącza powtarzanie tego samego utowru lub playlisty");
        this.setUsage("/repeat");
        this.setCommandData(Commands.slash("repeat", "Repeats current track or playlist"));
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
//        if (!event.getMember().getVoiceState().inAudioChannel() || !memberAudioChannel.getId().equals(musicPlayer.getAudioChannel().getId())) {
//            event.reply(":construction: Aby kontrolować bota, musisz byc na kanale z nim!").queue();
//            return;
//        }

        if (musicPlayer.getNowPlayingTrack() == null) {
            event.reply(":snake: Ale żadna muzyka nie jest włączona").queue();
            return;
        }

        event.reply("Funkcja nie dostępna :x:").queue();

//        var repeat = musicPlayer.isRepeat();
//        musicPlayer.setRepeat(!);
//
//        if (!repeat) {
//            event.reply(":repeat_one: Włączono powtarzanie utowru").queue();
//            return;
//        }
//
//        event.reply(":repeat: Wyłączono powtarzanie utowrów").queue();
    }
}
