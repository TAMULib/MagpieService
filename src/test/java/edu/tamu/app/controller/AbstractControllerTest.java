package edu.tamu.app.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.when;

import java.awt.print.Pageable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.Resource;
import edu.tamu.app.model.Role;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.ResourceRepo;
import edu.tamu.app.service.ProjectFactory;
import edu.tamu.app.service.SyncService;
import edu.tamu.app.service.exporter.DspaceCsvExporter;
import edu.tamu.app.service.exporter.SpotlightCsvExporter;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.utilities.FileSystemUtility;
import edu.tamu.weaver.response.ApiResponse;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public abstract class AbstractControllerTest extends MockData {

    protected static ApiResponse response;

    @Mock
    protected DocumentRepo documentRepo;
    
    @Mock
    protected ResourceRepo resourceRepo;

    @Mock
    protected ProjectRepo projectRepo;

    @Mock
    protected FieldProfileRepo fieldProfileRepo;

    @Mock
    protected MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Mock
    protected AppUserRepo userRepo;

    @Mock
    protected MetadataFieldGroupRepo metadataFieldGroupRepo;

    @Spy
    protected static FileSystemUtility fileSystemUtility;

    @Mock
    protected DspaceCsvExporter dspaceCSVExporter;

    @Mock
    protected SpotlightCsvExporter spotlightExporter;

    @Mock
    protected ProjectFactory projectsService;

    @Mock
    protected MagpieServiceRegistry projectServiceRegistry;

    @Mock
    protected SyncService syncService;

    @InjectMocks
    protected AdminController adminController;

    @InjectMocks
    protected ControlledVocabularyController controlledVocabularyController;

    @InjectMocks
    protected DocumentController documentController;

    @InjectMocks
    protected MetadataController metadataController;

    @InjectMocks
    protected ExportController exportController;

    @InjectMocks
    protected ProjectController projectController;

    @InjectMocks
    protected ProjectSuggestorController suggestionController;

    @InjectMocks
    protected UserController userController;

    @Spy
    protected ObjectMapper objectMapper;

    @Mock
    protected SimpMessagingTemplate simpMessagingTemplate;

    protected static String[] mockAdmins = { TEST_USER1.getUsername().toString(), TEST_USER2.getUsername().toString() };

    protected static String[] mockManagers = { TEST_USER3.getUsername().toString() };

    @SuppressWarnings({ "unchecked" })
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        credentials.setEmail("aggieJane@tamu.edu");
        credentials.setFirstName(TEST_USER1.getFirstName());
        credentials.setLastName(TEST_USER1.getLastName());
        credentials.setRole(Role.ROLE_ADMIN.toString());
        credentials.setUin(TEST_USER1.getUsername().toString());
        credentials.setNetid("aggieJane@tamu.edu");

        // app user
        when(userRepo.findAll()).thenReturn(mockUserList);

        when(userRepo.save(any(AppUser.class))).then(new Answer<AppUser>() {
            @Override
            public AppUser answer(InvocationOnMock invocation) throws Throwable {
                return saveAppUser((AppUser) invocation.getArguments()[0]);
            }
        });

        when(userRepo.update(any(AppUser.class))).then(new Answer<AppUser>() {
            @Override
            public AppUser answer(InvocationOnMock invocation) throws Throwable {
                return saveAppUser((AppUser) invocation.getArguments()[0]);
            }
        });

        when(userRepo.create(any(String.class), any(String.class), any(String.class), any(String.class))).then(new Answer<AppUser>() {
            @Override
            public AppUser answer(InvocationOnMock invocation) throws Throwable {
                return createUser((String) invocation.getArguments()[0], (String) invocation.getArguments()[1], (String) invocation.getArguments()[1], (String) invocation.getArguments()[2]);
            }
        });

        // document
        when(documentRepo.findAll()).thenReturn(mockDocumentList);

        when(documentRepo.save(any(Document.class))).then(new Answer<Document>() {
            @Override
            public Document answer(InvocationOnMock invocation) throws Throwable {
                return saveDocument((Document) invocation.getArguments()[0]);
            }
        });

        when(documentRepo.update(any(Document.class))).then(new Answer<Document>() {
            @Override
            public Document answer(InvocationOnMock invocation) throws Throwable {
                return saveDocument((Document) invocation.getArguments()[0]);
            }
        });

        when(documentRepo.findByProjectNameAndName(any(String.class), any(String.class))).then(new Answer<Document>() {
            @Override
            public Document answer(InvocationOnMock invocation) throws Throwable {
                return findDocumentByProjectNameandName((String) invocation.getArguments()[0], (String) invocation.getArguments()[1]);
            }
        });

        // TODO
        when(documentRepo.pageableDynamicDocumentQuery((Map<String, String[]>) any(Map.class), (org.springframework.data.domain.Pageable) any(Pageable.class))).then(new Answer<Page<Document>>() {
            @Override
            public Page<Document> answer(InvocationOnMock invocation) throws Throwable {
                return (Page<Document>) TEST_DOCUMENT1;
            }
        });

        when(documentRepo.findByStatus(any(String.class))).thenReturn(mockDocumentList);

        // project
        when(projectRepo.findAll()).thenReturn(mockProjectList);

        when(projectRepo.create(any(String.class), any(IngestType.class), any(Boolean.class), (List<ProjectRepository>) any(List.class), any(List.class), any(List.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return TEST_PROJECT1;
            }
        });

        when(projectRepo.findOne(any(Long.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return findProjectById((Long) invocation.getArguments()[0]);
            }
        });

        when(projectRepo.read(any(Long.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return findProjectById((Long) invocation.getArguments()[0]);
            }
        });

        when(projectRepo.findByName(any(String.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return findProjectbyName((String) invocation.getArguments()[0]);
            }
        });

        when(projectRepo.save(any(Project.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return saveProject((Project) invocation.getArguments()[0]);
            }
        });

        when(projectRepo.update(any(Project.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return updateProject((Project) invocation.getArguments()[0]);
            }
        });

        when(projectsService.getOrCreateProject(any(File.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return TEST_PROJECT1;
            }
        });
        
        // resource
        when(resourceRepo.findAllByDocumentProjectNameAndDocumentName(any(String.class), any(String.class))).thenReturn(new ArrayList<Resource>());

        // Field Profiles

        //for cases when we expect the FieldProfile not to be found
        when(fieldProfileRepo.findByProjectAndGloss(refEq(TEST_PROJECT1), any(String.class))).then(new Answer<FieldProfile>() {
           @Override
           public FieldProfile answer(InvocationOnMock invocation) throws Throwable {
               return null;
           }
        });

        when(fieldProfileRepo.save(any(FieldProfile.class))).then(new Answer<FieldProfile>() {
            @Override
            public FieldProfile answer(InvocationOnMock invocation) throws Throwable {
                return TEST_PROFILE1;
            }
         });

        // Metadata Field Labels

        when(metadataFieldLabelRepo.create(any(String.class), any(FieldProfile.class))).then(new Answer<MetadataFieldLabel>() {
            @Override
            public MetadataFieldLabel answer(InvocationOnMock invocation) throws Throwable {
                return TEST_META_LABEL;
            }
        });

        when(metadataFieldLabelRepo.findByNameAndProfile(any(String.class),any(FieldProfile.class))).then(new Answer<MetadataFieldLabel>() {
            @Override
            public MetadataFieldLabel answer(InvocationOnMock invocation) throws Throwable {
                return TEST_META_LABEL;
            }
         });

        // metdataHeader formats
        mockSpotlightExportedMetadataHeaders.add(0, "url");
        mockSpotlightExportedMetadataHeaders.add(1, "spotlight exported metadataheader 1");
        mockSpotlightExportedMetadataHeaders.add(2, "spotlight exported metadataheader 1");

        when(spotlightExporter.extractMetadataFields(any(String.class))).thenReturn(mockSpotlightExportedMetadataHeaders);

        mockSpotlightMetdata.add(mockSpotlightExportedMetadataHeaders);
        when(spotlightExporter.extractMetadata(any(Project.class))).thenReturn(mockSpotlightMetdata);

        mockDspaceCSVExportedMetadataHeaders.add("BUNDLE:ORIGINAL");
        mockDspaceCSVExportedMetadataHeaders.add("dspaceCSVExportedMetadataHeader 1");
        mockDspaceCSVExportedMetadataHeaders.add("dspaceCSVExportedMetadataHeader 2");
        mockDspaceCSVExportedMetadata.add(mockDspaceCSVExportedMetadataHeaders);
        when(dspaceCSVExporter.extractMetadataFields(any(String.class))).thenReturn(mockDspaceCSVExportedMetadataHeaders);
        when(dspaceCSVExporter.extractMetadata(any(Project.class))).thenReturn(mockDspaceCSVExportedMetadata);

        when(metadataFieldGroupRepo.findAll()).thenReturn(mockMetadataFieldGroupList);

    }

    @After
    public void cleanUp() {
        response = null;
        documentRepo.deleteAll();
        metadataFieldGroupRepo.deleteAll();
        projectRepo.deleteAll();
        userRepo.deleteAll();
    }
}
