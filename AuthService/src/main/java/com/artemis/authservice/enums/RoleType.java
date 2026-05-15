package com.artemis.authservice.enums;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority
{
    ROLE_USER, ROLE_OWNER, ROLE_ADMIN;

    @Override
    public String getAuthority()
    {
        return name();
    }
}