package pro.paulek.objects;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "posts")
public class Rule34Posts {
    private String Count;
    private String Offset;
    @XmlElement(name = "tag")
    private List<Rule34Tag> tags;

    public Rule34Posts() {
    }

    public Rule34Posts(String count, String offset, List<Rule34Tag> tags) {
        Count = count;
        Offset = offset;
        this.tags = tags;
    }

    public String getCount() {
        return Count;
    }

    @XmlAttribute
    public void setCount(String count) {
        Count = count;
    }

    public String getOffset() {
        return Offset;
    }

    @XmlAttribute
    public void setOffset(String offset) {
        Offset = offset;
    }

    public List<Rule34Tag> getAllTags() {
        return tags;
    }


    public void addTag(Rule34Tag tag) {
        tags.add(tag);
    }

    public void setTags(List<Rule34Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Rule34Posts{" +
                "Count='" + Count + '\'' +
                ", Offset='" + Offset + '\'' +
                ", tags=" + tags +
                '}';
    }
}
