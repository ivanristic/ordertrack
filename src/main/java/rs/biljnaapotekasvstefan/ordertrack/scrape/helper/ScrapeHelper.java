package rs.biljnaapotekasvstefan.ordertrack.scrape.helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

public class ScrapeHelper {

    public static String getElementDataOwn(Element doc, String selector) {
        Element e = getElement(doc, selector);
        if (e == null) return null;
        return e.ownText().trim();
    }

    public static String getElementData(Element doc, String selector){
        Element e = getElement(doc, selector);
        if (e == null) return null;
        return e.text().trim();
    }

    public static String getElementHtml(Element doc, String selector) {
        Element e = getElement(doc, selector);
        if (e == null) return null;
        return e.html().trim();
    }

    public static Element getElement(Element doc, String selector) {
        return getElements(doc, selector).first();
    }

    public static Elements getElements(Element doc, String selector) {
        return doc.select(selector);
    }

    public static String getAttributeValue(Element doc, String selector, String attr) {
        return doc.select(selector).first().attr(attr);
    }

    public static Document getPage(String url) throws Exception {
        try {
            return Jsoup.connect(url)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36")
                    .get();
        } catch(IOException ex) {
            throw new Exception(ex);
        }
    }

    public static Document createDocument(String pageContent) {
        return Jsoup.parse(pageContent);
    }

    public static Document getPageWithWebDriver(String url){
        WebDriver driver = null;
        String html = null;
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        try {
            driver = new ChromeDriver(chromeOptions);
            driver.get(url);
            Thread.sleep(5000);
            html = driver.getPageSource();
        } catch (InterruptedException e){

        } finally {
            if(driver != null)
                driver.quit();
        }
        return Jsoup.parse(html);
    }
}
