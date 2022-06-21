package edu.tamu.app.model;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import edu.tamu.app.service.PropertyProtectionService;
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

    @Transient
    private PropertyProtectionService propertyProtectionService = null;

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
        List<String> targetSettingValues = null;
        boolean isProtect = false;
        for (ProjectSetting setting : settings) {
            if (setting.getKey().equals(key)) {
                isProtect = setting.isProtect();
                targetSettingValues = setting.getValueList();
                break;
            }
        }
        if (propertyProtectionService != null && isProtect) {
            try {
                targetSettingValues = propertyProtectionService.decryptPropertyValues(targetSettingValues);
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }
        return targetSettingValues;
    }

    public PropertyProtectionService getPropertyProtectionService() {
        return propertyProtectionService;
    }

    public void setPropertyProtectionService(PropertyProtectionService propertyProtectionService) {
        this.propertyProtectionService = propertyProtectionService;
    }

}
