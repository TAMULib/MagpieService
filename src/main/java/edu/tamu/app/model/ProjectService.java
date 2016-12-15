package edu.tamu.app.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import edu.tamu.app.enums.ServiceType;
import edu.tamu.framework.model.BaseEntity;

@MappedSuperclass
public abstract class ProjectService extends BaseEntity {

    @Column
    private String name;

    @Enumerated
    private ServiceType type;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSetting> settings;

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