# School Wall API 测试用例

> 基础URL: `http://localhost:8080`
>
> 所有接口返回格式统一，详见本文档末尾

---

## 目录

1. [健康检查](#1-健康检查)
2. [用户认证](#2-用户认证)
3. [用户管理](#3-用户管理)
4. [帖子模块](#4-帖子模块)
5. [评论模块](#5-评论模块)
6. [消息模块](#6-消息模块)
7. [搜索模块](#7-搜索模块)
8. [文件上传](#8-文件上传)
9. [统一返回格式](#9-统一返回格式)

---

## 1. 健康检查

### 1.1 健康检查

**接口地址:** `GET /api/health`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Content-Type | application/json | - |

**请求参数:** 无

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": "OK"
}
```

---

## 2. 用户认证

### 2.1 用户注册

**接口地址:** `POST /api/user/register`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Content-Type | application/json | - |

**请求参数 (JSON):**
```json
{
  "username": "testuser",
  "password": "password123",
  "nickname": "测试用户"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 (3-20字符) |
| password | string | 是 | 密码 (6-20字符) |
| nickname | string | 否 | 昵称 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

### 2.2 用户登录

**接口地址:** `POST /api/user/login`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Content-Type | application/json | - |

**请求参数 (JSON):**
```json
{
  "username": "testuser",
  "password": "password123"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "userId": 1,
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "testuser",
    "nickname": "测试用户",
    "avatar": null
  }
}
```

> **重要:** 登录成功后，需要将 `token` 保存，后续需要登录的接口需要在请求头中携带

---

## 3. 用户管理

> 以下接口除特别说明外，需要在请求头中携带 `Token`

**认证请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 登录返回的Token |

---

### 3.1 获取当前用户信息

**接口地址:** `GET /api/user/info`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**请求参数:** 无

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "nickname": "测试用户",
    "avatar": null,
    "school": null,
    "bio": null,
    "postCount": 5,
    "likeCount": 23,
    "collectCount": 3,
    "followerCount": 10,
    "followingCount": 5,
    "isFollowed": null,
    "createTime": "2024-05-01T10:30:00"
  }
}
```

---

### 3.2 获取指定用户信息

**接口地址:** `GET /api/user/{userId}`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 可选 |

**路径参数:**
| 参数 | 类型 | 说明 |
|------|------|------|
| userId | long | 用户ID |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 2,
    "username": "otheruser",
    "nickname": "其他用户",
    "avatar": null,
    "school": "XX大学",
    "bio": "这个人很懒，什么都没写",
    "postCount": 10,
    "likeCount": 50,
    "collectCount": 8,
    "followerCount": 100,
    "followingCount": 30,
    "isFollowed": true,
    "createTime": "2024-04-15T08:00:00"
  }
}
```

---

### 3.3 更新用户信息

**接口地址:** `PUT /api/user/info`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Content-Type | application/json | - |
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**请求参数 (JSON):**
```json
{
  "nickname": "新昵称",
  "avatar": "https://example.com/avatar.jpg",
  "school": "XX大学",
  "bio": "这是我的个人简介"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| nickname | string | 否 | 昵称 |
| avatar | string | 否 | 头像URL |
| school | string | 否 | 学校 |
| bio | string | 否 | 个人简介 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "更新成功",
  "data": { ... }
}
```

---

### 3.4 关注用户

**接口地址:** `POST /api/user/{userId}/follow`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**路径参数:**
| 参数 | 类型 | 说明 |
|------|------|------|
| userId | long | 要关注的用户ID |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

### 3.5 取消关注

**接口地址:** `DELETE /api/user/{userId}/follow`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**路径参数:**
| 参数 | 类型 | 说明 |
|------|------|------|
| userId | long | 要取消关注的用户ID |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

### 3.6 获取粉丝列表

**接口地址:** `GET /api/user/{userId}/followers`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 可选 |

**路径参数:**
| 参数 | 类型 | 说明 |
|------|------|------|
| userId | long | 用户ID |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 3,
      "username": "fan1",
      "nickname": "粉丝1",
      "avatar": null,
      "school": "XX大学",
      "bio": "我是粉丝",
      "isFollowed": false
    }
  ]
}
```

---

### 3.7 获取关注列表

**接口地址:** `GET /api/user/{userId}/following`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 可选 |

**路径参数:**
| 参数 | 类型 | 说明 |
|------|------|------|
| userId | long | 用户ID |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 4,
      "username": "following1",
      "nickname": "关注1",
      "avatar": null,
      "school": "XX大学",
      "bio": "我关注的人",
      "isFollowed": true
    }
  ]
}
```

---

### 3.8 搜索用户

**接口地址:** `GET /api/user/search`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 可选 |

**请求参数:**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 是 | 搜索关键字 |

**示例:** `GET /api/user/search?keyword=张三`

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 5,
      "username": "zhangsan",
      "nickname": "张三",
      "avatar": null,
      "school": "XX大学",
      "bio": "学生",
      "followerCount": 20,
      "followingCount": 10,
      "postCount": 5,
      "likeCount": 30,
      "isFollowed": false,
      "createTime": "2024-05-10T12:00:00"
    }
  ]
}
```

---

## 4. 帖子模块

### 4.1 获取帖子列表（普通分页）

**接口地址:** `GET /api/posts`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 可选 |

**请求参数:**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页数量 |
| type | string | 否 | latest | 排序类型: latest/hot/hole/love |
| tag | string | 否 | - | 话题标签筛选 |

**示例:** `GET /api/posts?page=1&size=10&type=latest`

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "帖子标题",
        "content": "帖子内容",
        "images": ["https://example.com/img1.jpg"],
        "tag": "校园生活",
        "tagColor": "blue",
        "userId": 1,
        "username": "testuser",
        "nickname": "测试用户",
        "avatar": null,
        "likes": 10,
        "comments": 5,
        "collects": 3,
        "isLiked": false,
        "isCollected": false,
        "isAnon": 0,
        "createTime": "2024-05-20T15:30:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

---

### 4.2 获取帖子列表（游标分页 - 无限滚动）

**接口地址:** `GET /api/posts/cursor`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 可选 |

**请求参数:**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| cursor | long | 否 | null | 游标时间戳（首次请求为null） |
| size | int | 否 | 10 | 每页数量 |
| type | string | 否 | latest | 排序类型: latest/hot/hole/love |
| tag | string | 否 | - | 话题标签筛选 |

**首次请求示例:** `GET /api/posts/cursor?size=10&type=latest`

**滚动加载示例:** `GET /api/posts/cursor?cursor=1716200000&size=10&type=latest`

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [ ... ],
    "nextCursor": 1716190000,
    "hasMore": true,
    "count": 10
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| records | array | 帖子列表 |
| nextCursor | long | 下一页游标时间戳，为空表示没有更多数据 |
| hasMore | boolean | 是否还有更多数据 |
| count | int | 本次返回的数据数量 |

---

### 4.3 获取帖子详情

**接口地址:** `GET /api/posts/{postId}`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 可选 |

**路径参数:**
| 参数 | 类型 | 说明 |
|------|------|------|
| postId | long | 帖子ID |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1,
    "title": "帖子标题",
    "content": "帖子内容",
    "images": ["https://example.com/img1.jpg"],
    "tag": "校园生活",
    "tagColor": "blue",
    "userId": 1,
    "username": "testuser",
    "nickname": "测试用户",
    "avatar": null,
    "likes": 10,
    "comments": 5,
    "collects": 3,
    "isLiked": false,
    "isCollected": false,
    "isAnon": 0,
    "createTime": "2024-05-20T15:30:00",
    "commentList": [
      {
        "id": 1,
        "content": "评论内容",
        "userId": 2,
        "username": "commenter",
        "nickname": "评论者",
        "avatar": null,
        "likes": 2,
        "isLiked": false,
        "createTime": "2024-05-20T16:00:00"
      }
    ]
  }
}
```

---

### 4.4 发布帖子

**接口地址:** `POST /api/posts`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Content-Type | application/json | - |
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**请求参数 (JSON):**
```json
{
  "title": "帖子标题",
  "content": "这是帖子内容",
  "images": ["https://example.com/img1.jpg"],
  "tag": "校园生活",
  "isAnon": 0
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 否 | 标题 |
| content | string | 是 | 内容 |
| images | array | 否 | 图片URL数组 |
| tag | string | 否 | 话题标签 |
| isAnon | int | 否 | 是否匿名 (0: 否, 1: 是) |

**响应示例:**
```json
{
  "code": 200,
  "msg": "发布成功",
  "data": {
    "id": 10,
    "title": "帖子标题",
    "content": "这是帖子内容",
    "images": ["https://example.com/img1.jpg"],
    "tag": "校园生活",
    ...
  }
}
```

---

### 4.5 点赞帖子

**接口地址:** `POST /api/posts/{postId}/like`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**路径参数:**
| 参数 | 类型 | 说明 |
|------|------|------|
| postId | long | 帖子ID |

**响应示例:**
```json
{
  "code": 200,
  "msg": "点赞成功",
  "data": {
    "postId": 1,
    "liked": true,
    "likes": 11
  }
}
```

---

### 4.6 收藏帖子

**接口地址:** `POST /api/posts/{postId}/collect`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**路径参数:**
| 参数 | 类型 | 说明 |
|------|------|------|
| postId | long | 帖子ID |

**响应示例:**
```json
{
  "code": 200,
  "msg": "收藏成功",
  "data": {
    "postId": 1,
    "collected": true,
    "collects": 4
  }
}
```

---

### 4.7 删除帖子

**接口地址:** `DELETE /api/posts/{postId}`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**路径参数:**
| 参数 | 类型 | 说明 |
|------|------|------|
| postId | long | 帖子ID |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

### 4.8 获取我的帖子

**接口地址:** `GET /api/posts/mine`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**请求参数:**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页数量 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [ ... ],
    "total": 5,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

### 4.9 获取我的收藏

**接口地址:** `GET /api/posts/collections`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**请求参数:**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页数量 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [ ... ],
    "total": 3,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

### 4.10 获取热门话题标签

**接口地址:** `GET /api/posts/tags`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Content-Type | application/json | - |

**请求参数:**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| limit | int | 否 | 10 | 返回数量 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "name": "校园生活",
      "color": "blue",
      "count": 50
    },
    {
      "name": "学习",
      "color": "green",
      "count": 30
    }
  ]
}
```

---

### 4.11 搜索可@的用户

**接口地址:** `GET /api/posts/mentioned`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 可选 |

**请求参数:**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 是 | 搜索关键字 |

**示例:** `GET /api/posts/mentioned?keyword=张三`

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 5,
      "username": "zhangsan",
      "nickname": "张三",
      "avatar": null,
      "school": "XX大学",
      "bio": "学生",
      "isFollowed": true
    }
  ]
}
```

---

## 5. 评论模块

### 5.1 获取评论列表

**接口地址:** `GET /api/comments`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 可选 |

**请求参数:**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| postId | long | 是 | - | 帖子ID |
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 20 | 每页数量 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "content": "评论内容",
        "userId": 2,
        "username": "commenter",
        "nickname": "评论者",
        "avatar": null,
        "likes": 5,
        "isLiked": false,
        "createTime": "2024-05-20T16:00:00"
      }
    ],
    "total": 20,
    "size": 20,
    "current": 1,
    "pages": 1
  }
}
```

