package com.yuranium.authservice.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private Integer size;

    @Column(name = "binary_data", columnDefinition = "BYTEA")
    private byte[] binaryData = new byte[0];

    @Column(name = "date_added", columnDefinition = "TIMESTAMP")
    private LocalDateTime dateAdded;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private UserEntity user;

    @PrePersist
    private void prePersist()
    {
        this.size = binaryData.length;

        if (this.dateAdded == null)
            this.dateAdded = LocalDateTime.now();
    }
}