package com.yuranium.authservice.service;

import com.yuranium.authservice.models.entity.AvatarEntity;
import com.yuranium.authservice.repository.AvatarRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvatarService
{
    private final AvatarRepository avatarRepository;

    public static final float COMPRESSION_QUALITY = 0.6F;

    @Transactional
    public List<AvatarEntity> saveAll(List<AvatarEntity> avatars)
    {
        return avatarRepository.saveAll(avatars);
    }

    public List<AvatarEntity> multipartToEntity(List<MultipartFile> file)
    {
        if (file == null)
            return null;
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

    @SneakyThrows
    public byte[] compressImage(byte[] inputBytes)
    {
        final int MIN_BYTE_SIZE_COMPRESSION = 20_000;

        if (inputBytes.length < MIN_BYTE_SIZE_COMPRESSION)
            return inputBytes;
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(inputBytes));
        if (image == null)
            throw new IllegalArgumentException("Incorrect image");
        image = resizeImageWithAspectRatio(image);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream))
        {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(COMPRESSION_QUALITY);

            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();

            return outputStream.toByteArray();
        }
    }

    private BufferedImage resizeImageWithAspectRatio(BufferedImage originalImage)
    {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        if (originalWidth <= 1920 && originalHeight <= 1080)
            return originalImage;

        double widthRatio = (double) 1920 / originalWidth;
        double heightRatio = (double) 1080 / originalHeight;

        double scale = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        java.awt.Image resultingImage = originalImage.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();

        return outputImage;
    }
}