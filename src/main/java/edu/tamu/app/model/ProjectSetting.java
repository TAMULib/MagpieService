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
    private List<String> valueList;

    @Column
    private Boolean protect = false;

    public ProjectSetting() {
        setValueList(new ArrayList<String>());
    }

    public ProjectSetting(String key, List<String> valueList) {
        setKey(key);
        setValueList(valueList);
    }

    public ProjectSetting(String key, List<String> valueList, Boolean protect) {
      setKey(key);
      setValueList(valueList);
      setProtect(protect);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getValueList() {
        return valueList;
    }

    @JsonGetter("values")
    protected List<String> getValuesForSerializer() {
        if (this.isProtect()) {
            ArrayList<String> protectedValues = new ArrayList<String>();
            this.getValueList().forEach(k -> {
                protectedValues.add("");
            });
            return protectedValues;
        } else {
            return getValueList();
        }
    }

    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }

    public void addValue(String value) {
        if (!valueList.contains(value)) {
            this.valueList.add(value);
        }
    }

    public void removeValue(String value) {
        this.valueList.remove(value);
    }

    public Boolean isProtect() {
      return protect;
    }

    public void setProtect(Boolean protect) {
      this.protect = protect;
    }

}
