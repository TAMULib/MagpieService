package edu.tamu.app.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.annotations.Order;
import edu.tamu.app.runner.OrderedRunner;
import edu.tamu.framework.model.Credentials;

@WebAppConfiguration
@ActiveProfiles({ "test" })
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
public class ShibTest {

    private Map<String, String> aggieJackToken;

    private long timestamp = new Date().getTime() + (5 * 60 * 1000);

    @Before
    public void setup() {
        aggieJackToken = new HashMap<>();
        aggieJackToken.put("lastName", "Daniels");
        aggieJackToken.put("firstName", "Jack");
        aggieJackToken.put("netid", "aggiejack");
        aggieJackToken.put("uin", "123456789");
        aggieJackToken.put("exp", String.valueOf(timestamp));
        aggieJackToken.put("email", "aggiejack@tamu.edu");
    }

    @Test
    @Order(1)
    public void testCreateShib() {

        Credentials shib = new Credentials(aggieJackToken);

        Assert.assertEquals("Last name did not match.", "Daniels", shib.getLastName());
        Assert.assertEquals("First name did not match.", "Jack", shib.getFirstName());

        // TODO: The framework credential object does not have this method. It
        // can be found in the commit history of this application
        // Assert.assertEquals("Netid did not match.", "aggiejack", shib.getNetId());

        Assert.assertEquals("UIN did not match.", "123456789", shib.getUin());
        Assert.assertEquals("Expiration did not match.", String.valueOf(timestamp), shib.getExp());
        Assert.assertEquals("Email did not match.", "aggiejack@tamu.edu", shib.getEmail());
    }

}
