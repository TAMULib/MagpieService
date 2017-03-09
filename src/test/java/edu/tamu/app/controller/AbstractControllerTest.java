package edu.tamu.app.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.service.ProjectsService;
import edu.tamu.app.service.SyncService;

public abstract class AbstractControllerTest extends MockData {

	@Mock
	protected ObjectMapper objectMapper;

	@Mock
	protected SyncService syncService;

	@Mock
	protected ProjectsService projectsService;

	@Mock
	protected ResourceLoader resourceLoader;

	@InjectMocks
	protected AdminController adminController;

	@InjectMocks
	protected ControlledVocabularyController controlledVocabularyController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		grantorList.add("A & M");
		grantorList.add("TAMU");

		degreeList.add("PhD");
		degreeList.add("ME");
		degreeList.add("MS");

		cvMap.put("grantor", grantorList);
		cvMap.put("degrees", degreeList);

		when(objectMapper.readValue(any (String.class), any (TypeReference.class))).then(new Answer<Map<String, Object>>(){
			@Override
			public Map<String, Object> answer(InvocationOnMock invocation) throws Throwable {
				return cvMap;
			}
		});
	}

	@After
	public void cleanUp () {
		
	}
}
