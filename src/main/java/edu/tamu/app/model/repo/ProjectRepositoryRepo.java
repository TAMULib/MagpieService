package edu.tamu.app.model.repo;

import org.springframework.stereotype.Repository;

import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.repo.custom.ProjectRepositoryRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

@Repository
public interface ProjectRepositoryRepo extends WeaverRepo<ProjectRepository>, ProjectRepositoryRepoCustom {

    public ProjectRepository findByName(String name);

}
