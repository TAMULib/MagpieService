package edu.tamu.app.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

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
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.controller.interceptor.AppRestInterceptor;
import edu.tamu.app.controller.interceptor.AppStompInterceptor;
import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.app.model.repo.ControlledVocabularyRepo;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;
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
	protected ApplicationContext applicationContext;

	@Mock
	protected AppUserRepo userRepo;

	@Mock
	protected ControlledVocabularyRepo controlledVocabularyRepo;

	@Mock
	protected DocumentRepo documentRepo;

	@Mock
	protected FieldProfileRepo projectFieldProfileRepo;

	@Mock
	protected MetadataFieldGroupRepo metadataFieldGroupRepo;

	@Mock
	protected MetadataFieldLabelRepo metadataFieldLabelRepo;

	@Mock
	protected MetadataFieldValueRepo metadataFieldValueRepo;

	@Mock
	protected ProjectRepo projectRepo;

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

	@InjectMocks
	protected AppStompInterceptor appStompInterceptor;

	@InjectMocks
	protected AppRestInterceptor appRestInterceptor;

	protected static String[] mockAdmins = {TEST_USER1.getUin().toString(), TEST_USER2.getUin().toString() };
	protected static String[] mockManagers = {TEST_USER3.getUin().toString()};

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		//app user
		when(userRepo.findAll()).thenReturn(mockAppUserList);

		when(userRepo.save(any (AppUser.class))).then( new Answer<AppUser>() {
			@Override
			public AppUser answer(InvocationOnMock invocation) throws Throwable {
				return saveAppUser((AppUser)invocation.getArguments()[0]);
			}
		});

		when(userRepo.findByUin(any (Long.class))).then( new Answer<AppUser>() {
			@Override
			public AppUser answer(InvocationOnMock invocation) throws Throwable {
				return findUserByUin((Long)invocation.getArguments()[0]);
			}
		});

		when(userRepo.create(any (Long.class), any (String.class), any (String.class), any (String.class))).then(new Answer<AppUser>() {
			@Override
			public AppUser answer(InvocationOnMock invocation) throws Throwable {
				return createAppUser((Long) invocation.getArguments()[0], (String) invocation.getArguments()[1], (String) invocation.getArguments()[2], (String) invocation.getArguments()[3] );
			}
		});

		when(projectRepo.findAll()).thenReturn(mockProjectList);

		when(projectRepo.save(any (Project.class))).then( new Answer<Project>() {
			@Override
			public Project answer(InvocationOnMock invocation) throws Throwable {
				return saveProject( (Project) invocation.getArguments()[0]);
			}
		});

		when(projectRepo.findByName(any (String.class))).then(new Answer<Project>() {
			@Override
			public Project answer(InvocationOnMock invocation) throws Throwable {
				return findProjectByName( (String) invocation.getArguments()[0]);
			}
		});

		//metadatafieldGroup
		when(metadataFieldGroupRepo.findAll()).thenReturn(mockMetadataFieldGroupList);

		aggieJackToken = new HashMap<String, String>();
		aggieJackToken.put("lastName","Daniels");
		aggieJackToken.put("firstName","Jack");
		aggieJackToken.put("netid","aggiejack");
		aggieJackToken.put("uin","123456789");
		aggieJackToken.put("exp",null);
		aggieJackToken.put("email","aggiejack@tamu.edu");

		ReflectionTestUtils.setField(appRestInterceptor, "admins", mockAdmins);
		ReflectionTestUtils.setField(appStompInterceptor, "admins", mockAdmins);
		ReflectionTestUtils.setField(appStompInterceptor, "managers", mockManagers);
	}

	@After
	public void cleanUp () {
		response = null;
		credentials = null;
		testCredentials = null;
		controlledVocabularyRepo.deleteAll();
		projectFieldProfileRepo.deleteAll();
		metadataFieldValueRepo.deleteAll();
		metadataFieldLabelRepo.deleteAll();
		metadataFieldGroupRepo.deleteAll();
		documentRepo.deleteAll();
		projectRepo.deleteAll();
		userRepo.deleteAll();
	}
}
