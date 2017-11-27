package edu.tamu.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "url", "repository_id" }))
public class PublishedLocation extends BaseEntity {

    @ManyToOne(optional = false)
    private ProjectRepository repository;

    @Column(nullable = false)
    private String url;

    public PublishedLocation() {

    }

    public PublishedLocation(ProjectRepository repository, String url) {
        this();
        setRepository(repository);
        setUrl(url);
    }

    public ProjectRepository getRepository() {
        return repository;
    }

    public void setRepository(ProjectRepository repository) {
        this.repository = repository;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
