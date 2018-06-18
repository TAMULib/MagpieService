package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
public class ProjectSuggestor extends ProjectService {

    @ManyToMany(mappedBy="suggestors",fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Project.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<Project> projects = new ArrayList<Project>();

    public ProjectSuggestor() {
        super();
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
