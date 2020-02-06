package uk.broadoakdata.pdi;

import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.chrome.Chrome;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ali akkas
 */
public class ChromeRunner {

    private static final Logger logger = LoggerFactory.getLogger(ChromeRunner.class);

    @Test
    public void testChrome() throws Exception {
        Driver driver = Chrome.start();
        driver.setUrl("https://github.com/login");
        driver.input("#login_field", "hello");
        driver.input("#password", "world");
        driver.click("input[name=commit]");
        String html = driver.html("#js-flash-container");
        assertTrue(html.contains("Incorrect username or password."));
        driver.setUrl("https://google.com");
        driver.input("input[name=q]", "karate dsl");
        driver.click("input[name=btnI]");
        assertEquals("https://github.com/intuit/karate", driver.getUrl());
        driver.quit();
    }

}