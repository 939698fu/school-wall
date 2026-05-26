-- 校园微墙数据库初始化脚本
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS `school_wall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `school_wall`;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名（登录用）',
    `password` VARCHAR(200) NOT NULL COMMENT '密码（加密存储，格式：salt$hash）',
    `open_id` VARCHAR(100) UNIQUE DEFAULT '' COMMENT '微信OpenID',
    `nickname` VARCHAR(50) NOT NULL COMMENT '用户昵称',
    `avatar` VARCHAR(500) DEFAULT '🍊' COMMENT '头像URL',
    `school` VARCHAR(100) DEFAULT '' COMMENT '学校名称',
    `bio` VARCHAR(100) DEFAULT '' COMMENT '个人简介',
    `post_count` INT DEFAULT 0 COMMENT '帖子数',
    `like_count` INT DEFAULT 0 COMMENT '获赞数',
    `collect_count` INT DEFAULT 0 COMMENT '收藏数',
    `follower_count` INT DEFAULT 0 COMMENT '粉丝数',
    `following_count` INT DEFAULT 0 COMMENT '关注数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_open_id` (`open_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 2. 帖子表
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '作者ID',
    `title` VARCHAR(50) NOT NULL COMMENT '帖子标题',
    `content` TEXT NOT NULL COMMENT '帖子内容',
    `images` TEXT COMMENT '图片URL列表，JSON格式',
    `tag` VARCHAR(20) DEFAULT '校园生活' COMMENT '话题标签',
    `tag_color` VARCHAR(20) DEFAULT 'gray' COMMENT '标签颜色',
    `is_anon` TINYINT(1) DEFAULT 0 COMMENT '是否匿名(0-否,1-是)',
    `is_private` TINYINT(1) DEFAULT 0 COMMENT '是否仅自己可见(0-否,1-是)',
    `likes` INT DEFAULT 0 COMMENT '点赞数',
    `comments` INT DEFAULT 0 COMMENT '评论数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_tag` (`tag`),
    INDEX `idx_is_anon` (`is_anon`),
    INDEX `idx_likes` (`likes`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子表';

-- ----------------------------
-- 3. 评论表
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    `post_id` BIGINT NOT NULL COMMENT '帖子ID',
    `user_id` BIGINT NOT NULL COMMENT '评论者ID',
    `content` VARCHAR(200) NOT NULL COMMENT '评论内容',
    `likes` INT DEFAULT 0 COMMENT '点赞数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`post_id`) REFERENCES `post`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    INDEX `idx_post_id` (`post_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ----------------------------
-- 4. 点赞记录表
-- ----------------------------
DROP TABLE IF EXISTS `like_record`;
CREATE TABLE `like_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `post_id` BIGINT DEFAULT NULL COMMENT '帖子ID',
    `comment_id` BIGINT DEFAULT NULL COMMENT '评论ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`post_id`) REFERENCES `post`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`comment_id`) REFERENCES `comment`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
    UNIQUE KEY `uk_user_comment` (`user_id`, `comment_id`),
    INDEX `idx_post_id` (`post_id`),
    INDEX `idx_comment_id` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录表';

-- ----------------------------
-- 5. 收藏记录表
-- ----------------------------
DROP TABLE IF EXISTS `collect_record`;
CREATE TABLE `collect_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `post_id` BIGINT NOT NULL COMMENT '帖子ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`post_id`) REFERENCES `post`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
    INDEX `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏记录表';

-- ----------------------------
-- 6. 关注记录表
-- ----------------------------
DROP TABLE IF EXISTS `follow_record`;
CREATE TABLE `follow_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '关注者ID',
    `followed_id` BIGINT NOT NULL COMMENT '被关注者ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`followed_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_user_followed` (`user_id`, `followed_id`),
    INDEX `idx_followed_id` (`followed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注记录表';

-- ----------------------------
-- 7. 消息表
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    `from_id` BIGINT NOT NULL COMMENT '发送者ID',
    `to_id` BIGINT NOT NULL COMMENT '接收者ID',
    `content` VARCHAR(1000) NOT NULL COMMENT '消息内容',
    `type` VARCHAR(20) DEFAULT 'text' COMMENT '消息类型(text/image)',
    `is_read` TINYINT(1) DEFAULT 0 COMMENT '是否已读(0-未读,1-已读)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`from_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`to_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    INDEX `idx_from_id` (`from_id`),
    INDEX `idx_to_id` (`to_id`),
    INDEX `idx_is_read` (`is_read`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- ----------------------------
-- 初始化测试数据
-- ----------------------------

-- 插入测试用户（密码统一为：123456，加密格式为 salt$hash）
INSERT INTO `user` (`username`, `password`, `open_id`, `nickname`, `avatar`, `school`, `bio`, `post_count`, `like_count`, `collect_count`) VALUES
('chengzi', 'aGVsbG8gd29ybGQ=$jZAEJE0aT9bZk6M4C8T0sL0E4Y5rT6F7G8H9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z6', 'test_openid_1', '橙子不甜', '🍊', '某某大学', '喜欢吃喝玩乐，摸鱼度日🐟', 12, 286, 43),
('shuxue', 'aGVsbG8gd29ybGQ=$jZAEJE0aT9bZk6M4C8T0sL0E4Y5rT6F7G8H9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z6', 'test_openid_2', '数学学长', '🦁', '某某大学', '数学系大三，喜欢数学分析', 25, 1520, 89),
('xiaojuzi', 'aGVsbG8gd29ybGQ=$jZAEJE0aT9bZk6M4C8T0sL0E4Y5rT6F7G8H9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z6', 'test_openid_3', '小橘子同学', '🐱', '某某大学', '努力学习的大一新生', 8, 156, 23),
('anonym', 'aGVsbG8gd29ybGQ=$jZAEJE0aT9bZk6M4C8T0sL0E4Y5rT6F7G8H9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z6', 'test_openid_4', '匿名用户', '🐼', '某某大学', '', 5, 312, 12),
('xuexi', 'aGVsbG8gd29ybGQ=$jZAEJE0aT9bZk6M4C8T0sL0E4Y5rT6F7G8H9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z6', 'test_openid_5', '学习委员本委', '📖', '某某大学', '分享学习资源，帮助同学进步', 20, 430, 56);

-- 插入测试帖子
INSERT INTO `post` (`user_id`, `title`, `content`, `tag`, `tag_color`, `is_anon`, `likes`, `comments`) VALUES
(1, '北区食堂新出的麻辣香锅真的绝了！！', '今天中午去北区食堂吃饭，偶然发现新开了个麻辣香锅窗口，随手点了一份，结果完全惊艳到我了！！\n\n食材超级新鲜，锅底是那种红汤的，麻辣程度可以自选，我选了中辣刚刚好。里面有午餐肉、腐竹、莲藕、土豆...价格也很实惠，才18块钱。\n\n强烈推荐大家去试试！就在北区食堂二楼最右边那个窗口，记得早点去不然要排长队的！', '美食', 'orange', 1, 128, 34),
(3, '有没有大神可以帮我看一下这道数分题？', '这道题我做了两个小时还没搞明白，感觉思路有点对但是最后一步总是卡住，求大神指点...\n\n题目是：设 f(x) 在 [a,b] 上连续，在 (a,b) 上可导，且 f(a)=f(b)=0，证明存在 ξ∈(a,b) 使得 f(ξ)+f(ξ)=0。', '学习', 'blue', 0, 56, 21),
(4, '想向图书馆二楼靠窗那个男生表白', '每次去图书馆都能看到你，黑色卫衣，总是戴着耳机认真看书，不知道你叫什么名字，只是觉得这种专注的样子特别好看。\n\n如果你刚好看到这条帖子，可以私信我吗🥺', '表白', 'pink', 1, 312, 88),
(1, '图书馆自习座位被人"占座"了该怎么办', '今天去图书馆，看到一排座位全部用书包占着，但是人都不在。我等了快一个小时了，这合理吗？学校有没有规定说不能占座？', '吐槽', 'gray', 0, 89, 45),
(5, '分享一个超好用的备考资料整理网站', '期末季到了，分享给大家一个整理好的备考资源站，里面有历年真题和重点笔记，纯白嫖，感谢学长学姐们的贡献！', '资源', 'green', 0, 430, 62);

-- 插入测试评论
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `likes`) VALUES
(1, 3, '已经约好舍友明天中午去！感谢博主强推！🔥🔥', 23),
(1, 4, '北区食堂要排队的，我今天特意绕过去看了，队伍排到门口了哈哈哈', 11),
(1, 1, '@隔壁学院的路过 哈哈对，我当时去是11点半刚开，建议早点！', 8),
(2, 2, '考虑辅助函数 g(x)=e^x·f(x)，然后对 g 用罗尔定理即可', 18),
(2, 3, '啊啊啊！懂了懂了！太感谢了！', 3),
(3, 1, '图书馆二楼靠窗男生们：是我！都是我！', 200),
(3, 5, '图书馆二楼靠窗现在已经没有位置了（', 99);

-- 插入测试消息
INSERT INTO `message` (`from_id`, `to_id`, `content`, `is_read`) VALUES
(2, 3, '你好，我看了你发的那道数分题', 1),
(3, 2, '啊好的！你有思路吗', 1),
(2, 3, '考虑辅助函数 g(x)=e^x·f(x)，对 g 用罗尔定理就行', 1),
(3, 2, '哇！懂了！那你有空可以帮我看看其他几道吗😭', 1),
(2, 3, '可以，明天下午图书馆见！', 0),
(1, 3, '晚上一起去吃麻辣香锅不', 0),
(5, 1, '你好，我想报名这次志愿者活动', 1),
(1, 5, '好的收到，我转给负责人', 1);

-- ----------------------------
-- 创建视图：获取帖子详情（包含作者信息）
-- ----------------------------
DROP VIEW IF EXISTS `post_detail_view`;
CREATE VIEW `post_detail_view` AS
SELECT 
    p.*,
    u.nickname AS author_nickname,
    u.avatar AS author_avatar
FROM `post` p
LEFT JOIN `user` u ON p.user_id = u.id;

-- ----------------------------
-- 创建视图：获取评论详情（包含评论者信息）
-- ----------------------------
DROP VIEW IF EXISTS `comment_detail_view`;
CREATE VIEW `comment_detail_view` AS
SELECT 
    c.*,
    u.nickname AS author_nickname,
    u.avatar AS author_avatar
FROM `comment` c
LEFT JOIN `user` u ON c.user_id = u.id;
