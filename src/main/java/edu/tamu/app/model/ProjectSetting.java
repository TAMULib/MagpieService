package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import com.fasterxml.jackson.annotation.JsonGetter;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
public class ProjectSetting extends BaseEntity {

    @Column(nullable = false)
    private String key;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> values;

    @Column
    private Boolean protect = false;

    public ProjectSetting() {
        setValues(new ArrayList<String>());
    }

    public ProjectSetting(String key, List<String> values) {
        setKey(key);
        setValues(values);
    }

    public ProjectSetting(String key, List<String> values, Boolean protect) {
      setKey(key);
      setValues(values);
      setProtect(protect);
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

    @JsonGetter("values")
    protected List<String> getValuesForSerializer() {
        if (this.isProtect()) {
            ArrayList<String> protectedValues = new ArrayList<String>();
            this.getValues().forEach(k -> {
                protectedValues.add("");
            });
            return protectedValues;
        } else {
            return getValues();
        }
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

    public Boolean isProtect() {
      return protect;
    }

    public void setProtect(Boolean protect) {
      this.protect = protect;
    }

}
