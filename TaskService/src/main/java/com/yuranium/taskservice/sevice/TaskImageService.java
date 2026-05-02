package com.yuranium.taskservice.sevice;

import com.yuranium.taskservice.entity.TaskImageEntity;
import com.yuranium.taskservice.repository.TaskImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskImageService
{
    private final TaskImageRepository imageRepository;

    @Transactional
    public List<TaskImageEntity> saveAll(List<TaskImageEntity> avatars)
    {
        return imageRepository.saveAll(avatars);
    }

    public List<TaskImageEntity> multipartToEntity(List<MultipartFile> file)
    {
        return file.stream()
                .map(image -> {
                    TaskImageEntity avatar = new TaskImageEntity();
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