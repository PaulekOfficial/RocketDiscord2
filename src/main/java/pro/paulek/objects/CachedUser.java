package pro.paulek.objects;

import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class CachedUser {

    private int id;
    private String discordID;
    private String username;
    private String avatarUrl;

    private boolean bot;
    private boolean system;
    private boolean mfaEnabled;
    private boolean verificated;

    private String bannerUrl;
    private int accentColor;

    private String locale;
    private String email;
    private int flags;
    private int premiumType;
    private int publicFlags;

    private LocalDateTime timestamp;

    private final static Logger logger = LoggerFactory.getLogger(CachedUser.class);

    public CachedUser(int id, String discordID, String username, String avatarUrl, boolean bot, boolean system, boolean mfaEnabled, String bannerUrl, int accentColor, String locale, String email, int flags, int premiumType, int publicFlags, LocalDateTime timestamp) {
        this.id = id;
        this.discordID = discordID;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.bot = bot;
        this.system = system;
        this.mfaEnabled = mfaEnabled;
        this.bannerUrl = bannerUrl;
        this.accentColor = accentColor;
        this.locale = locale;
        this.email = email;
        this.flags = flags;
        this.premiumType = premiumType;
        this.publicFlags = publicFlags;
        this.timestamp = timestamp;
    }

    /**
     * Creates #CachedUser form jda user
     * -1 or "" values are not implemented yet.
     * @param user
     */
    public CachedUser(User user, User.Profile profile) {
        this(-1, user.getId(), user.getName(), user.getAvatarUrl(), user.isBot(), user.isSystem(), false, profile.getBannerUrl(), profile.getAccentColorRaw(), "", "", user.getFlagsRaw(), -1, -1, LocalDateTime.now());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiscordID() {
        return discordID;
    }

    public void setDiscordID(String discordID) {
        this.discordID = discordID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public boolean isVerificated() {
        return verificated;
    }

    public void setVerificated(boolean verificated) {
        this.verificated = verificated;
    }

    public int getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(int accentColor) {
        this.accentColor = accentColor;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getPremiumType() {
        return premiumType;
    }

    public void setPremiumType(int premiumType) {
        this.premiumType = premiumType;
    }

    public int getPublicFlags() {
        return publicFlags;
    }

    public void setPublicFlags(int publicFlags) {
        this.publicFlags = publicFlags;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
