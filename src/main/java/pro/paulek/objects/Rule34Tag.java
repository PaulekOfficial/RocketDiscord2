package pro.paulek.objects;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "tag")
@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(propOrder = { "id", "md5", "file_name", "file_url", "height", "width", "preview_url", "preview_height", "preview_width", "rating", "date", "tags", "source", "score", "author"})
public class Rule34Tag {
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String md5;
    @XmlAttribute(name = "file_name")
    private String fileName;
    @XmlAttribute(name = "file_url")
    private String fileUrl;
    @XmlAttribute
    private String height;
    @XmlAttribute
    private String width;
    @XmlAttribute(name = "preview_url")
    private String previewUrl;
    @XmlAttribute(name = "preview_height")
    private String previewHeight;
    @XmlAttribute(name = "preview_width")
    private String previewWidth;
    @XmlAttribute
    private String rating;
    @XmlAttribute
    private String date;
    @XmlAttribute
    private String tags;
    @XmlAttribute
    private String source;
    @XmlAttribute
    private String score;
    @XmlAttribute
    private String author;

    public Rule34Tag() {
    }

    public Rule34Tag(String id, String md5, String fileName, String fileUrl, String height, String width, String previewUrl, String previewHeight, String previewWidth, String rating, String date, String tags, String source, String score, String author) {
        this.id = id;
        this.md5 = md5;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.height = height;
        this.width = width;
        this.previewUrl = previewUrl;
        this.previewHeight = previewHeight;
        this.previewWidth = previewWidth;
        this.rating = rating;
        this.date = date;
        this.tags = tags;
        this.source = source;
        this.score = score;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getPreviewHeight() {
        return previewHeight;
    }

    public void setPreviewHeight(String previewHeight) {
        this.previewHeight = previewHeight;
    }

    public String getPreviewWidth() {
        return previewWidth;
    }

    public void setPreviewWidth(String previewWidth) {
        this.previewWidth = previewWidth;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Rule34Tag{" +
                "id='" + id + '\'' +
                ", md5='" + md5 + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", height='" + height + '\'' +
                ", width='" + width + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                ", previewHeight='" + previewHeight + '\'' +
                ", previewWidth='" + previewWidth + '\'' +
                ", rating='" + rating + '\'' +
                ", date='" + date + '\'' +
                ", tags='" + tags + '\'' +
                ", source='" + source + '\'' +
                ", score='" + score + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
