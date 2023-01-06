package rs.biljnaapotekasvstefan.ordertrack.scrape.engine;


import jakarta.annotation.PreDestroy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.biljnaapotekasvstefan.ordertrack.config.ScrapeConfig;
import rs.biljnaapotekasvstefan.ordertrack.scrape.loader.PageLoader;

import io.github.bonigarcia.wdm.WebDriverManager;


import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class ScrapeLoader {

    private final WebDriver webDriver;
    private final String firstTab;
    //private final ScrapeConfig scrapeConfig;

   //public ScrapeLoader(ScrapeConfig scrapeConfig) throws MalformedURLException {
    //    this.scrapeConfig = scrapeConfig;
    public ScrapeLoader() throws MalformedURLException {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("disable-infobars");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        WebDriverManager.chromedriver().setup();
        //URL remote = new URL("http://chrome:4444/wd/hub");
        //this.webDriver = new RemoteWebDriver(remote, chromeOptions);
        this.webDriver = new ChromeDriver(chromeOptions);
        /*FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setHeadless(true);
        firefoxOptions.addArguments("--disable-extensions");
        firefoxOptions.addArguments("--disable-gpu");
        firefoxOptions.addArguments("--no-sandbox");
        firefoxOptions.addArguments("--disable-dev-shm-usage");
        WebDriverManager.firefoxdriver().setup();
        this.webDriver = new FirefoxDriver(firefoxOptions);*/
        this.firstTab = this.webDriver.getWindowHandle();
    }

    public String loadAndGetPageContent(URL url, PageLoader pageLoader) throws InterruptedException {
        //open new tab
        this.webDriver.switchTo().newWindow(WindowType.TAB);
        //load page
        String pageContent = pageLoader.loadPage(url, this.webDriver);

        //close tab
        this.webDriver.close();
        //return to first tab
        this.webDriver.switchTo().window(firstTab);

        return pageContent;
    }

    @PreDestroy
    public void destroy() {
        this.webDriver.quit();
    }
}
