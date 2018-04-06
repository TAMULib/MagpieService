package edu.tamu.app.model.repo;

import org.springframework.stereotype.Repository;

import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.repo.custom.ProjectAuthorityRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

@Repository
public interface ProjectAuthorityRepo extends WeaverRepo<ProjectAuthority>, ProjectAuthorityRepoCustom {

    public ProjectAuthority findByName(String name);

}