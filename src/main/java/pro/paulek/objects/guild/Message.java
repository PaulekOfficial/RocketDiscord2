package pro.paulek.objects.guild;

import java.util.Objects;

public class Message {
    private int id;
    private String authorName;
    private String authorID;
    private String messageID;
    private String content;
    private MessageAction action;

    public Message(String authorName, String authorID, String messageID, String content, MessageAction action) {
        this.authorName = authorName;
        this.authorID = authorID;
        this.messageID = messageID;
        this.content = content;
        this.action = action;
    }

    public Message(int id, String authorName, String authorID, String messageID, String content, MessageAction action) {
        this.id = id;
        this.authorName = authorName;
        this.authorID = authorID;
        this.messageID = messageID;
        this.content = content;
        this.action = action;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id && Objects.equals(authorName, message.authorName) && Objects.equals(authorID, message.authorID) && Objects.equals(messageID, message.messageID) && Objects.equals(content, message.content) && action == message.action;
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
