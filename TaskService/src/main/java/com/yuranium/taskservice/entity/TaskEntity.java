package com.yuranium.taskservice.entity;

import com.yuranium.taskservice.enums.TaskImportance;
import com.yuranium.taskservice.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "task")
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity
{
    @Id
    @Column(name = "id_task", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "importance")
    private TaskImportance taskImportance;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private TaskStatus taskStatus;

    @Column(name = "date_added", columnDefinition = "DATE")
    private LocalDate dateAdded;

    @Column(name = "date_updated", columnDefinition = "TIMESTAMP")
    private LocalDateTime dateUpdated;

    @Column(name = "date_finished", columnDefinition = "DATE")
    private LocalDate dateFinished;

    @Column(name = "finished")
    private Boolean isFinished;

    @Column(name = "id_project")
    private UUID projectId;

    @BatchSize(size = 15)
    @OneToMany(mappedBy = "task")
    private List<TaskImageEntity> images = new ArrayList<>();

    @PrePersist
    private void prePersist()
    {
        this.id = UUID.randomUUID();
        this.dateUpdated = LocalDateTime.now();

        if (this.dateAdded == null)
            this.dateAdded = LocalDate.now();

        if (this.isFinished == null)
            this.isFinished = false;
    }

    public void setImages(List<TaskImageEntity> images)
    {
        for (TaskImageEntity avatar : images)
            avatar.setTask(this);
        this.images = images;
    }
}