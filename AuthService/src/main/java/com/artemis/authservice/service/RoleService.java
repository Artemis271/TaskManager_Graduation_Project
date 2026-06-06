package com.artemis.authservice.service;

import com.artemis.authservice.enums.RoleType;
import com.artemis.authservice.models.entity.RoleEntity;
import com.artemis.authservice.mapper.RoleMapper;
import com.artemis.authservice.repository.RoleRepository;
import com.artemis.authservice.util.exception.RoleEntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService
{
    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public RoleEntity getRole(Integer id)
    {
        return roleRepository.findById(id)
                .orElseThrow(
                        () -> new RoleEntityNotFoundException(
                                String.format("Role with id=%d not found in database!", id)
                        )
                );
    }

    public RoleEntity getRoleByType(RoleType roleType)
    {
        return roleRepository.findByRole(roleType)
                .orElseThrow(
                        () -> new RoleEntityNotFoundException(
                                String.format("Role %s not found in database!", roleType.name())
                        )
                );
    }

    public List<RoleEntity> saveAll(Set<RoleEntity> roles)
    {
        return roleRepository.saveAll(roles);
    }
}