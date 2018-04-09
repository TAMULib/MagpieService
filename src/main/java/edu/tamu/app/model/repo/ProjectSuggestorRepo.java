package edu.tamu.app.model.repo;

import org.springframework.stereotype.Repository;

import edu.tamu.app.model.ProjectSuggestor;
import edu.tamu.app.model.repo.custom.ProjectSuggestorRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

@Repository
public interface ProjectSuggestorRepo extends WeaverRepo<ProjectSuggestor>, ProjectSuggestorRepoCustom {

    public ProjectSuggestor findByName(String name);

}
