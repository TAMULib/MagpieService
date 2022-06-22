package edu.tamu.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.tamu.weaver.auth.model.Credentials;

public class ShibTest extends AbstractModelTest {

    @Test
    public void testCreateShib() {
        Credentials shib = new Credentials(aggieJackToken);
        assertEquals("Daniels", shib.getLastName(), "Last name did not match.");
        assertEquals("Jack", shib.getFirstName(), "First name did not match.");
        assertEquals("aggiejack", shib.getNetid(), "Netid did not match.");
        assertEquals("123456789", shib.getUin(), "UIN did not match.");
        assertEquals(String.valueOf(timestamp), shib.getExp(), "Expiration did not match.");
        assertEquals("aggiejack@tamu.edu", shib.getEmail(), "Email did not match.");
    }

}
