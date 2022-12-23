package rs.biljnaapotekasvstefan.ordertrack.scrape.loader;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.springframework.stereotype.Component;

import java.net.URL;
@Component
public class PageLoaderImpl implements PageLoader{
    @Override
    public String loadPage(URL url, WebDriver webDriver) throws InterruptedException {

            webDriver.get(url.toString());
            Thread.sleep(4000);
            return webDriver.getPageSource();

    }
}
