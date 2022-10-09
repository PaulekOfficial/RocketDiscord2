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

public class JoinCommand extends Command {

    private final static Logger logger = LoggerFactory.getLogger(PlayCommand.class);

    private final IRocketDiscord rocketDiscord;

    public JoinCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("join");
        this.setDescription("dołącza bota do kanału głosowego, na którym aktualnie się znajdujesz");
        this.setUsage("/join");
        var commandData = Commands.slash("join", "Joins music bot to voice channel");
        this.setCommandData(commandData);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, TextChannel channel, Guild guild, Member member) {
        var musicPlayer = rocketDiscord.getMusicManager(guild.getId());

        if (musicPlayer == null) {
            musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
            musicPlayer.init();
            rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
        }

        var audioChannel = event.getMember().getVoiceState().getChannel();
        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.reply(":satellite: Muszisz być na jakimś kanale, abym mógł dołączyć do niego").queue();
            return;
        }

        if (!guild.getAudioManager().isConnected()) {
            guild.getAudioManager().openAudioConnection(audioChannel);
            event.reply(":cookie: Dołączyłem!").queue();
        } else {
            event.reply(":confused: Ale ja już jestem na kanale głosowym").queue();
        }
    }
}
