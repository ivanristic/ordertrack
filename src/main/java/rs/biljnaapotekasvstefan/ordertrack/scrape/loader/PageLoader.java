package rs.biljnaapotekasvstefan.ordertrack.scrape.loader;

import org.openqa.selenium.WebDriver;

import java.net.URL;

public interface PageLoader {
    String  loadPage(URL url, WebDriver webDriver) throws InterruptedException;
}
