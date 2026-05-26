package com.example.schoolwall.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * 文件上传配置类
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    /**
     * 上传路径
     */
    private String path = "./uploads";

    /**
     * 允许的文件类型
     */
    private String[] allowedTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};

    /**
     * 最大文件大小（MB）
     */
    private int maxSize = 10;

    /**
     * 访问路径前缀
     */
    private String accessUrl = "/api/upload";

    /**
     * 初始化上传目录
     */
    @PostConstruct
    public void init() {
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (created) {
                log.info("文件上传目录已创建: {}", uploadDir.getAbsolutePath());
            }
        }
    }

    /**
     * 检查文件类型是否允许
     */
    public boolean isAllowedType(String contentType) {
        if (contentType == null) {
            return false;
        }
        for (String type : allowedTypes) {
            if (contentType.toLowerCase().startsWith(type.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件存储路径
     */
    public String getStoragePath(String folder) {
        return path + File.separator + folder;
    }

    /**
     * 获取访问URL
     */
    public String getAccessUrl(String folder, String fileName) {
        return accessUrl + "/" + folder + "/" + fileName;
    }
}