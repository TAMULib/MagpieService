package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;

import edu.tamu.app.annotations.Order;
import edu.tamu.app.enums.AppRole;
import edu.tamu.app.utilities.FileSystemUtility;

public class AppUserTest extends AbstractClass {

    @Test
    @Order(1)
    public void testSaveUser() {
        userRepo.save(testUser1);
        assertEquals("Test user was not saved.", testUser1.getUin(), userRepo.findByUin(uin).getUin());
    }

    @Test(expected = DataIntegrityViolationException.class)
    @Order(2)
    public void testDuplicate() {
        userRepo.save(testUser1);
        userRepo.save(testUser2);
    }

    @Test
    @Order(3)
    public void testFindUser() {
        userRepo.save(testUser1);
        assertEquals("User repository is empty.", 1, userRepo.findAll().size());
        AppUser assertUser = userRepo.findByUin(uin);
        assertEquals("Test User was not found.", assertUser.getUin(), testUser1.getUin());
    }

    @Test
    @Order(4)
    public void testDeleteUser() {
        userRepo.save(testUser1);
        assertEquals("User repository is empty.", 1, userRepo.findAll().size());
        userRepo.delete(testUser1);
        assertNull("Test User was not removed.", userRepo.findByUin(uin));
    }

    @Test
    @Order(5)
    public void testAppUserGetters() throws Exception {
		userRepo.save(testUser3);
		AppUser assertUser = userRepo.findByUin(testUser3.getUin());
		assertEquals("Test User 3  was not found.", assertUser.getUin(), testUser3.getUin());
		assertUser.setFirstName("Another Jane");
		assertUser.setLastName("Another Daniel");
		assertUser.setRole(AppRole.ROLE_USER);
		userRepo.save(testUser3);
		assertUser = userRepo.findByUin(testUser3.getUin());
		assertEquals("Test User 3  firstName was not modified.", assertUser.getFirstName(), testUser3.getFirstName());
		assertEquals("Test User 3  lastName was not modified.", assertUser.getLastName(), testUser3.getLastName());
		assertEquals("Test User 3  role was not modified.", assertUser.getRole(), testUser3.getRole());
    }

}
