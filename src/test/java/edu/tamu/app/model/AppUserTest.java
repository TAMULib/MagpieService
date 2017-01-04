package edu.tamu.app.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.annotations.Order;
import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.app.runner.OrderedRunner;

@WebAppConfiguration
@ActiveProfiles({"test"})
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
public class AppUserTest {

    @Autowired
    private AppUserRepo userRepo;

    private Long uin = 123456789L;

    private AppUser testUser1 = new AppUser(uin);

    private AppUser testUser2 = new AppUser(uin);

    @Before
    public void setUp() {
        Assert.assertEquals("User repository is not empty.", 0, userRepo.findAll().size());
    }

    @Test
    @Order(1)
    public void testSaveUser() {
        userRepo.save(testUser1);
        Assert.assertEquals("Test user was not saved.", testUser1.getUin(), userRepo.findByUin(uin).getUin());
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
        Assert.assertEquals("User repository is empty.", 1, userRepo.findAll().size());
        AppUser assertUser = userRepo.findByUin(uin);
        Assert.assertEquals("Test User was not found.", assertUser.getUin(), testUser1.getUin());
    }

    @Test
    @Order(4)
    public void testDeleteUser() {
        userRepo.save(testUser1);
        Assert.assertEquals("User repository is empty.", 1, userRepo.findAll().size());
        userRepo.delete(testUser1);
        Assert.assertNull("Test User was not removed.", userRepo.findByUin(uin));
    }

    @After
    public void cleanUp() {
        userRepo.deleteAll();
    }

}
