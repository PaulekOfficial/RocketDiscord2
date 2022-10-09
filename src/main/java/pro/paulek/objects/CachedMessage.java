package pro.paulek.objects;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;

import java.util.List;

public class CachedMessage {

    private String authorName;
    private String authorID;

    private String messageID;
    private String content;

    private boolean tts;
    private List<MessageEmbed> embeds;
    private MessageReference messageReference;
    private List<StickerItem> stickerIDs;
    private List<Message.Attachment> attachments;

    public CachedMessage(Message message) {
        this.authorName = message.getAuthor().getName();
        this.authorID = message.getAuthor().getId();
        this.messageID = message.getId();
        this.content = message.getContentRaw();
        this.tts = message.isTTS();
        this.embeds = message.getEmbeds();
        this.messageReference = message.getMessageReference();
        this.stickerIDs = message.getStickers();
        this.attachments = message.getAttachments();
    }

    public CachedMessage(String authorName, String authorID, String messageID, String content, boolean tts, List<MessageEmbed> embeds, MessageReference messageReference, List<StickerItem> stickerIDs, List<Message.Attachment> attachments) {
        this.authorName = authorName;
        this.authorID = authorID;
        this.messageID = messageID;
        this.content = content;
        this.tts = tts;
        this.embeds = embeds;
        this.messageReference = messageReference;
        this.stickerIDs = stickerIDs;
        this.attachments = attachments;
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

    public boolean isTts() {
        return tts;
    }

    public void setTts(boolean tts) {
        this.tts = tts;
    }

    public List<MessageEmbed> getEmbeds() {
        return embeds;
    }

    public void setEmbeds(List<MessageEmbed> embeds) {
        this.embeds = embeds;
    }

    public MessageReference getMessageReference() {
        return messageReference;
    }

    public void setMessageReference(MessageReference messageReference) {
        this.messageReference = messageReference;
    }

    public List<StickerItem> getStickerIDs() {
        return stickerIDs;
    }

    public void setStickerIDs(List<StickerItem> stickerIDs) {
        this.stickerIDs = stickerIDs;
    }

    public List<Message.Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Message.Attachment> attachments) {
        this.attachments = attachments;
    }
}
