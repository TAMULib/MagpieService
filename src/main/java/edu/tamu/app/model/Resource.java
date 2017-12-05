/* 
 * Resource.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.weaver.data.model.BaseEntity;
import edu.tamu.weaver.data.resolver.BaseEntityIdResolver;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "document_id" }))
public class Resource extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Document.class, resolver = BaseEntityIdResolver.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Document document;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String mimeType;

    public Resource() {

    }

    public Resource(Document document, String name, String path, String url, String mimeType) {
        this();
        this.document = document;
        this.name = name;
        this.path = path;
        this.url = url;
        this.mimeType = mimeType;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}
