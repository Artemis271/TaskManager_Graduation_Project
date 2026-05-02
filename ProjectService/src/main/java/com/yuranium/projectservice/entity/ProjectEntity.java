package com.yuranium.projectservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "project")
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEntity
{
    @Id
    @Column(name = "id_project", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_added", columnDefinition = "DATE")
    private LocalDate dateAdded;

    @Column(name = "date_updated", columnDefinition = "DATE")
    private LocalDate dateUpdated;

    @BatchSize(size = 15)
    @OneToMany(mappedBy = "project")
    private List<AvatarEntity> avatars = new ArrayList<>();

    @Column(name = "id_user")
    private Long userId;

    @PrePersist
    private void prePersist()
    {
        this.id = UUID.randomUUID();
        this.dateUpdated = LocalDate.now();

        if (this.dateAdded == null)
            this.dateAdded = LocalDate.now();
    }

    public void setAvatars(List<AvatarEntity> avatars)
    {
        for (AvatarEntity avatar : avatars)
            avatar.setProject(this);
        this.avatars = avatars;
    }
}