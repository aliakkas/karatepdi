package uk.broadoakdata.pdi;

import com.intuit.karate.FileUtils;
import com.intuit.karate.driver.chrome.Chrome;
import java.io.File;
import java.util.Collections;

import static com.intuit.karate.driver.chrome.Chrome.*;

public class TestPdiHome {

    public static void main(String[] args) {
        Chrome chrome = startHeadless();
        chrome.setUrl("http://127.0.0.1:9999/login");
        byte[] bytes = chrome.pdf(Collections.EMPTY_MAP);
        FileUtils.writeToFile(new File("target/pdi.pdf"), bytes);
        bytes = chrome.screenshot();
        FileUtils.writeToFile(new File("target/pdi.png"), bytes);
        chrome.quit();
    }

}