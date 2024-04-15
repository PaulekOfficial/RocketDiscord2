package pro.paulek.managers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.paulek.objects.enums.RepeatType;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

public class MusicManager extends AudioEventAdapter implements Runnable, AudioSendHandler {
    private final static Logger logger = LoggerFactory.getLogger(MusicManager.class);

    private final ExecutorService executorService;
    private final Guild guild;
    private AudioChannel audioChannel;
    private final AudioPlayer player;
    private final ByteBuffer byteBuffer;
    private final MutableAudioFrame audioFrame;
    private final BlockingQueue<AudioTrack> playlist;
    private RepeatType repeat;
    private AudioTrack nowPlayingTrack;
    private LocalDateTime lastPlayed;

    public MusicManager(AudioPlayer player, Guild guild) {
        this.player = Objects.requireNonNull(player);
        this.guild = Objects.requireNonNull(guild);
        this.byteBuffer = ByteBuffer.allocate(1024);
        this.audioFrame = new MutableAudioFrame();
        this.playlist = new LinkedBlockingQueue<>();
        this.repeat = RepeatType.NONE;

        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void init() {
        this.player.addListener(this);
        this.guild.getAudioManager().setSendingHandler(this);
        this.audioFrame.setBuffer(byteBuffer);

        this.executorService.submit(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException exception) {
                logger.error("Track duration watchdog sleep error", exception);
            }

            //Add to time 5 minutes
            if (lastPlayed == null || player.getPlayingTrack() != null) {
                continue;
            }

            var modifiedDate = lastPlayed.plus(5, ChronoUnit.MINUTES);

            //Bot leaves channel after 5 min of idle
            if (LocalDateTime.now().isAfter(modifiedDate) && player.getPlayingTrack() == null) {
                if (guild.getAudioManager().isConnected()) {
                    guild.getAudioManager().closeAudioConnection();
                    audioChannel = null;
                }
            }
        }
    }

    //TOOD better implementation of this method
    public CompletableFuture<BlockingQueue<AudioTrack>> getPlaylist() {
        return CompletableFuture.completedFuture(playlist);
    }

    public Future<Boolean> playTrack(AudioTrack track) {
        return executorService.submit(() -> {
            if (player.startTrack(track, true)) {
                this.nowPlayingTrack = track;
                return true;
            }

            this.playlist.offer(track);
            return false;
        });
    }

    public Future<Boolean> playTracks(AudioTrack... tracks) {
        return executorService.submit(() -> {
            for (var track : tracks) {
                if (player.startTrack(track, true)) {
                    this.nowPlayingTrack = track;
                    return true;
                }

                this.playlist.offer(track);
            }

            return false;
        });
    }

    public Future<Boolean> pauseTrack() {
        return executorService.submit(() -> {
            if (player.getPlayingTrack() == null) {
                return false;
            }

            player.setPaused(true);
            return true;
        });
    }

    public Future<Boolean> resumeTrack() {
        return executorService.submit(() -> {
            if (player.getPlayingTrack() == null) {
                return false;
            }

            player.setPaused(false);
            return true;
        });
    }

    public Future<Boolean> stopTrack() {
        return executorService.submit(() -> {
            if (player.getPlayingTrack() == null) {
                return false;
            }

            player.stopTrack();
            return true;
        });
    }

    public Future<Boolean> stopAndClearPlaylist() {
        return executorService.submit(() -> {
            if (player.getPlayingTrack() == null) {
                return false;
            }

            playlist.clear();
            return this.stopTrack().get();
        });
    }

    public Optional<AudioChannel> getAudioChannel() {
        return Optional.ofNullable(audioChannel);
    }

    public Optional<AudioPlayer> getPlayer() {
        return Optional.ofNullable(player);
    }

    //TODO better implementation
    public CompletableFuture<Optional<AudioTrack>> getNowPlayingTrack() {
        return CompletableFuture.completedFuture(Optional.ofNullable(nowPlayingTrack));
    }

    public void queue(AudioTrack audioTrack) {
        if (player.startTrack(audioTrack, true)) {
            this.nowPlayingTrack = audioTrack;

            return;
        }

        this.playlist.offer(audioTrack);
    }

    public Future<Boolean> skipTrack() {
        return executorService.submit(() -> {
            if (player.getPlayingTrack() == null) {
                return false;
            }

            return this.nextTrack().get();
        });
    }

    private Future<Boolean> nextTrack() {
        return executorService.submit(() -> {
            if (repeat == RepeatType.ONE) {
                if (nowPlayingTrack == null) {
                    return false;
                }

                this.nowPlayingTrack = nowPlayingTrack.makeClone();
                player.startTrack(nowPlayingTrack, false);
                return true;
            }

            if (repeat == RepeatType.ALL) {
                //TODO repeat all tracks
            }

            this.nowPlayingTrack = playlist.poll();
            if (nowPlayingTrack == null) {
                return false;
            }

            player.startTrack(nowPlayingTrack, false);
            return true;
        });
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastPlayed = LocalDateTime.now();
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
        return player.provide(audioFrame);
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        byteBuffer.flip();
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
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        logger.warn("Music player stuck at " + thresholdMs + " on " + guild.getName());
    }

    public void setAudioChannel(AudioChannel channel) {
        this.audioChannel = channel;
    }

    public RepeatType isRepeat() {
        return repeat;
    }

    public void setRepeat(RepeatType repeat) {
        this.repeat = repeat;
    }
}
