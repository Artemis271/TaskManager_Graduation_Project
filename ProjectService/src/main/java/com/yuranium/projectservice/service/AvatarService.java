package com.yuranium.projectservice.service;

import com.yuranium.projectservice.entity.AvatarEntity;
import com.yuranium.projectservice.repository.AvatarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvatarService
{
    private final AvatarRepository avatarRepository;

    @Transactional
    public List<AvatarEntity> saveAll(List<AvatarEntity> avatars)
    {
        return avatarRepository.saveAll(avatars);
    }

    public List<AvatarEntity> multipartToEntity(List<MultipartFile> file)
    {
        return file.stream()
                .map(image -> {
                    AvatarEntity avatar = new AvatarEntity();
                    avatar.setName(image.getOriginalFilename());
                    avatar.setContentType(image.getContentType());
                    try {
                        avatar.setBinaryData(image.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return avatar;
                })
                .collect(Collectors.toList());
    }
}