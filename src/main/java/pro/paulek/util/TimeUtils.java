package pro.paulek.util;

public class TimeUtils {

    public static String millisecondsToMinutesFormat(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;

        if (seconds < 10) {
            return String.valueOf(minutes) + ":0" + String.valueOf(seconds);
        }
        return String.valueOf(minutes) + ":" + String.valueOf(seconds);
    }
}
