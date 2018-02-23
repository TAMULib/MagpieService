package edu.tamu.app.model.repo;

import org.springframework.stereotype.Repository;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.custom.ProjectRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

@Repository
public interface ProjectRepo extends WeaverRepo<Project>, ProjectRepoCustom {

    public Project findByName(String name);

}
