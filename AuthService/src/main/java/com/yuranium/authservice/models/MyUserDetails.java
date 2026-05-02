package com.yuranium.authservice.models;

import com.yuranium.authservice.models.entity.RoleEntity;
import com.yuranium.authservice.models.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@AllArgsConstructor
public class MyUserDetails implements UserDetails
{
    private UserEntity user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return user.getRoles().stream()
                .map(RoleEntity::getRole)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword()
    {
        return user.getPassword();
    }

    @Override
    public String getUsername()
    {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled()
    {
        return user.getActivity();
    }

    public Long getId()
    {
        return user.getId();
    }
}