---

### 5.2 发布评论

**接口地址:** `POST /api/comments`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Content-Type | application/json | - |
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**请求参数 (JSON):**
```json
{
  "postId": 1,
  "content": "这是一条评论"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| postId | long | 是 | 帖子ID |
| content | string | 是 | 评论内容 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "评论成功",
  "data": {
    "id": 10,
    "postId": 1,
    "content": "这是一条评论",
    "userId": 1,
    "likes": 0,
    "isLiked": false,
    "createTime": "2024-05-20T17:00:00"
  }
}
```

---

### 5.3 点赞评论

**接口地址:** `POST /api/comments/{commentId}/like`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**路径参数:**
| 参数 | 类型 | 说明 |
|------|------|------|
| commentId | long | 评论ID |

**响应示例:**
```json
{
  "code": 200,
  "msg": "点赞成功",
  "data": {
    "commentId": 1,
    "liked": true,
    "likes": 6
  }
}
```

---

### 5.4 删除评论

**接口地址:** `DELETE /api/comments/{commentId}`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**路径参数:**
| 参数 | 类型 | 说明 |
|------|------|------|
| commentId | long | 评论ID |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

## 6. 消息模块

### 6.1 获取会话列表

**接口地址:** `GET /api/messages/conversations`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "userId": 2,
      "username": "friend1",
      "nickname": "好友1",
      "avatar": null,
      "lastMessage": "你好呀",
      "lastMessageTime": "2024-05-20T18:00:00",
      "unreadCount": 2
    }
  ]
}
```

---

### 6.2 搜索联系人

**接口地址:** `GET /api/messages/search`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**请求参数:**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 是 | 搜索关键字 |

**示例:** `GET /api/messages/search?keyword=张三`

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "userId": 5,
      "username": "zhangsan",
      "nickname": "张三",
      "avatar": null,
      "school": "XX大学",
      "lastMessage": "嗨",
      "lastMessageTime": "2024-05-20T17:30:00"
    }
  ]
}
```

