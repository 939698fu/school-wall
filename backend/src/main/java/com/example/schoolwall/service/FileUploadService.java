package com.example.schoolwall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {

    /**
     * 上传图片
     * @param file 上传的文件
     * @param folder 存储文件夹
     * @return 文件访问URL
     */
    String uploadImage(MultipartFile file, String folder);

    /**
     * 上传帖子图片
     * @param file 上传的文件
     * @return 文件访问URL
     */
    String uploadPostImage(MultipartFile file);

    /**
     * 上传用户头像
     * @param file 上传的文件
     * @return 文件访问URL
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 上传消息图片
     * @param file 上传的文件
     * @return 文件访问URL
     */
    String uploadMessageImage(MultipartFile file);
}