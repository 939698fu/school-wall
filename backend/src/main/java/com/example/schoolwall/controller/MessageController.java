package com.example.schoolwall.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.schoolwall.common.Result;
import com.example.schoolwall.dto.request.SendMessageRequest;
import com.example.schoolwall.dto.response.ConversationVO;
import com.example.schoolwall.dto.response.MessageVO;
import com.example.schoolwall.dto.response.SearchContactVO;
import com.example.schoolwall.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/messages")
@Tag(name = "消息模块", description = "消息管理")
@RequiredArgsConstructor
public class MessageController {

    @Autowired
    private final MessageService messageService;

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    @Operation(summary = "获取会话列表", description = "获取当前用户的会话列表")
    public Result<List<ConversationVO>> getConversations(
            @RequestAttribute("userId") Long userId) {

        List<ConversationVO> result = messageService.getConversations(userId);
        return Result.success(result);
    }

    /**
     * 搜索联系人
     */
    @GetMapping("/search")
    @Operation(summary = "搜索联系人", description = "搜索用户名、昵称、学校")
    public Result<List<SearchContactVO>> searchContacts(
            @RequestAttribute("userId") Long userId,
            @Parameter(description = "搜索关键字") @RequestParam String keyword) {

        List<SearchContactVO> result = messageService.searchContacts(userId, keyword);
        return Result.success(result);
    }

    /**
     * 获取聊天记录
     */
    @GetMapping("/chat/{userId}")
    @Operation(summary = "获取聊天记录", description = "获取与指定用户的聊天记录")
    public Result<IPage<MessageVO>> getChatHistory(
            @Parameter(description = "目标用户ID") @PathVariable Long userId,
            @RequestAttribute("userId") Long currentUserId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer size) {

        IPage<MessageVO> result = messageService.getChatHistory(currentUserId, userId, page, size);
        return Result.success(result);
    }

    /**
     * 发送消息
     */
    @PostMapping("/send")
    @Operation(summary = "发送消息", description = "发送消息给指定用户")
    public Result<MessageVO> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @RequestAttribute("userId") Long userId) {

        MessageVO result = messageService.sendMessage(request, userId);
        return Result.success("发送成功", result);
    }

    /**
     * 标记消息已读
     */
    @PutMapping("/read/{userId}")
    @Operation(summary = "标记消息已读", description = "标记与指定用户的消息为已读")
    public Result<Void> markAsRead(
            @Parameter(description = "目标用户ID") @PathVariable Long userId,
            @RequestAttribute("userId") Long currentUserId) {

        messageService.markAsRead(currentUserId, userId);
        return Result.success();
    }
}