---

### 6.3 获取聊天记录

**接口地址:** `GET /api/messages/chat/{userId}`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**路径参数:**
| 参数 | 类型 | 说明 |
|------|------|------|
| userId | long | 目标用户ID |

**请求参数:**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 20 | 每页数量 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "fromId": 1,
        "toId": 2,
        "content": "你好",
        "type": "text",
        "isRead": true,
        "createTime": "2024-05-20T10:00:00"
      },
      {
        "id": 2,
        "fromId": 2,
        "toId": 1,
        "content": "你好呀",
        "type": "text",
        "isRead": false,
        "createTime": "2024-05-20T10:01:00"
      }
    ],
    "total": 50,
    "size": 20,
    "current": 1,
    "pages": 3
  }
}
```

---

### 6.4 发送消息

**接口地址:** `POST /api/messages/send`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Content-Type | application/json | - |
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**请求参数 (JSON):**
```json
{
  "toId": 2,
  "content": "你好",
  "type": "text"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| toId | long | 是 | 接收方用户ID |
| content | string | 是 | 消息内容 |
| type | string | 否 | 消息类型: text/image，默认为text |

**响应示例:**
```json
{
  "code": 200,
  "msg": "发送成功",
  "data": {
    "id": 100,
    "fromId": 1,
    "toId": 2,
    "content": "你好",
    "type": "text",
    "isRead": false,
    "createTime": "2024-05-20T18:00:00"
  }
}
```

---

### 6.5 标记消息已读

**接口地址:** `PUT /api/messages/read/{userId}`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**路径参数:**
| 参数 | 类型 | 说明 |
|------|------|------|
| userId | long | 目标用户ID |

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

## 7. 搜索模块

### 7.1 综合搜索

**接口地址:** `GET /api/search`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Content-Type | application/json | - |

**请求参数:**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| keyword | string | 是 | - | 搜索关键词 |
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页数量 |

**示例:** `GET /api/search?keyword=校园&page=1&size=10`

**响应示例:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "users": [
      {
        "id": 5,
        "username": "zhangsan",
        "nickname": "张三",
        "avatar": null,
        "school": "XX大学",
        "bio": "学生",
        "isFollowed": false
      }
    ],
    "posts": [
      {
        "id": 1,
        "title": "校园生活",
        "content": "校园生活真美好",
        "images": [],
        "tag": "校园生活",
        "tagColor": "blue",
        "userId": 5,
        "username": "zhangsan",
        "nickname": "张三",
        "avatar": null,
        "likes": 10,
        "comments": 5,
        "collects": 3,
        "isLiked": false,
        "isCollected": false,
        "isAnon": 0,
        "createTime": "2024-05-20T15:30:00"
      }
    ],
    "total": 15
  }
}
```

---

## 8. 文件上传

### 8.1 通用图片上传

**接口地址:** `POST /api/upload/image`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 可选 |

**请求参数 (FormData):**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 图片文件 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "上传成功",
  "data": {
    "url": "https://example.com/uploads/images/20240520183000_abc123.jpg"
  }
}
```

