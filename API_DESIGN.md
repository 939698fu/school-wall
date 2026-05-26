# 校园微墙 - API 接口设计文档

## 目录

1. [用户模块](#1-用户模块)
2. [帖子模块](#2-帖子模块)
3. [评论模块](#3-评论模块)
4. [搜索模块](#4-搜索模块)
5. [消息模块](#5-消息模块)
6. [分页规范](#6-分页规范)
7. [RESTful 规范](#7-restful-规范)

---

## 1. 用户模块

### 1.1 微信登录

- **路径**: `POST /api/user/login`
- **描述**: 微信登录验证

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `code` | String | 是 | 微信登录临时凭证 |

**成功响应** (200):

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "user": {
            "id": 1,
            "nickname": "橙子不甜",
            "avatar": "🍊",
            "school": "某某大学",
            "bio": "喜欢吃喝玩乐",
            "postCount": 12,
            "likeCount": 286,
            "collectCount": 43
        }
    }
}
```

---

### 1.2 获取用户信息

- **路径**: `GET /api/user/info`
- **描述**: 获取当前登录用户信息
- **权限**: 需要登录

**成功响应** (200):

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "nickname": "橙子不甜",
        "avatar": "🍊",
        "school": "某某大学",
        "bio": "喜欢吃喝玩乐",
        "postCount": 12,
        "likeCount": 286,
        "collectCount": 43,
        "createTime": "2024-01-01 10:00:00"
    }
}
```

---

### 1.3 更新用户信息

- **路径**: `PUT /api/user/info`
- **描述**: 更新用户信息
- **权限**: 需要登录

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `nickname` | String | 否 | 用户昵称（2-20字符） |
| `avatar` | String | 否 | 头像 URL |
| `school` | String | 否 | 学校名称 |
| `bio` | String | 否 | 个人简介（最多100字符） |

**成功响应** (200):

```json
{
    "code": 200,
    "message": "更新成功",
    "data": {
        "id": 1,
        "nickname": "新昵称",
        "avatar": "🍎",
        "school": "新学校",
        "bio": "新简介"
    }
}
```

---

### 1.4 获取其他用户信息

- **路径**: `GET /api/user/{userId}`
- **描述**: 获取指定用户的公开信息

**路径参数**:

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `userId` | Long | 用户ID |

**成功响应** (200):

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 2,
        "nickname": "数学学长",
        "avatar": "🦁",
        "school": "某某大学",
        "bio": "数学系大三",
        "postCount": 25,
        "likeCount": 1520,
        "collectCount": 89,
        "isFollowed": false
    }
}
```

---

### 1.5 关注用户

- **路径**: `POST /api/user/{userId}/follow`
- **描述**: 关注指定用户
- **权限**: 需要登录

**成功响应** (200):

```json
{
    "code": 200,
    "message": "关注成功",
    "data": null
}
```

---

### 1.6 取消关注

- **路径**: `DELETE /api/user/{userId}/follow`
- **描述**: 取消关注指定用户
- **权限**: 需要登录

**成功响应** (200):

```json
{
    "code": 200,
    "message": "取消关注成功",
    "data": null
}
```

---

## 2. 帖子模块

### 2.1 获取帖子列表

- **路径**: `GET /api/posts`
- **描述**: 获取帖子列表（支持分页和分类筛选）

**请求参数**:

| 字段 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `page` | Integer | 否 | 1 | 页码 |
| `size` | Integer | 否 | 10 | 每页数量 |
| `type` | String | 否 | latest | 排序类型：latest(最新)、hot(热门)、hole(树洞)、love(表白) |
| `tag` | String | 否 | - | 话题标签筛选 |

**成功响应** (200):

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "title": "北区食堂新出的麻辣香锅真的绝了！！",
                "content": "今天中午去北区食堂吃饭...",
                "images": [],
                "tag": "美食",
                "tagColor": "orange",
                "isAnon": true,
                "author": "匿名用户",
                "authorAvatar": "🐼",
                "authorId": 101,
                "likes": 128,
                "liked": false,
                "collected": false,
                "commentCount": 34,
                "time": "10分钟前",
                "fullTime": "2024-06-10 12:34:00"
            }
        ],
        "total": 100,
        "page": 1,
        "size": 10
    }
}
```

---

### 2.2 获取帖子详情

- **路径**: `GET /api/posts/{postId}`
- **描述**: 获取帖子详情

**路径参数**:

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `postId` | Long | 帖子ID |

**成功响应** (200):

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "title": "标题",
        "content": "内容",
        "images": [],
        "tag": "美食",
        "tagColor": "orange",
        "isAnon": true,
        "author": "匿名用户",
        "authorAvatar": "🐼",
        "authorId": 101,
        "likes": 128,
        "liked": false,
        "collected": false,
        "commentCount": 34,
        "time": "10分钟前",
        "fullTime": "2024-06-10 12:34:00",
        "comments": [
            {
                "id": 1,
                "author": "饿了么同学",
                "avatar": "🦊",
                "content": "已经约好舍友明天中午去！",
                "time": "12:40",
                "likes": 23,
                "liked": false,
                "isAuthor": false
            }
        ]
    }
}
```

---

### 2.3 发布帖子

- **路径**: `POST /api/posts`
- **描述**: 发布新帖子
- **权限**: 需要登录

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `title` | String | 是 | 标题（2-50字符） |
| `content` | String | 是 | 正文（10-2000字符） |
| `images` | Array | 否 | 图片 URL 数组（最多9张） |
| `tag` | String | 否 | 话题标签 |
| `tagColor` | String | 否 | 标签颜色 |
| `isAnon` | Boolean | 否 | 是否匿名（默认false） |

**成功响应** (200):

```json
{
    "code": 200,
    "message": "发布成功",
    "data": {
        "id": 100,
        "title": "新帖子标题",
        "content": "帖子内容",
        "createTime": "2024-06-10 15:30:00"
    }
}
```

---

### 2.4 点赞帖子

- **路径**: `POST /api/posts/{postId}/like`
- **描述**: 点赞/取消点赞帖子
- **权限**: 需要登录

**成功响应** (200):

```json
{
    "code": 200,
    "message": "点赞成功",
    "data": {
        "liked": true,
        "likes": 129
    }
}
```

---

### 2.5 收藏帖子

- **路径**: `POST /api/posts/{postId}/collect`
- **描述**: 收藏/取消收藏帖子
- **权限**: 需要登录

**成功响应** (200):

```json
{
    "code": 200,
    "message": "收藏成功",
    "data": {
        "collected": true
    }
}
```

---

### 2.6 删除帖子

- **路径**: `DELETE /api/posts/{postId}`
- **描述**: 删除帖子（仅限自己的帖子）
- **权限**: 需要登录

**成功响应** (200):

```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

---

### 2.7 获取我的帖子

- **路径**: `GET /api/posts/mine`
- **描述**: 获取当前用户发布的帖子
- **权限**: 需要登录

**请求参数**:

| 字段 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `page` | Integer | 否 | 1 | 页码 |
| `size` | Integer | 否 | 10 | 每页数量 |

**成功响应** (200): 同帖子列表接口

---

### 2.8 获取我的收藏

- **路径**: `GET /api/posts/collections`
- **描述**: 获取当前用户收藏的帖子
- **权限**: 需要登录

**请求参数**:

| 字段 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `page` | Integer | 否 | 1 | 页码 |
| `size` | Integer | 否 | 10 | 每页数量 |

**成功响应** (200): 同帖子列表接口

---

## 3. 评论模块

### 3.1 获取评论列表

- **路径**: `GET /api/comments`
- **描述**: 获取帖子的评论列表

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `postId` | Long | 是 | 帖子ID |
| `page` | Integer | 否 | 1 | 页码 |
| `size` | Integer | 否 | 20 | 每页数量 |

**成功响应** (200):

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "postId": 1,
                "author": "饿了么同学",
                "avatar": "🦊",
                "authorId": 201,
                "content": "评论内容",
                "time": "12:40",
                "fullTime": "2024-06-10 12:40:00",
                "likes": 23,
                "liked": false,
                "isAuthor": false
            }
        ],
        "total": 34,
        "page": 1,
        "size": 20
    }
}
```

---

### 3.2 发布评论

- **路径**: `POST /api/comments`
- **描述**: 发布评论
- **权限**: 需要登录

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `postId` | Long | 是 | 帖子ID |
| `content` | String | 是 | 评论内容（1-200字符） |

**成功响应** (200):

```json
{
    "code": 200,
    "message": "评论成功",
    "data": {
        "id": 100,
        "postId": 1,
        "content": "新评论",
        "time": "刚刚"
    }
}
```

---

### 3.3 点赞评论

- **路径**: `POST /api/comments/{commentId}/like`
- **描述**: 点赞/取消点赞评论
- **权限**: 需要登录

**成功响应** (200):

```json
{
    "code": 200,
    "message": "点赞成功",
    "data": {
        "liked": true,
        "likes": 24
    }
}
```

---

### 3.4 删除评论

- **路径**: `DELETE /api/comments/{commentId}`
- **描述**: 删除评论（仅限自己的评论或帖子作者）
- **权限**: 需要登录

**成功响应** (200):

```json
{
    "code": 200,
    "message": "删除成功",
    "data": null
}
```

---

## 4. 搜索模块

### 4.1 搜索

- **路径**: `GET /api/search`
- **描述**: 搜索用户和帖子

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `keyword` | String | 是 | 搜索关键词 |
| `page` | Integer | 否 | 1 | 页码 |
| `size` | Integer | 否 | 10 | 每页数量 |

**成功响应** (200):

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "users": [
            {
                "id": 2,
                "nickname": "数学学长",
                "avatar": "🦁",
                "school": "某某大学",
                "postCount": 25
            }
        ],
        "posts": [
            {
                "id": 5,
                "title": "分享一个超好用的备考资料整理网站",
                "content": "期末季到了...",
                "author": "学习委员",
                "likes": 430,
                "commentCount": 62,
                "time": "3小时前"
            }
        ],
        "userTotal": 5,
        "postTotal": 23
    }
}
```

