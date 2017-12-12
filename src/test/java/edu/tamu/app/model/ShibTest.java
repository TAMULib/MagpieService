package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.tamu.weaver.auth.model.Credentials;

public class ShibTest extends AbstractModelTest {

    @Test
    public void testCreateShib() {
        Credentials shib = new Credentials(aggieJackToken);
        assertEquals("Last name did not match.", "Daniels", shib.getLastName());
        assertEquals("First name did not match.", "Jack", shib.getFirstName());
        assertEquals("Netid did not match.", "aggiejack", shib.getNetid());
        assertEquals("UIN did not match.", "123456789", shib.getUin());
        assertEquals("Expiration did not match.", String.valueOf(timestamp), shib.getExp());
        assertEquals("Email did not match.", "aggiejack@tamu.edu", shib.getEmail());
    }

}
