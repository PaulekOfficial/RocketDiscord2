package pro.paulek.objects;

public class CachedMessage {

    private int id;

    private String authorName;
    private String authorID;

    private String messageID;
    private String content;

    private boolean tts;
    private String[] embeds;
    private String[] allowedMentions;
    private String messageReference;
    private String[] stickerIDs;
    private String[] filesURLs;
    private String payloadJson;
    private String[] attachments;

    public CachedMessage(int id, String authorName, String authorID, String messageID, String content, boolean tts, String[] embeds, String[] allowedMentions, String messageReference, String[] stickerIDs, String[] filesURLs, String payloadJson, String[] attachments) {
        this.id = id;
        this.authorName = authorName;
        this.authorID = authorID;
        this.messageID = messageID;
        this.content = content;
        this.tts = tts;
        this.embeds = embeds;
        this.allowedMentions = allowedMentions;
        this.messageReference = messageReference;
        this.stickerIDs = stickerIDs;
        this.filesURLs = filesURLs;
        this.payloadJson = payloadJson;
        this.attachments = attachments;
    }

    public boolean isTts() {
        return tts;
    }

    public void setTts(boolean tts) {
        this.tts = tts;
    }

    public String[] getEmbeds() {
        return embeds;
    }

    public void setEmbeds(String[] embeds) {
        this.embeds = embeds;
    }

    public String[] getAllowedMentions() {
        return allowedMentions;
    }

    public void setAllowedMentions(String[] allowedMentions) {
        this.allowedMentions = allowedMentions;
    }

    public String getMessageReference() {
        return messageReference;
    }

    public void setMessageReference(String messageReference) {
        this.messageReference = messageReference;
    }

    public String[] getStickerIDs() {
        return stickerIDs;
    }

    public void setStickerIDs(String[] stickerIDs) {
        this.stickerIDs = stickerIDs;
    }

    public String[] getFilesURLs() {
        return filesURLs;
    }

    public void setFilesURLs(String[] filesURLs) {
        this.filesURLs = filesURLs;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public String[] getAttachments() {
        return attachments;
    }

    public void setAttachments(String[] attachments) {
        this.attachments = attachments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
