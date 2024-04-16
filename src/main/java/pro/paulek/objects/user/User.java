package pro.paulek.objects.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private final static Logger logger = LoggerFactory.getLogger(User.class);
    private int id;
    private String discordID;
    private String username;
    private String avatarUrl;
    private boolean bot;
    private boolean system;
    private boolean mfaEnabled;
    private boolean verification;
    private String bannerUrl;
    private int accentColor;
    private String locale;
    private String email;
    private int flags;
    private int premiumType;
    private int publicFlags;
    private LocalDateTime timestamp;
    private int experience;

    public User(int id, String discordID, String username, String avatarUrl, boolean bot, boolean system, boolean mfaEnabled, boolean verification, String bannerUrl, int accentColor, String locale, String email, int flags, int premiumType, int publicFlags, LocalDateTime timestamp) {
        this.id = id;
        this.discordID = discordID;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.bot = bot;
        this.system = system;
        this.mfaEnabled = mfaEnabled;
        this.verification = verification;
        this.bannerUrl = bannerUrl;
        this.accentColor = accentColor;
        this.locale = locale;
        this.email = email;
        this.flags = flags;
        this.premiumType = premiumType;
        this.publicFlags = publicFlags;
        this.timestamp = timestamp;
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

    public boolean isVerification() {
        return verification;
    }

    public void setVerification(boolean verification) {
        this.verification = verification;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return getId() == user.getId() && isBot() == user.isBot() && isSystem() == user.isSystem() && isMfaEnabled() == user.isMfaEnabled() && isVerification() == user.isVerification() && getAccentColor() == user.getAccentColor() && getFlags() == user.getFlags() && getPremiumType() == user.getPremiumType() && getPublicFlags() == user.getPublicFlags() && Objects.equals(getDiscordID(), user.getDiscordID()) && Objects.equals(getUsername(), user.getUsername()) && Objects.equals(getAvatarUrl(), user.getAvatarUrl()) && Objects.equals(getBannerUrl(), user.getBannerUrl()) && Objects.equals(getLocale(), user.getLocale()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getTimestamp(), user.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDiscordID(), getUsername(), getAvatarUrl(), isBot(), isSystem(), isMfaEnabled(), isVerification(), getBannerUrl(), getAccentColor(), getLocale(), getEmail(), getFlags(), getPremiumType(), getPublicFlags(), getTimestamp());
    }
}
