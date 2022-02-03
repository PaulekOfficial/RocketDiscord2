package pro.paulek.data;

import java.util.List;

public class GuildConfiguration {

    private final String guildID;
    private String guildName;

    private boolean commandsChannelsWhitelistMode;
    private List<String> commandChannels;
    private List<String> memesChannels;
    private List<String> autoVoiceChannels;

    private boolean welcomeImageEnable;
    private boolean leaveImageEnable;
    private String welcomeImageMessage;
    private String leaveImageMessage;
    private String welcomeChannel;

    private String announcementsChannel;

    private List<String> botAdmins;

    private String djRole;

    public GuildConfiguration(String guildID, String guildName, boolean commandsChannelsWhitelistMode, List<String> commandChannels, List<String> memesChannels, List<String> autoVoiceChannels, boolean welcomeImageEnable, boolean leaveImageEnable, String welcomeImageMessage, String leaveImageMessage, String welcomeChannel, String announcementsChannel, List<String> botAdmins, String djRole) {
        this.guildID = guildID;
        this.guildName = guildName;
        this.commandsChannelsWhitelistMode = commandsChannelsWhitelistMode;
        this.commandChannels = commandChannels;
        this.memesChannels = memesChannels;
        this.autoVoiceChannels = autoVoiceChannels;
        this.welcomeImageEnable = welcomeImageEnable;
        this.leaveImageEnable = leaveImageEnable;
        this.welcomeImageMessage = welcomeImageMessage;
        this.leaveImageMessage = leaveImageMessage;
        this.welcomeChannel = welcomeChannel;
        this.announcementsChannel = announcementsChannel;
        this.botAdmins = botAdmins;
        this.djRole = djRole;
    }

    public boolean isWelcomeImageEnable() {
        return welcomeImageEnable;
    }

    public void setWelcomeImageEnable(boolean welcomeImageEnable) {
        this.welcomeImageEnable = welcomeImageEnable;
    }

    public boolean isLeaveImageEnable() {
        return leaveImageEnable;
    }

    public void setLeaveImageEnable(boolean leaveImageEnable) {
        this.leaveImageEnable = leaveImageEnable;
    }

    public String getLeaveImageMessage() {
        return leaveImageMessage;
    }

    public void setLeaveImageMessage(String leaveImageMessage) {
        this.leaveImageMessage = leaveImageMessage;
    }

    public String getWelcomeImageMessage() {
        return welcomeImageMessage;
    }

    public void setWelcomeImageMessage(String welcomeImageMessage) {
        this.welcomeImageMessage = welcomeImageMessage;
    }

    public String getGuildID() {
        return guildID;
    }

    public String getGuildName() {
        return guildName;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public boolean isCommandsChannelsWhitelistMode() {
        return commandsChannelsWhitelistMode;
    }

    public void setCommandsChannelsWhitelistMode(boolean commandsChannelsWhitelistMode) {
        this.commandsChannelsWhitelistMode = commandsChannelsWhitelistMode;
    }

    public List<String> getCommandChannels() {
        return commandChannels;
    }

    public void setCommandChannels(List<String> commandChannels) {
        this.commandChannels = commandChannels;
    }

    public List<String> getMemesChannels() {
        return memesChannels;
    }

    public void setMemesChannels(List<String> memesChannels) {
        this.memesChannels = memesChannels;
    }

    public List<String> getAutoVoiceChannels() {
        return autoVoiceChannels;
    }

    public void setAutoVoiceChannels(List<String> autoVoiceChannels) {
        this.autoVoiceChannels = autoVoiceChannels;
    }

    public String getWelcomeChannel() {
        return welcomeChannel;
    }

    public void setWelcomeChannel(String welcomeChannel) {
        this.welcomeChannel = welcomeChannel;
    }

    public String getAnnouncementsChannel() {
        return announcementsChannel;
    }

    public void setAnnouncementsChannel(String announcementsChannel) {
        this.announcementsChannel = announcementsChannel;
    }

    public List<String> getBotAdmins() {
        return botAdmins;
    }

    public void setBotAdmins(List<String> botAdmins) {
        this.botAdmins = botAdmins;
    }

    public String getDjRole() {
        return djRole;
    }

    public void setDjRole(String djRole) {
        this.djRole = djRole;
    }
}
