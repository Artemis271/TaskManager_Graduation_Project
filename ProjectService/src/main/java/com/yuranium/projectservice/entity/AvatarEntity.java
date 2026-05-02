package com.yuranium.projectservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "avatar")
@AllArgsConstructor
@NoArgsConstructor
public class AvatarEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avatar")
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "content_type", columnDefinition = "VARCHAR(20)")
    private String contentType;

    @Column(name = "size")
    private Long size;

    @Column(name = "binary_data", columnDefinition = "BYTEA")
    private byte[] binaryData;

    @Column(name = "date_added", columnDefinition = "DATE")
    private LocalDate dateAdded;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_project")
    private ProjectEntity project;

    @PrePersist
    private void prePersist()
    {
        this.size = (long) binaryData.length;
        this.dateAdded = LocalDate.now();
    }
}