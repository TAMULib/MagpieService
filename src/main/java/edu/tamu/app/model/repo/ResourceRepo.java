package edu.tamu.app.model.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import edu.tamu.app.model.Resource;
import edu.tamu.app.model.repo.custom.ResourceRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

@Repository
public interface ResourceRepo extends WeaverRepo<Resource>, ResourceRepoCustom {

    public Resource findByDocumentNameAndName(String documentName, String name);

    public List<Resource> findAllByDocumentName(String documentName);

}
