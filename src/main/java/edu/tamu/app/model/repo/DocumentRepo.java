package edu.tamu.app.model.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.repo.custom.DocumentRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

@Repository
public interface DocumentRepo extends WeaverRepo<Document>, DocumentRepoCustom, JpaSpecificationExecutor<Document> {

    public Page<Document> findAll(Specification<Document> specification, Pageable pageable);

    public Page<Document> findAll(Pageable pageable);

    public Document findByProjectNameAndName(String projectName, String name);

    public List<Document> findByStatus(String status);

    public List<Document> findByProjectNameAndStatus(String projectName, String status);

}
