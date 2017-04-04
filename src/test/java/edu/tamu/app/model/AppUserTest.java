package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.annotations.Order;

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
        Assert.assertNull("Test User was not removed.", userRepo.findByUin(uin));
    }
}
