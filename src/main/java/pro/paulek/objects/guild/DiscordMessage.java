package pro.paulek.objects.guild;

import java.time.Instant;
import java.util.Objects;

public class DiscordMessage {
    private int id;
    private String authorName;
    private String authorID;
    private String messageID;
    private String content;
    private MessageAction action;
    private Instant createdAt;

    public DiscordMessage(String authorName, String authorID, String messageID, String content, MessageAction action, Instant createdAt) {
        this.authorName = authorName;
        this.authorID = authorID;
        this.messageID = messageID;
        this.content = content;
        this.action = action;
        this.createdAt = createdAt;
    }

    public DiscordMessage(int id, String authorName, String authorID, String messageID, String content, MessageAction action, Instant createdAt) {
        this.id = id;
        this.authorName = authorName;
        this.authorID = authorID;
        this.messageID = messageID;
        this.content = content;
        this.action = action;
        this.createdAt = createdAt;
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

    public MessageAction getAction() {
        return action;
    }

    public void setAction(MessageAction action) {
        this.action = action;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscordMessage discordMessage = (DiscordMessage) o;
        return id == discordMessage.id && Objects.equals(authorName, discordMessage.authorName) && Objects.equals(authorID, discordMessage.authorID) && Objects.equals(messageID, discordMessage.messageID) && Objects.equals(content, discordMessage.content) && action == discordMessage.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authorName, authorID, messageID, content, action);
    }

    public enum MessageAction {
        NEW,
        EDITED,
        DELETED;
    }
}
