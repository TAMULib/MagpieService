package edu.tamu.app.model.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

import edu.tamu.app.config.TestDataSourceConfiguration;
import edu.tamu.app.model.repo.UserRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestDataSourceConfiguration.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    					  DirtiesContextTestExecutionListener.class,
    					  TransactionalTestExecutionListener.class,
    					  DbUnitTestExecutionListener.class })
public class UserTest {
	
	@Autowired
	private UserRepo userRepo;
	
	private Long uin = (long) 123456789;
	
	private UserImpl testUser1 = new UserImpl(uin);
	
	private UserImpl testUser2 = new UserImpl(uin);
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void test1SaveUser() {
		Assert.assertEquals("User repository is not empty.", 0, userRepo.findAll().size());
		userRepo.save(testUser1);
		Assert.assertEquals("User repository does not have saved user.", 1, userRepo.findAll().size());
	}
	
	@Test
	public void test2Duplicate() {
		Assert.assertEquals("User repository is not empty.", 0, userRepo.findAll().size());
		userRepo.save(testUser1);
		Assert.assertEquals("User repository is empty.", 1, userRepo.findAll().size());
		userRepo.save(testUser2);
		Assert.assertEquals("Duplicate uin was added.", 1, userRepo.findAll().size());
	}
	
	@Test
	public void test3FindUser() {
		Assert.assertEquals("User repository is not empty.", 0, userRepo.findAll().size());
		userRepo.save(testUser1);
		Assert.assertEquals("User repository is empty.", 1, userRepo.findAll().size());
		UserImpl assertUser = userRepo.getUserByUin(uin);
		Assert.assertEquals("Test User was not added.", assertUser.getUin(), testUser1.getUin());
	}
	
	@Test
	public void test4DeleteUser() {
		Assert.assertEquals("User repository is not empty.", 0, userRepo.findAll().size());
		userRepo.save(testUser1);
		Assert.assertEquals("User repository is empty.", 1, userRepo.findAll().size());
		userRepo.delete(testUser1);
		Assert.assertEquals("Test User was not removed.", 0, userRepo.findAll().size());
	}
	
	@After
	public void cleanUp() {
		userRepo.deleteAll();
	}
	
}
