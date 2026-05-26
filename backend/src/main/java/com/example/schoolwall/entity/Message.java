package com.example.schoolwall.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("message")
public class Message extends BaseEntity {

    /**
     * 发送者ID
     */
    @TableField("from_id")
    private Long fromId;

    /**
     * 接收者ID
     */
    @TableField("to_id")
    private Long toId;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 消息类型(text/image)
     */
    @TableField("type")
    private String type;

    /**
     * 图片url
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 是否已读(0-未读,1-已读)
     */
    @TableField("is_read")
    private Integer isRead;
}
