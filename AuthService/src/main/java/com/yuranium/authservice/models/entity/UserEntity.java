package com.yuranium.authservice.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity
{
    @Id
    @Column(name = "id_user")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "date_registration", columnDefinition = "TIMESTAMP")
    private LocalDateTime dateRegistration;

    @Column(name = "activity")
    private Boolean activity;

    @BatchSize(size = 15)
    @OneToMany(mappedBy = "user")
    private List<AvatarEntity> avatars = new ArrayList<>();


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_role"))
    private Set<RoleEntity> roles;

    @PrePersist
    private void prePersist()
    {
        if (this.dateRegistration == null)
            this.dateRegistration = LocalDateTime.now();

        if (this.activity == null)
            this.activity = true;
    }

    public void setAvatars(List<AvatarEntity> avatars)
    {
        if (avatars == null)
            return;
        for (AvatarEntity avatar : avatars)
            avatar.setUser(this);
        this.avatars = avatars;
    }

    public void setRoles(Set<RoleEntity> roles)
    {
        Set<UserEntity> users = new HashSet<>(Set.of(this));
        Set<RoleEntity> currentRoles = new HashSet<>();
        if (roles == null) return;
        for (RoleEntity role : roles)
        {
            role.setUsers(users);
            currentRoles.add(role);
        }
        this.roles = currentRoles;
    }
}