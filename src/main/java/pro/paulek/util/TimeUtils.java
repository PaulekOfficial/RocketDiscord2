package pro.paulek.util;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import pro.paulek.objects.MusicManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {

    private static final SimpleDateFormat formatterWithHours;
    private static final SimpleDateFormat formatter;

    static {
        formatterWithHours = new SimpleDateFormat("HH:mm:ss");
        formatterWithHours.setTimeZone(TimeZone.getTimeZone("UTC"));

        formatter = new SimpleDateFormat("mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String millisecondsToMinutesFormat(long milliseconds) {
        var date = new Date(milliseconds);
        if (date.getHours() > 0) {
            return formatterWithHours.format(date);
        }
        return formatter.format(date);
    }

    public static long playlistTime(MusicManager musicManager) {
        long queueTrackTime = musicManager.getQueue().stream().mapToLong(AudioTrack::getDuration).sum();
        queueTrackTime += musicManager.getCurrentTrack().isPresent() ? musicManager.getCurrentTrack().get().getDuration() - musicManager.getCurrentTrack().get().getPosition() : 0;

        return queueTrackTime;
    }

    public static String calculateTimeToPlayTrack(MusicManager musicManager) {
        return millisecondsToMinutesFormat(playlistTime(musicManager));
    }
}
