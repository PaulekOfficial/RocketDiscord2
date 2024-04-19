package pro.paulek.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pro.paulek.IRocketDiscord;
import pro.paulek.commands.Command;
import pro.paulek.managers.MusicManager;

import java.util.Objects;

public class JoinCommand extends Command {

    private final IRocketDiscord rocketDiscord;

    public JoinCommand(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);

        this.setName("join");
        this.setDescription("Joins the bot to a voice channel");
        this.setUsage("/join [channel name]");
        var commandData = Commands.slash("join", "Joins the bot to a voice channel");
        commandData.addOption(OptionType.STRING, "channel", "Name of the channel to join", false);
        this.setCommandData(commandData);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, TextChannel channel, Guild guild, Member member) {
        MusicManager musicPlayer = getOrCreateMusicManager(guild);

        String channelName = event.getOption("channel") != null ? event.getOption("channel").getAsString() : null;
        VoiceChannel voiceChannel = null;

        if (channelName != null) {
            for (VoiceChannel vc : guild.getVoiceChannels()) {
                if (vc.getName().equalsIgnoreCase(channelName)) {
                    voiceChannel = vc;
                    break;
                }
            }
        } else if (Objects.requireNonNull(member.getVoiceState()).inAudioChannel()) {
            voiceChannel = (VoiceChannel) member.getVoiceState().getChannel();
        }

        if (voiceChannel == null) {
            event.reply(":x: Nie mogę dołączyć do kanału głosowego").queue();
            return;
        }

        var joined = musicPlayer.joinChannel(voiceChannel);
        try {
            if (joined.get()) {
                event.reply(":white_check_mark: Dołączono do kanału głosowego " + voiceChannel.getName()).queue();
            } else {
                event.reply(":x: Nie mogłem dołączyć do kanału głosowego " + voiceChannel.getName()).queue();
            }
        } catch (Exception e) {
            event.reply(":x: Nie mogłem dołączyć do kanału głosowego " + voiceChannel.getName()).queue();
        }
    }

    private MusicManager getOrCreateMusicManager(Guild guild) {
        var manager = rocketDiscord.getMusicManager(guild.getId());
        if (manager.isPresent()) {
            return manager.get();
        }

        MusicManager musicPlayer = new MusicManager(rocketDiscord.getAudioManager().createPlayer(), guild);
        musicPlayer.init();
        rocketDiscord.getMusicManagers().add(guild.getId(), musicPlayer);
        return musicPlayer;
    }
}