package pro.paulek.objects;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MusicManager extends AudioEventAdapter implements Runnable, AudioSendHandler {

    private final static Logger logger = LoggerFactory.getLogger(MusicManager.class);

    private Thread watchdogThread;

    private final Guild guild;
    private final AudioPlayer audioPlayer;
    private final ByteBuffer byteBuffer;
    private final MutableAudioFrame audioFrame;
    private final BlockingQueue<AudioTrack> queue;

    private AudioTrack currentTrack;

    private long sumTrackTime;

    public MusicManager(AudioPlayer audioPlayer, Guild guild) {
        this.audioPlayer = Objects.requireNonNull(audioPlayer);
        this.guild = Objects.requireNonNull(guild);
        this.byteBuffer = ByteBuffer.allocate(1024);
        this.audioFrame = new MutableAudioFrame();
        this.queue = new LinkedBlockingQueue<>();
        this.sumTrackTime = 0;
    }

    public void init() {
        this.audioPlayer.addListener(this);
        this.guild.getAudioManager().setSendingHandler(this);
        this.audioFrame.setBuffer(byteBuffer);

        this.watchdogThread = new Thread(this);
        this.watchdogThread.start();
    }

    @Override
    public void run() {
//        var currentTrack = audioPlayer.getPlayingTrack();
//        var duration = currentTrack.getDuration();
//        var time = 0L;
//        var v = duration - currentTrack.getPosition();
//
//        if (time != v) {
//            sumTrackTime -= v - time;
//            time = v;
//        }
        if (sumTrackTime <=0) {
            sumTrackTime = 0;
        } else {
            sumTrackTime -= 1000;
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException exception) {
            logger.error("Track duration watchdog sleep error", exception);
        }
    }

    public void removeAllTracks() {
        queue.clear();
    }

    public void queue(AudioTrack audioTrack) {
        if (audioPlayer.startTrack(audioTrack, true)) {
            this.currentTrack = audioTrack;
            this.sumTrackTime += audioTrack.getDuration();
            return;
        }

        this.queue.offer(audioTrack);
        this.sumTrackTime += audioTrack.getDuration();
    }

    public void nextTrack() {
        this.currentTrack = queue.poll();
        audioPlayer.startTrack(currentTrack, false);
    }

    public void killWatchdog() {
        this.watchdogThread.interrupt();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.currentTrack = null;
        if (!endReason.mayStartNext) {
            return;
        }

        this.nextTrack();
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    @Override
    public boolean canProvide() {
        return audioPlayer.provide(audioFrame);
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        ((Buffer) byteBuffer).flip();
        return byteBuffer;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        logger.info("Music Player for guild " + guild.getName() + " paused");
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        logger.info("Music Player for guild " + guild.getName() + " resumed");
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        logger.info("Started track " + track.getIdentifier() + " in guild " + guild.getName());
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        logger.error("Cannot play track in guild " + guild.getName(), exception);
        this.currentTrack = null;
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        logger.warn("Music player stuck at " + thresholdMs + " on " + guild.getName());
    }

    public AudioTrack getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(AudioTrack currentTrack) {
        this.currentTrack = currentTrack;
    }

    public Thread getWatchdogThread() {
        return watchdogThread;
    }

    public void setWatchdogThread(Thread watchdogThread) {
        this.watchdogThread = watchdogThread;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    public long getSumTrackTime() {
        return sumTrackTime;
    }

    public void setSumTrackTime(long sumTrackTime) {
        this.sumTrackTime = sumTrackTime;
    }
}
