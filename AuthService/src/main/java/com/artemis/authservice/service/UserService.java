package com.artemis.authservice.service;

import com.artemis.authservice.enums.RoleType;
import com.artemis.authservice.mapper.UserMapper;
import com.artemis.authservice.models.MyUserDetails;
import com.artemis.authservice.models.dto.UserDto;
import com.artemis.authservice.models.dto.UserInfoDto;
import com.artemis.authservice.models.dto.UserInputDto;
import com.artemis.authservice.models.dto.UserUpdateDto;
import com.artemis.authservice.models.entity.AvatarEntity;
import com.artemis.authservice.models.entity.RoleEntity;
import com.artemis.authservice.models.entity.UserEntity;
import com.artemis.authservice.repository.UserRepository;
import com.artemis.authservice.service.kafka.KafkaProducer;
import com.artemis.authservice.util.exception.UserEntityAlreadyExistsException;
import com.artemis.authservice.util.exception.UserEntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService
{
    private final UserRepository userRepository;

    private final AvatarService avatarService;

    private final RoleService roleService;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final KafkaProducer kafkaProducer;

    @Transactional(readOnly = true)
    public UserInfoDto getUser(Long id)
    {
        return userMapper.toInfoDto(
                userRepository.findByIdAndIsDeletedFalse(id)
                        .orElseThrow(() -> new UserEntityNotFoundException(
                                String.format("The user with id=%d was not found!", id))
                        ));
    }

    @Transactional
    public UserDto createUser(UserInputDto userDto)
    {
        if (userRepository.findByEmail(userDto.email()).isPresent())
            throw new UserEntityAlreadyExistsException(
                    String.format("The user with email=%s is already exists", userDto.email()
                    ));
        UserEntity userEntity = userMapper.toEntity(userDto);
        userEntity.setRoles(new HashSet<>(Set.of(roleService.getRole(1))));
        userEntity.setAvatars(avatarService.multipartToEntity(userDto.avatars()));
        userEntity.setPassword(passwordEncoder.encode(userDto.password()));

        if (userDto.avatars() != null)
            avatarService.saveAll(userEntity.getAvatars());
        roleService.saveAll(userEntity.getRoles());
        UserEntity user = userRepository.save(userEntity);
        kafkaProducer.sendCreateUserEvent(user);

        return userMapper.toUserDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UserUpdateDto userDto)
    {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserEntityNotFoundException(
                        String.format(
                                "The user with id=%d cannot be updated because it does not exist",
                                id
                        )
                ));
        userEntity.setUsername(userDto.username());
        userEntity.setName(userDto.name());
        userEntity.setLastName(userDto.lastName());

        if (userDto.avatars() != null)
        {
            List<AvatarEntity> newAvatars = avatarService.multipartToEntity(userDto.avatars());
            avatarService.saveAll(newAvatars);
            userEntity.getAvatars().addAll(newAvatars);
            userEntity.setAvatars(userEntity.getAvatars());
        }

        kafkaProducer.sendUpdateUserEvent(userEntity);
        return userMapper.toUserDto(
                userRepository.save(userEntity)
        );
    }

    @Transactional
    public void updateUserAvatar(Long id, MultipartFile file)
    {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserEntityNotFoundException(
                        String.format(
                                "The user with id=%d cannot be updated because it does not exist",
                                id
                        )
                ));
        List<AvatarEntity> newAvatars = avatarService.multipartToEntity(List.of(file));
        avatarService.saveAll(newAvatars);
        userEntity.getAvatars().addAll(newAvatars);
        userEntity.setAvatars(userEntity.getAvatars());
        kafkaProducer.sendUpdateUserEvent(userEntity);
    }

    @Transactional(readOnly = true)
    public List<UserInfoDto> getAllUsers()
    {
        return userRepository.findAllByIsDeletedFalse().stream()
                .map(userMapper::toInfoDto)
                .toList();
    }

    @Transactional
    public UserDto assignAdminRole(Long id)
    {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserEntityNotFoundException(
                        String.format("The user with id=%d was not found!", id)
                ));
        RoleEntity adminRole = roleService.getRoleByType(RoleType.ROLE_ADMIN);
        userEntity.getRoles().add(adminRole);
        return userMapper.toUserDto(userRepository.save(userEntity));
    }

    @Transactional
    public UserDto revokeAdminRole(Long id)
    {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserEntityNotFoundException(
                        String.format("The user with id=%d was not found!", id)
                ));
        userEntity.getRoles().removeIf(r -> r.getRole() == RoleType.ROLE_ADMIN);
        return userMapper.toUserDto(userRepository.save(userEntity));
    }

    @Transactional
    public void deleteUser(Long id)
    {
        UserEntity userEntity = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new UserEntityNotFoundException(
                        String.format(
                                "The user with id=%d cannot be removed because it does not exist",
                                id
                        )
                ));
        userEntity.setIsDeleted(true);
        kafkaProducer.sendDeleteUserEvent(id);
        userRepository.save(userEntity);
    }

    @Transactional
    public UserDto restoreUser(Long id)
    {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserEntityNotFoundException(
                        String.format("The user with id=%d was not found!", id)
                ));
        userEntity.setIsDeleted(false);
        return userMapper.toUserDto(userRepository.save(userEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        return new MyUserDetails(
                userRepository.findByEmailAndIsDeletedFalse(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException(
                                        String.format("The user with email=%s was not found!",
                                                username)
                                )
                        )
        );
    }
}