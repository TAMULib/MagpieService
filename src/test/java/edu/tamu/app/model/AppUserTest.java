package edu.tamu.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class AppUserTest extends AbstractModelTest {

    @Test
    public void testSaveUser() {
        userRepo.save(testUser1);
        assertEquals(testUser1.getUsername(), userRepo.findByUsername(uin).get().getUsername(), "Test user was not saved.");
    }

    @Test
    public void testDuplicate() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepo.save(testUser1);
            userRepo.save(testUser2);
        });
    }

    @Test
    public void testFindUser() {
        userRepo.save(testUser1);
        assertEquals(1, userRepo.findAll().size(), "User repository is empty.");
        Optional<AppUser> assertUser = userRepo.findByUsername(uin);
        assertEquals(assertUser.get().getUsername(), testUser1.getUsername(), "Test User was not found.");
    }

    @Test
    public void testDeleteUser() {
        testUser1 = userRepo.save(testUser1);
        assertEquals(1, userRepo.findAll().size(), "User repository is empty.");
        userRepo.delete(testUser1);
        assertEquals(0, userRepo.findAll().size(),  "Test User was not removed.");
    }

    @Test
    public void testAppUserGetters() throws Exception {
        userRepo.save(testUser3);
        AppUser assertUser = userRepo.findByUsername(testUser3.getUsername()).get();
        assertEquals(assertUser.getUsername(), testUser3.getUsername(), "Test User 3  was not found.");
        assertUser.setFirstName("Another Jane");
        assertUser.setLastName("Another Daniel");
        assertUser.setRole(Role.ROLE_USER);
        userRepo.save(testUser3);
        assertUser = userRepo.findByUsername(testUser3.getUsername()).get();
        assertEquals(assertUser.getFirstName(), testUser3.getFirstName(), "Test User 3  firstName was not modified.");
        assertEquals(assertUser.getLastName(), testUser3.getLastName(), "Test User 3  lastName was not modified.");
        assertEquals(assertUser.getRole(), testUser3.getRole(), "Test User 3  role was not modified.");
    }

}
