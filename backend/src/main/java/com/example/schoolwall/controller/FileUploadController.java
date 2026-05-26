package com.example.schoolwall.controller;

import com.example.schoolwall.common.Result;
import com.example.schoolwall.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "文件上传模块", description = "图片上传管理")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * 通用图片上传接口
     */
    @PostMapping("/upload/image")
    @Operation(summary = "上传图片", description = "通用图片上传接口")
    public Result<Map<String, String>> uploadImage(
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file) {
        
        String url = fileUploadService.uploadImage(file, "images");
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success("上传成功", result);
    }

    /**
     * 帖子图片上传接口
     */
    @PostMapping("/posts/image/upload")
    @Operation(summary = "上传帖子图片", description = "上传帖子图片，返回图片URL")
    public Result<Map<String, String>> uploadPostImage(
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file) {
        
        String url = fileUploadService.uploadPostImage(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success("上传成功", result);
    }

    /**
     * 用户头像上传接口
     */
    @PostMapping("/user/avatar")
    @Operation(summary = "上传头像", description = "上传用户头像，返回头像URL")
    public Result<Map<String, String>> uploadAvatar(
            @Parameter(description = "头像文件") @RequestParam("file") MultipartFile file) {
        
        String url = fileUploadService.uploadAvatar(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success("上传成功", result);
    }

    /**
     * 消息图片上传接口
     */
    @PostMapping("/messages/image/upload")
    @Operation(summary = "上传消息图片", description = "上传消息图片，返回图片URL")
    public Result<Map<String, String>> uploadMessageImage(
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file) {
        
        String url = fileUploadService.uploadMessageImage(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success("上传成功", result);
    }
}