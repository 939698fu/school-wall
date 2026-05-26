package com.example.schoolwall.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 关注记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("follow_record")
public class FollowRecord extends BaseEntity {

    /**
     * 关注者ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 被关注者ID
     */
    @TableField("followed_id")
    private Long followedId;
}
