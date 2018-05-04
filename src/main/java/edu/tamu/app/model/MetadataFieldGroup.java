package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "label_id", "document_id" }))
public class MetadataFieldGroup extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    private MetadataFieldLabel label;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Document.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Document document;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    private List<MetadataFieldValue> values;

    public MetadataFieldGroup() {
        values = new ArrayList<MetadataFieldValue>();
    }

    public MetadataFieldGroup(MetadataFieldLabel label) {
        this();
        this.label = label;
    }

    public MetadataFieldGroup(Document document, MetadataFieldLabel label) {
        this(label);
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public MetadataFieldLabel getLabel() {
        return label;
    }

    public void setLabel(MetadataFieldLabel label) {
        this.label = label;
    }

    public List<MetadataFieldValue> getValues() {
        return values;
    }

    public void setValues(List<MetadataFieldValue> values) {
        this.values = values;
    }

    public void addValue(MetadataFieldValue value) {
        if (!values.contains(value)) {
            values.add(value);
        }
    }

    public void removeValue(MetadataFieldValue value) {
        values.remove(value);
    }

    public void clearValues() {
        values.clear();
    }

    public boolean containsValue(String value) {
        boolean containsValue = false;
        for (MetadataFieldValue metadataFieldValue : values) {
            if (metadataFieldValue.getValue().equals(value)) {
                containsValue = true;
                break;
            }
        }
        return containsValue;
    }

}
