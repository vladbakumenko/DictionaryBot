import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("https://sinonim.org/s/%D0%B2%D1%80%D0%B5%D0%BC%D1%8F")
                .userAgent("Chrome/107.0.0.0")
                .referrer("http://www.google.com")
                .get();

        Elements listOfWord = document.select("div.outtable").select("table");

        List<String> synonyms = new ArrayList<>();

        for (Element element : listOfWord.select("a")) {
            if (element.text().equals("https://sinonim.org/")) {
                continue;
            }
            synonyms.add(element.text());
        }

        System.out.println(synonyms);
    }
}
