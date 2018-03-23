package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

@MappedSuperclass
public abstract class ProjectService extends ValidatingBaseEntity {

    @Column
    private String name;

    @Enumerated
    private ServiceType type;

    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    private List<ProjectSetting> settings;

    public ProjectService() {
        settings = new ArrayList<ProjectSetting>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    public List<ProjectSetting> getSettings() {
        return settings;
    }

    public void setSettings(List<ProjectSetting> settings) {
        this.settings = settings;
    }

    public List<String> getSettingValues(String key) {
        List<String> targetSetting = null;
        for (ProjectSetting setting : settings) {
            if (setting.getKey().equals(key)) {
                targetSetting = setting.getValues();
                break;
            }
        }
        return targetSetting;
    }

}
