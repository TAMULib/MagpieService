package edu.tamu.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.awt.print.Pageable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
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
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
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
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        credentials.setEmail("aggieJane@tamu.edu");
        credentials.setFirstName(TEST_USER1.getFirstName());
        credentials.setLastName(TEST_USER1.getLastName());
        credentials.setRole(Role.ROLE_ADMIN.toString());
        credentials.setUin(TEST_USER1.getUsername().toString());
        credentials.setNetid("aggieJane@tamu.edu");

        // app user
        lenient().when(userRepo.findAll()).thenReturn(mockUserList);

        lenient().when(userRepo.save(any(AppUser.class))).then(new Answer<AppUser>() {
            @Override
            public AppUser answer(InvocationOnMock invocation) throws Throwable {
                return saveAppUser((AppUser) invocation.getArguments()[0]);
            }
        });

        lenient().when(userRepo.update(any(AppUser.class))).then(new Answer<AppUser>() {
            @Override
            public AppUser answer(InvocationOnMock invocation) throws Throwable {
                return saveAppUser((AppUser) invocation.getArguments()[0]);
            }
        });

        lenient().when(userRepo.create(any(String.class), any(String.class), any(String.class), any(String.class))).then(new Answer<AppUser>() {
            @Override
            public AppUser answer(InvocationOnMock invocation) throws Throwable {
                return createUser((String) invocation.getArguments()[0], (String) invocation.getArguments()[1], (String) invocation.getArguments()[1], (String) invocation.getArguments()[2]);
            }
        });

        // document
        lenient().when(documentRepo.findAll()).thenReturn(mockDocumentList);

        lenient().when(documentRepo.save(any(Document.class))).then(new Answer<Document>() {
            @Override
            public Document answer(InvocationOnMock invocation) throws Throwable {
                return saveDocument((Document) invocation.getArguments()[0]);
            }
        });

        lenient().when(documentRepo.update(any(Document.class))).then(new Answer<Document>() {
            @Override
            public Document answer(InvocationOnMock invocation) throws Throwable {
                return saveDocument((Document) invocation.getArguments()[0]);
            }
        });

        lenient().when(documentRepo.findByProjectNameAndName(any(String.class), any(String.class))).then(new Answer<Document>() {
            @Override
            public Document answer(InvocationOnMock invocation) throws Throwable {
                return findDocumentByProjectNameandName((String) invocation.getArguments()[0], (String) invocation.getArguments()[1]);
            }
        });

        lenient().when(documentRepo.getById(any(Long.class))).then(new Answer<Document>() {
            @Override
            public Document answer(InvocationOnMock invocation) throws Throwable {
                return findDocumentById((Long) invocation.getArguments()[0]);
            }
        });

        // TODO
        lenient().when(documentRepo.pageableDynamicDocumentQuery((Map<String, String[]>) any(Map.class), (org.springframework.data.domain.Pageable) any(Pageable.class))).then(new Answer<Page<Document>>() {
            @Override
            public Page<Document> answer(InvocationOnMock invocation) throws Throwable {
                return (Page<Document>) TEST_DOCUMENT1;
            }
        });

        lenient().when(documentRepo.findByStatus(any(String.class))).thenReturn(mockDocumentList);

        // project
        lenient().when(projectRepo.findAll()).thenReturn(mockProjectList);

        lenient().when(projectRepo.create(any(String.class), any(IngestType.class), any(Boolean.class), any(List.class), any(List.class), any(List.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return TEST_PROJECT1;
            }
        });

        lenient().when(projectRepo.getById(any(Long.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return findProjectById((Long) invocation.getArguments()[0]);
            }
        });

        lenient().when(projectRepo.read(any(Long.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return findProjectById((Long) invocation.getArguments()[0]);
            }
        });

        lenient().when(projectRepo.findByName(any(String.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return findProjectbyName((String) invocation.getArguments()[0]);
            }
        });

        lenient().when(projectRepo.save(any(Project.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return saveProject((Project) invocation.getArguments()[0]);
            }
        });

        lenient().when(projectRepo.update(any(Project.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return updateProject((Project) invocation.getArguments()[0]);
            }
        });

        lenient().when(projectsService.getOrCreateProject(any(File.class))).then(new Answer<Project>() {
            @Override
            public Project answer(InvocationOnMock invocation) throws Throwable {
                return TEST_PROJECT1;
            }
        });

        // resource
        lenient().when(resourceRepo.findAllByDocumentProjectNameAndDocumentName(any(String.class), any(String.class))).thenReturn(new ArrayList<Resource>());

        // Field Profiles

        //for cases when we expect the FieldProfile not to be found
        lenient().when(fieldProfileRepo.findByProjectAndGloss(refEq(TEST_PROJECT1), any(String.class))).then(new Answer<FieldProfile>() {
           @Override
           public FieldProfile answer(InvocationOnMock invocation) throws Throwable {
               return null;
           }
        });

        lenient().when(fieldProfileRepo.save(any(FieldProfile.class))).then(new Answer<FieldProfile>() {
            @Override
            public FieldProfile answer(InvocationOnMock invocation) throws Throwable {
                return TEST_PROFILE1;
            }
         });

        // Metadata Field Labels

        lenient().when(metadataFieldLabelRepo.create(any(String.class), any(FieldProfile.class))).then(new Answer<MetadataFieldLabel>() {
            @Override
            public MetadataFieldLabel answer(InvocationOnMock invocation) throws Throwable {
                return TEST_META_LABEL;
            }
        });

        lenient().when(metadataFieldLabelRepo.findByNameAndProfile(any(String.class),any(FieldProfile.class))).then(new Answer<MetadataFieldLabel>() {
            @Override
            public MetadataFieldLabel answer(InvocationOnMock invocation) throws Throwable {
                return TEST_META_LABEL;
            }
         });

        // metdataHeader formats
        mockSpotlightExportedMetadataHeaders.add(0, "url");
        mockSpotlightExportedMetadataHeaders.add(1, "spotlight exported metadataheader 1");
        mockSpotlightExportedMetadataHeaders.add(2, "spotlight exported metadataheader 1");

        lenient().when(spotlightExporter.extractMetadataFields(any(String.class))).thenReturn(mockSpotlightExportedMetadataHeaders);

        mockSpotlightMetdata.add(mockSpotlightExportedMetadataHeaders);
        lenient().when(spotlightExporter.extractMetadata(any(Project.class))).thenReturn(mockSpotlightMetdata);

        mockDspaceCSVExportedMetadataHeaders.add("BUNDLE:ORIGINAL");
        mockDspaceCSVExportedMetadataHeaders.add("dspaceCSVExportedMetadataHeader 1");
        mockDspaceCSVExportedMetadataHeaders.add("dspaceCSVExportedMetadataHeader 2");
        mockDspaceCSVExportedMetadata.add(mockDspaceCSVExportedMetadataHeaders);
        lenient().when(dspaceCSVExporter.extractMetadataFields(any(String.class))).thenReturn(mockDspaceCSVExportedMetadataHeaders);
        lenient().when(dspaceCSVExporter.extractMetadata(any(Project.class))).thenReturn(mockDspaceCSVExportedMetadata);

        lenient().when(metadataFieldGroupRepo.findAll()).thenReturn(mockMetadataFieldGroupList);

    }

    @AfterEach
    public void cleanUp() {
        response = null;
        documentRepo.deleteAll();
        metadataFieldGroupRepo.deleteAll();
        projectRepo.deleteAll();
        userRepo.deleteAll();
    }
}
