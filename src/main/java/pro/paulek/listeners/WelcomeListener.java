package pro.paulek.listeners;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.IRocketDiscord;
import pro.paulek.util.ImageGenerator;

import java.util.Objects;

public class WelcomeListener extends ListenerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(WelcomeListener.class);

    private final IRocketDiscord rocketDiscord;
    private final ImageGenerator imageGenerator = new ImageGenerator();

    public WelcomeListener(IRocketDiscord rocketDiscord) {
        this.rocketDiscord = Objects.requireNonNull(rocketDiscord);
    }

    //TODO run in another thread
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        var guildConfiguration = rocketDiscord.getGuildConfigurations().get(event.getGuild().getId());
        if (guildConfiguration.isEmpty()) {
            return;
        }

        var gc = guildConfiguration.get();
        if (!gc.isWelcomeImageEnable()) {
            return;
        }

        var image = imageGenerator.generateWelcomeImage(event.getUser(), gc.getWelcomeImageMessage());

        Objects.requireNonNull(event.getGuild().getTextChannelById(gc.getWelcomeChannel())).sendFiles(FileUpload.fromData(image)).queue();
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        var guildConfiguration = rocketDiscord.getGuildConfigurations().get(event.getGuild().getId());
        if (guildConfiguration.isEmpty()) {
            return;
        }

        var gc = guildConfiguration.get();
        if (!gc.isLeaveImageEnable()) {
            return;
        }

        var image = imageGenerator.generateLeaveImage(event.getUser(), gc.getLeaveImageMessage());

        Objects.requireNonNull(event.getGuild().getTextChannelById(gc.getWelcomeChannel())).sendFiles(FileUpload.fromData(image)).queue();
    }
}
