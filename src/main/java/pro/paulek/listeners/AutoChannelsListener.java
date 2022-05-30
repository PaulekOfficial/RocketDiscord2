package pro.paulek.listeners;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class AutoChannelsListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        super.onGuildVoiceJoin(event);
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        super.onGuildVoiceLeave(event);
    }
}
