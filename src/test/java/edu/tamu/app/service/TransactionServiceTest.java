package edu.tamu.app.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.WebServerInit;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebServerInit.class)
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    private String[] tids = { "8e18640e-dadf-11e9-8a34-2a2ae2dbcce4", "8e18679c-dadf-11e9-8a34-2a2ae2dbcce4" };

    @Test
    public void testAdd() {
        transactionService.add(tids[0], Duration.ofHours(1));
        assertEquals(1, transactionService.count());
        assertFalse(transactionService.isAboutToExpire(tids[0]));
    }

    @Test
    public void testRemove() {
        testAdd();
        transactionService.remove(tids[0]);
        assertEquals(0, transactionService.count());
    }

    @Test
    public void testIsAboutToExpire() {
        transactionService.add(tids[0], Duration.ofSeconds(10));
        assertEquals(1, transactionService.count());
        assertTrue(transactionService.isAboutToExpire(tids[0]));
    }

    @Test
    public void testCount() {
        for (String tid : tids) {
            transactionService.add(tid, Duration.ofDays(1));
        }
        assertEquals(tids.length, transactionService.count());
    }

    @Test
    public void testClear() {
        testCount();
        transactionService.clear();
        assertEquals(0, transactionService.count());
    }

    @Test
    public void testExpire() {
        for (String tid : tids) {
            transactionService.add(tid, Duration.ofNanos(1));
        }
        assertEquals(tids.length, transactionService.count());
        transactionService.expire();
        assertEquals(0, transactionService.count());
    }

    @After
    public void clear() {
        transactionService.clear();
    }

}
