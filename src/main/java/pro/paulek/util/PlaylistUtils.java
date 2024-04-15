package pro.paulek.util;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import pro.paulek.managers.MusicManager;

import java.awt.*;
import java.time.LocalDateTime;

public class PlaylistUtils {


    public static EmbedBuilder generatePlaylistEmbed(MusicManager manager, String title) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(title);
        stringBuilder.append(System.getProperty("line.separator"));

        int queueSize = manager.getPlaylist().size();

        if (manager.getNowPlayingTrack().isPresent()) {
            var currentTrack = manager.getNowPlayingTrack().get();

            stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.append(":banjo: Aktualnie gram ");
            stringBuilder.append(currentTrack.getInfo().title);
            stringBuilder.append(" | :clock1: ");
            stringBuilder.append(TimeUtils.millisecondsToMinutesFormat(currentTrack.getDuration()));
            stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.append(System.getProperty("line.separator"));
        }

        if (queueSize <= 0) {
            stringBuilder.append(":empty_nest: Nic tu nie ma :(");
            stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.append(":tent: Dodaj utwór poleceniem /play");
        }

        int i = 1 + queueSize;

        for (AudioTrack audioTrack : manager.getPlaylist()) {
            if (i <= 10 + queueSize) {
                stringBuilder.append(":notes:");
                stringBuilder.append(i);
                stringBuilder.append(".` ");
                stringBuilder.append(audioTrack.getInfo().title);
                stringBuilder.append("` | :clock10: ");
                stringBuilder.append(TimeUtils.millisecondsToMinutesFormat(audioTrack.getDuration()));
                stringBuilder.append(System.getProperty("line.separator"));
                stringBuilder.append(System.getProperty("line.separator"));
            }

            manager.queue(audioTrack);
            i++;
        }
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Liczba utworów w playliście: ");
        stringBuilder.append(String.valueOf(manager.getPlaylist().size()));

        var embed = new EmbedBuilder()
                .setDescription(stringBuilder.toString())
                .setColor(Color.GREEN)
                .setThumbnail("https://img.youtube.com/vi/" + manager.getPlaylist().element().getIdentifier() + "/0.jpg")
                .setAuthor("Dodano do playlisty", "https://paulek.pro/", "https://cdn.pixabay.com/photo/2019/08/11/18/27/icon-4399630_1280.png")
                .setTimestamp(LocalDateTime.now())
                ;

        if (manager.getNowPlayingTrack().isPresent()) {
            embed.setThumbnail("https://img.youtube.com/vi/" + manager.getNowPlayingTrack().get().getIdentifier() + "/0.jpg");
        }

        return embed;
    }
}
