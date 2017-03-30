package edu.tamu.app.controller;

import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.app.service.DocumentPushService;
import edu.tamu.app.service.ProjectsService;
import edu.tamu.app.service.SyncService;
import edu.tamu.app.service.VoyagerService;
import edu.tamu.framework.model.ApiResponse;

@ActiveProfiles("test")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
public abstract class AbstractControllerTest extends MockData {

	protected static ApiResponse response;

	@Mock
	protected AppUserRepo userRepo;

	@Spy
	protected ObjectMapper objectMapper;

	@Mock
	protected SyncService syncService;

	@Mock
	protected ProjectsService projectsService;

	@Mock
	protected ResourceLoader resourceLoader;

	@Mock
	protected DocumentPushService documentPushService;

	@Mock
	protected VoyagerService voyagerService;

	@Mock
	protected SimpMessagingTemplate simpMessagingTemplate;

	@InjectMocks
	protected AdminController adminController;

	@InjectMocks
	protected ControlledVocabularyController controlledVocabularyController;

	@InjectMocks
	protected DocumentController documentController;

	@InjectMocks
	protected MetadataController metadataController;

	@InjectMocks
	protected ProjectController projectController;

	@InjectMocks
	protected UserController userController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(userRepo.findAll()).thenReturn(mockAppUserList);

		when(userRepo.save(any (AppUser.class))).then( new Answer<AppUser>() {
			@Override
			public AppUser answer(InvocationOnMock invocation) throws Throwable {
				return saveAppUser((AppUser)invocation.getArguments()[0]);
			}
		});

		aggieJackToken = new HashMap<String, String>();
		aggieJackToken.put("lastName","Daniels");
		aggieJackToken.put("firstName","Jack");
		aggieJackToken.put("netid","aggiejack");
		aggieJackToken.put("uin","123456789");
		aggieJackToken.put("exp",null);
		aggieJackToken.put("email","aggiejack@tamu.edu");

		grantorList.add("A & M");
		grantorList.add("TAMU");

		degreeList.add("PhD");
		degreeList.add("ME");
		degreeList.add("MS");

		cvMap.put("grantor", grantorList);
		cvMap.put("degrees", degreeList);

//		when(objectMapper.readValue(any (String.class), any (TypeReference.class))).then(new Answer<Map<String, Object>>(){
//			@Override
//			public Map<String, Object> answer(InvocationOnMock invocation) throws Throwable {
//				return cvMap;
//			}
//		});
	}

	@After
	public void cleanUp () {
		response = null;
	}
}
