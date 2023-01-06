package rs.biljnaapotekasvstefan.ordertrack.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

//@Getter
//@Setter
//@Configuration
public class ScrapeConfig {

    @Value("${webdriver.path}")
    private String webDriverPath;

    @PostConstruct
    public void setProperty(){
        System.setProperty("webdriver.chrome.driver", webDriverPath);
    }
}
