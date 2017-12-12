package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
public class ProjectSetting extends BaseEntity {

    @Column(nullable = false)
    private String key;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> values;

    public ProjectSetting() {
        setValues(new ArrayList<String>());
    }

    public ProjectSetting(String key, List<String> values) {
        setKey(key);
        setValues(values);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public void addValue(String value) {
        if (!values.contains(value)) {
            this.values.add(value);
        }
    }

    public void removeValue(String value) {
        this.values.remove(value);
    }

}
