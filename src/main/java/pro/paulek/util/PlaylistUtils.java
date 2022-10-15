package pro.paulek.util;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import pro.paulek.objects.MusicManager;

import java.awt.*;
import java.time.LocalDateTime;

public class PlaylistUtils {


    public static EmbedBuilder generatePlaylistEmbed(MusicManager manager, String title) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(title);
        stringBuilder.append(System.getProperty("line.separator"));

        int queueSize = manager.getQueue().size();

        if (manager.getCurrentTrack() != null) {
            stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.append(":banjo: Aktualnie gram ");
            stringBuilder.append(manager.getCurrentTrack().getInfo().title);
            stringBuilder.append(" | :clock1: ");
            stringBuilder.append(TimeUtils.millisecondsToMinutesFormat(manager.getCurrentTrack().getDuration()));
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

        for (AudioTrack audioTrack : manager.getQueue()) {
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
        stringBuilder.append(String.valueOf(manager.getQueue().size()));

        var embed = new EmbedBuilder()
                .setDescription(stringBuilder.toString())
                .setColor(Color.GREEN)
                .setAuthor("Dodano do playlisty", "https://paulek.pro/", "https://paulek.pro/img/butelka.png")
                .setTimestamp(LocalDateTime.now())
                ;
        return embed;
    }
}
