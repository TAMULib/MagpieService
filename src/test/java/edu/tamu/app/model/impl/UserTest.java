package edu.tamu.app.model.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
	
	private UserImpl testUser1 = new UserImpl(Long.parseLong("123456789"));
	
	private UserImpl testUser2 = new UserImpl(Long.parseLong("123456789"));
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void saveUser() {
		Assert.assertEquals("User repository is not empty.", userRepo.findAll().size(), 0);
		userRepo.save(testUser1);
		Assert.assertEquals("User repository does not have saved user.", userRepo.findAll().size(), 1);
	}
	
	@Test
	public void duplicate() {
		userRepo.save(testUser2);
		Assert.assertEquals("Duplicate uin was added.", userRepo.findAll().size(), 1);
	}
	
	@Test
	public void findUser() {
		UserImpl assertUser = userRepo.getUserByUin(Long.parseLong("123456789"));
		Assert.assertEquals("Test User was not added.", assertUser.getUin(), testUser1.getUin());
	}
	
	@Test
	public void deleteUser() {
		userRepo.delete(testUser1);
		Assert.assertEquals("Test User was not removed.", userRepo.findAll().size(), 0);
	}
	
	@After
	public void cleanUp() {
		
	}
	
}
