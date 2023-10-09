package pro.paulek.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pro.paulek.objects.react.Rule34Posts;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class RuleUtil {

    private static final String RULE34_URL = "http://rule34.paheal.net/api/danbooru/find_posts/index.xml";

    private static final Random randomizer = new Random();

    public static String Rule34Url(int limit, boolean random, String... tags) {
        if (random) limit = 1;

        StringBuilder url = new StringBuilder();
        url.append(RULE34_URL);

        if (!random) {
            url.append("?tags=");
            url.append(String.join("%20", tags));
        }

        if(random) {
            url.append("?id=");
            url.append(String.valueOf(randomizer.nextInt(1, 5410582)));
        }
        url.append("&limit=");
        url.append(String.valueOf(limit));

        return url.toString();
    }

    public static String Rule34Response(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null;) {
                response.append(line);
            }
        }

        return response.toString();
    }

    public static Rule34Posts UnmarshallRule34Post(String response) throws SAXException, ParserConfigurationException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Rule34Posts.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        InputSource inputSource = new InputSource( new StringReader(response));
        return (Rule34Posts) unmarshaller.unmarshal(inputSource);
    }
}
