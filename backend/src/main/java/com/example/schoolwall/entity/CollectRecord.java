package com.example.schoolwall.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("collect_record")
public class CollectRecord extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 帖子ID
     */
    @TableField("post_id")
    private Long postId;
}
