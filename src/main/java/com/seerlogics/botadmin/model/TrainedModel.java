package com.seerlogics.botadmin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lingoace.model.BaseModel;

import javax.persistence.*;

@Entity
@Table(name = "trained_model", uniqueConstraints = {@UniqueConstraint(columnNames = "name", name = "model_unique_name")})
public class TrainedModel extends BaseModel {
    @Column(length = 300, nullable = false)
    private String name;

    @Column(length = 1000, nullable = false)
    private String description;

    @Column(length = 100, nullable = false)
    private String fileType = "application/octet-stream";

    @Lob
    @JsonIgnore
    private byte[] file;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_account_id", nullable = false)
    private Account owner;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }
}
