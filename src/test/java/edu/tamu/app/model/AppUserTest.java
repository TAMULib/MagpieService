package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Optional;

import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import edu.tamu.app.model.Role;

public class AppUserTest extends AbstractModelTest {

    @Test
    public void testSaveUser() {
        userRepo.save(testUser1);
        assertEquals("Test user was not saved.", testUser1.getUsername(), userRepo.findByUsername(uin).get().getUsername());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDuplicate() {
        userRepo.save(testUser1);
        userRepo.save(testUser2);
    }

    @Test
    public void testFindUser() {
        userRepo.save(testUser1);
        assertEquals("User repository is empty.", 1, userRepo.findAll().size());
        Optional<AppUser> assertUser = userRepo.findByUsername(uin);
        assertEquals("Test User was not found.", assertUser.get().getUsername(), testUser1.getUsername());
    }

    @Test
    public void testDeleteUser() {
        testUser1 = userRepo.save(testUser1);
        Long id = testUser1.getId();
        assertEquals("User repository is empty.", 1, userRepo.findAll().size());
        userRepo.delete(testUser1);
        assertNull("Test User was not removed.", userRepo.findOne(id));
    }

    @Test
    public void testAppUserGetters() throws Exception {
        userRepo.save(testUser3);
        AppUser assertUser = userRepo.findByUsername(testUser3.getUsername()).get();
        assertEquals("Test User 3  was not found.", assertUser.getUsername(), testUser3.getUsername());
        assertUser.setFirstName("Another Jane");
        assertUser.setLastName("Another Daniel");
        assertUser.setRole(Role.ROLE_USER);
        userRepo.save(testUser3);
        assertUser = userRepo.findByUsername(testUser3.getUsername()).get();
        assertEquals("Test User 3  firstName was not modified.", assertUser.getFirstName(), testUser3.getFirstName());
        assertEquals("Test User 3  lastName was not modified.", assertUser.getLastName(), testUser3.getLastName());
        assertEquals("Test User 3  role was not modified.", assertUser.getRole(), testUser3.getRole());
    }

}
