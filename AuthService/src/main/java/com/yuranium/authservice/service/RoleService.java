package com.yuranium.authservice.service;

import com.yuranium.authservice.models.entity.RoleEntity;
import com.yuranium.authservice.mapper.RoleMapper;
import com.yuranium.authservice.repository.RoleRepository;
import com.yuranium.authservice.util.exception.RoleEntityNotFoundException;
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

    public List<RoleEntity> saveAll(Set<RoleEntity> roles)
    {
        return roleRepository.saveAll(roles);
    }
}