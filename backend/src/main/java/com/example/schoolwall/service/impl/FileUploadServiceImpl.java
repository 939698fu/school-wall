package com.example.schoolwall.service.impl;

import com.example.schoolwall.common.BusinessException;
import com.example.schoolwall.config.FileUploadConfig;
import com.example.schoolwall.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileUploadConfig fileUploadConfig;

    /**
     * 日期格式化器
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public String uploadImage(MultipartFile file, String folder) {
        validateFile(file);

        try {
            // 创建日期子目录
            String datePath = LocalDate.now().format(DATE_FORMATTER);
            String storagePath = fileUploadConfig.getStoragePath(folder) + File.separator + datePath;
            File storageDir = new File(storagePath);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 保存文件
            Path filePath = Paths.get(storagePath, newFilename);
            Files.write(filePath, file.getBytes());

            // 返回访问URL
            String accessUrl = fileUploadConfig.getAccessUrl(folder + "/" + datePath, newFilename);
            log.info("文件上传成功: {}", accessUrl);
            return accessUrl;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw BusinessException.error("文件上传失败");
        }
    }

    @Override
    public String uploadPostImage(MultipartFile file) {
        return uploadImage(file, "posts");
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        return uploadImage(file, "avatars");
    }

    @Override
    public String uploadMessageImage(MultipartFile file) {
        return uploadImage(file, "messages");
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.badRequest("请选择要上传的文件");
        }

        String contentType = file.getContentType();
        if (!fileUploadConfig.isAllowedType(contentType)) {
            throw BusinessException.badRequest("不支持的文件类型，仅支持图片格式");
        }

        long fileSize = file.getSize();
        long maxSizeBytes = (long) fileUploadConfig.getMaxSize() * 1024 * 1024;
        if (fileSize > maxSizeBytes) {
            throw BusinessException.badRequest("文件大小超过限制，最大支持" + fileUploadConfig.getMaxSize() + "MB");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}