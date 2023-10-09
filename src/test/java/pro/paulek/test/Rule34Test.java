package pro.paulek.test;

import jakarta.xml.bind.JAXBException;
import org.junit.Test;
import org.xml.sax.SAXException;
import pro.paulek.objects.react.Rule34Posts;
import pro.paulek.util.RuleUtil;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static junit.framework.TestCase.fail;

public class Rule34Test {
    @Test
    public void testGettingURL() throws IOException, JAXBException, ParserConfigurationException, SAXException {
        String url = RuleUtil.Rule34Url(10, false, "jelly");
        System.out.println(url);

        if (url == null || url.isEmpty()) fail();

        String response = RuleUtil.Rule34Response(url);
        System.out.println(response);

        if (response == null || response.isEmpty()) fail();

        Rule34Posts posts = RuleUtil.UnmarshallRule34Post(response);
        System.out.println(posts.getCount());

        if (posts.getAllTags().size() == 0) {
            fail();
        }

        System.out.println(posts.getAllTags().get(0).toString());
    }
}
