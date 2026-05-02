package com.yuranium.authservice.enums;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority
{
    ROLE_USER, ROLE_OWNER;

    @Override
    public String getAuthority()
    {
        return name();
    }
}