---

### 8.2 帖子图片上传

**接口地址:** `POST /api/posts/image/upload`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 可选 |

**请求参数 (FormData):**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 图片文件 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "上传成功",
  "data": {
    "url": "https://example.com/uploads/posts/20240520183000_abc123.jpg"
  }
}
```

---

### 8.3 用户头像上传

**接口地址:** `POST /api/user/avatar`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**请求参数 (FormData):**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 头像图片文件 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "上传成功",
  "data": {
    "url": "https://example.com/uploads/avatar/20240520183000_abc123.jpg"
  }
}
```

---

### 8.4 消息图片上传

**接口地址:** `POST /api/messages/image/upload`

**请求头:**
| 参数 | 示例 | 说明 |
|------|------|------|
| Authorization | Token eyJhbGciOiJIUzI1NiJ9... | 必填 |

**请求参数 (FormData):**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 图片文件 |

**响应示例:**
```json
{
  "code": 200,
  "msg": "上传成功",
  "data": {
    "url": "https://example.com/uploads/messages/20240520183000_abc123.jpg"
  }
}
```

---

## 9. 统一返回格式

所有接口统一返回以下格式:

```json
{
  "code": 200,
  "msg": "success",
  "data": { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码 (200=成功, 401=未登录, 400=请求错误, 404=资源不存在, 500=服务器错误) |
| msg | string | 提示信息 |
| data | object | 响应数据 (部分接口可能为null) |

### 常见状态码

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或Token过期 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 注意事项

1. **文件上传大小限制**: 单个文件最大 10MB
2. **支持图片格式**: JPEG, PNG, GIF, WEBP
3. **Token有效期**: 7天
4. **分页参数**: 大部分列表接口支持 `page` 和 `size` 参数
5. **无限滚动**: 推荐使用 `GET /api/posts/cursor` 接口实现无限滚动效果