---

## 5. 消息模块

### 5.1 获取会话列表

- **路径**: `GET /api/messages/conversations`
- **描述**: 获取当前用户的会话列表
- **权限**: 需要登录

**成功响应** (200):

```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "userId": 201,
            "name": "数学学长",
            "avatar": "🦁",
            "lastMsg": "可以，明天下午图书馆见！",
            "lastTime": "刚刚",
            "unread": 2
        }
    ]
}
```

---

### 5.2 获取聊天记录

- **路径**: `GET /api/messages/chat/{userId}`
- **描述**: 获取与指定用户的聊天记录
- **权限**: 需要登录

**请求参数**:

| 字段 | 类型 | 必填 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `page` | Integer | 否 | 1 | 页码 |
| `size` | Integer | 否 | 20 | 每页数量 |

**成功响应** (200):

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [
            {
                "id": 1,
                "fromId": 201,
                "toId": 1,
                "content": "你好，我看了你发的那道数分题",
                "time": "12:20",
                "fullTime": "2024-06-10 12:20:00",
                "fromMe": false
            }
        ],
        "total": 5,
        "page": 1,
        "size": 20
    }
}
```

---

### 5.3 发送消息

- **路径**: `POST /api/messages/send`
- **描述**: 发送消息
- **权限**: 需要登录

**请求参数**:

| 字段 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| `toId` | Long | 是 | 接收用户ID |
| `content` | String | 是 | 消息内容（1-1000字符） |
| `type` | String | 否 | text | 消息类型：text(文本)、image(图片) |

**成功响应** (200):

```json
{
    "code": 200,
    "message": "发送成功",
    "data": {
        "id": 100,
        "fromId": 1,
        "toId": 201,
        "content": "消息内容",
        "time": "刚刚",
        "fromMe": true
    }
}
```

---

### 5.4 标记消息已读

- **路径**: `PUT /api/messages/read/{userId}`
- **描述**: 标记与指定用户的消息为已读
- **权限**: 需要登录

**成功响应** (200):

```json
{
    "code": 200,
    "message": "已读成功",
    "data": null
}
```

---

## 6. 分页规范

### 6.1 请求参数

| 参数 | 类型 | 默认值 | 说明 |
| :--- | :--- | :--- | :--- |
| `page` | Integer | 1 | 页码，从1开始 |
| `size` | Integer | 10 | 每页数量，最大50 |

### 6.2 响应结构

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "list": [],
        "total": 100,
        "page": 1,
        "size": 10
    }
}
```

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `list` | Array | 数据列表 |
| `total` | Long | 总记录数 |
| `page` | Integer | 当前页码 |
| `size` | Integer | 每页数量 |

---

## 7. RESTful 规范

### 7.1 HTTP 方法使用

| 方法 | 用途 | 示例 |
| :--- | :--- | :--- |
| `GET` | 查询资源 | `GET /api/posts` |
| `POST` | 创建资源 | `POST /api/posts` |
| `PUT` | 更新资源 | `PUT /api/user/info` |
| `DELETE` | 删除资源 | `DELETE /api/posts/{id}` |

### 7.2 资源命名

- 使用复数形式：`/api/posts` 而非 `/api/post`
- 使用连字符分隔单词：`/api/post-comments`
- 避免使用动词：使用 `POST /api/posts/{id}/like` 而非 `POST /api/posts/{id}/doLike`

### 7.3 错误处理

所有错误响应统一格式：

```json
{
    "code": 400,
    "message": "参数错误：标题不能为空",
    "data": null,
    "timestamp": 1620000000000
}
```
