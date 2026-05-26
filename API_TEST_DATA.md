# 校园微墙 - Apifox/Postman 接口测试数据

## 目录

1. [环境配置](#环境配置)
2. [Token 获取](#token-获取)
3. [用户模块测试](#用户模块测试)
4. [帖子模块测试](#帖子模块测试)
5. [评论模块测试](#评论模块测试)
6. [搜索模块测试](#搜索模块测试)
7. [消息模块测试](#消息模块测试)
8. [分页参数说明](#分页参数说明)

---

## 环境配置

### 1. 基础配置

| 配置项 | 值 |
|--------|-----|
| **Base URL** | `http://localhost:8080/api` |
| **Content-Type** | `application/json` |

### 2. 环境变量

| 变量名 | 值 | 说明 |
|--------|-----|------|
| `baseUrl` | `http://localhost:8080/api` | API 基础路径 |
| `token` | `{{login_response_token}}` | 登录后获取的 Token |
| `userId` | `1` | 当前登录用户ID |

### 3. 全局请求头

| Key | Value | 说明 |
|-----|-------|------|
| `Content-Type` | `application/json` | 请求内容类型 |
| `Authorization` | `Bearer {{token}}` | 认证 Token（需要登录的接口） |

---

## Token 获取

### 登录接口

**接口**: `POST {{baseUrl}}/user/login`

**请求参数**:
```json
{
  "username": "chengzi",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiY2hlbmd6aSIsIm5pY2tuYW1lIjoi6Z2S5b2x5Z2b5Z2bIiwiYXZhdGFyIjoi8J+PiiIsInNjaG9vbCI6IuWbvueJh+e7reWImCIsImV4cCI6MTcxNjY4NzQwMH0.abc123def456",
    "user": {
      "id": 1,
      "username": "chengzi",
      "nickname": "橙子不甜",
      "avatar": "🍊",
      "school": "某某大学",
      "bio": "喜欢吃喝玩乐，摸鱼度日🐟"
    }
  }
}
```

**Apifox 后置脚本**（自动提取 Token）:
```javascript
const response = pm.response.json();
if (response.code === 200 && response.data.token) {
    pm.environment.set("token", response.data.token);
    pm.environment.set("userId", response.data.user.id);
    console.log("Token 已更新:", response.data.token);
}
```

---

## 用户模块测试

### 1.1 用户登录

**接口**: `POST {{baseUrl}}/user/login`

**请求参数**:
```json
{
  "username": "chengzi",
  "password": "123456"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "chengzi",
      "nickname": "橙子不甜",
      "avatar": "🍊",
      "school": "某某大学",
      "bio": "喜欢吃喝玩乐，摸鱼度日🐟"
    }
  }
}
```

---

### 1.2 获取当前用户信息

**接口**: `GET {{baseUrl}}/user/info`

**请求头**:
```
Authorization: Bearer {{token}}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "chengzi",
    "nickname": "橙子不甜",
    "avatar": "🍊",
    "school": "某某大学",
    "bio": "喜欢吃喝玩乐，摸鱼度日🐟",
    "postCount": 12,
    "likeCount": 286,
    "collectCount": 43,
    "createTime": "2024-01-15 10:30:00"
  }
}
```

---

### 1.3 更新用户信息

**接口**: `PUT {{baseUrl}}/user/info`

**请求头**:
```
Authorization: Bearer {{token}}
```

**请求参数**:
```json
{
  "nickname": "新昵称",
  "avatar": "https://example.com/avatar.jpg",
  "school": "新学校",
  "bio": "新的个人简介"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "username": "chengzi",
    "nickname": "新昵称",
    "avatar": "https://example.com/avatar.jpg",
    "school": "新学校",
    "bio": "新的个人简介",
    "postCount": 12,
    "likeCount": 286,
    "collectCount": 43
  }
}
```

---

### 1.4 获取其他用户信息

**接口**: `GET {{baseUrl}}/user/2`

**请求头**:
```
Authorization: Bearer {{token}}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2,
    "username": "shuxue",
    "nickname": "数学学长",
    "avatar": "🦁",
    "school": "某某大学",
    "bio": "数学系大三，喜欢数学分析",
    "postCount": 25,
    "likeCount": 1520,
    "collectCount": 89,
    "isFollowed": false
  }
}
```

---

### 1.5 关注用户

**接口**: `POST {{baseUrl}}/user/2/follow`

**请求头**:
```
Authorization: Bearer {{token}}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "关注成功",
  "data": null
}
```

---

### 1.6 取消关注

**接口**: `DELETE {{baseUrl}}/user/2/follow`

**请求头**:
```
Authorization: Bearer {{token}}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "取消关注成功",
  "data": null
}
```

---

## 帖子模块测试

### 2.1 获取帖子列表

**接口**: `GET {{baseUrl}}/posts`

**请求参数**:
```
page=1
size=10
type=latest
tag=美食
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "北区食堂新出的麻辣香锅真的绝了！！",
        "content": "今天中午去北区食堂吃饭，偶然发现新开了个麻辣香锅窗口...",
        "images": [
          "https://example.com/food1.jpg",
          "https://example.com/food2.jpg"
        ],
        "tag": "美食",
        "tagColor": "orange",
        "isAnon": true,
        "author": "匿名用户",
        "authorAvatar": "🐼",
        "authorId": 1,
        "likes": 128,
        "liked": false,
        "collected": false,
        "commentCount": 34,
        "time": "10分钟前",
        "fullTime": "2024-03-20 12:30:00",
        "createTime": "2024-03-20 12:30:00"
      }
    ],
    "total": 18,
    "size": 10,
    "current": 1,
    "pages": 2
  }
}
```

**分页参数示例**:
```
# 第一页，每页10条
page=1&size=10

# 第二页，每页20条
page=2&size=20

# 热门帖子
type=hot

# 树洞帖子
type=hole

# 表白帖子
type=love

# 筛选美食标签
tag=美食

# 筛选学习标签
tag=学习
```

---

### 2.2 获取帖子详情

**接口**: `GET {{baseUrl}}/posts/1`

**请求头**:
```
Authorization: Bearer {{token}}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "北区食堂新出的麻辣香锅真的绝了！！",
    "content": "今天中午去北区食堂吃饭，偶然发现新开了个麻辣香锅窗口，随手点了一份，结果完全惊艳到我了！！\n\n食材超级新鲜，锅底是那种红汤的，麻辣程度可以自选，我选了中辣刚刚好。里面有午餐肉、腐竹、莲藕、土豆...价格也很实惠，才18块钱。\n\n强烈推荐大家去试试！就在北区食堂二楼最右边那个窗口，记得早点去不然要排长队的！",
    "images": [
      "https://example.com/food1.jpg",
      "https://example.com/food2.jpg"
    ],
    "tag": "美食",
    "tagColor": "orange",
    "isAnon": true,
    "author": "匿名用户",
    "authorAvatar": "🐼",
    "authorId": 1,
    "likes": 128,
    "liked": false,
    "collected": false,
    "commentCount": 34,
    "time": "10分钟前",
    "fullTime": "2024-03-20 12:30:00",
    "createTime": "2024-03-20 12:30:00",
    "comments": [
      {
        "id": 1,
        "postId": 1,
        "author": "小橘子同学",
        "avatar": "🐱",
        "authorId": 3,
        "content": "已经约好舍友明天中午去！感谢博主强推！🔥🔥",
        "time": "12:45",
        "fullTime": "2024-03-20 12:45:00",
        "likes": 23,
        "liked": false,
        "isAuthor": false
      },
      {
        "id": 2,
        "postId": 1,
        "author": "匿名树洞",
        "avatar": "🐼",
        "authorId": 4,
        "content": "北区食堂要排队的，我今天特意绕过去看了，队伍排到门口了哈哈哈",
        "time": "13:00",
        "fullTime": "2024-03-20 13:00:00",
        "likes": 11,
        "liked": false,
        "isAuthor": false
      }
    ]
  }
}
```

---

### 2.3 发布帖子

**接口**: `POST {{baseUrl}}/posts`

**请求头**:
```
Authorization: Bearer {{token}}
```

**请求参数**:
```json
{
  "title": "今天天气真好",
  "content": "阳光明媚，适合出去走走～",
  "images": [
    "https://example.com/sunny.jpg"
  ],
  "tag": "校园生活",
  "tagColor": "blue",
  "isAnon": false
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "id": 100,
    "title": "今天天气真好",
    "content": "阳光明媚，适合出去走走～",
    "images": [
      "https://example.com/sunny.jpg"
    ],
    "tag": "校园生活",
    "tagColor": "blue",
    "isAnon": false,
    "author": "橙子不甜",
    "authorAvatar": "🍊",
    "authorId": 1,
    "likes": 0,
    "liked": false,
    "collected": false,
    "commentCount": 0,
    "time": "刚刚",
    "createTime": "2024-03-20 14:00:00"
  }
}
```

**匿名发布示例**:
```json
{
  "title": "匿名吐槽",
  "content": "今天食堂的饭菜太难吃了...",
  "tag": "吐槽",
  "tagColor": "gray",
  "isAnon": true
}
```

---

### 2.4 点赞帖子

**接口**: `POST {{baseUrl}}/posts/1/like`

**请求头**:
```
Authorization: Bearer {{token}}
```

**响应示例**:
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

**取消点赞响应**:
```json
{
  "code": 200,
  "message": "取消点赞成功",
  "data": {
    "liked": false,
    "likes": 128
  }
}
```

---

### 2.5 收藏帖子

**接口**: `POST {{baseUrl}}/posts/1/collect`

**请求头**:
```
Authorization: Bearer {{token}}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "收藏成功",
  "data": {
    "collected": true
  }
}
```

**取消收藏响应**:
```json
{
  "code": 200,
  "message": "取消收藏成功",
  "data": {
    "collected": false
  }
}
```

---

### 2.6 删除帖子

**接口**: `DELETE {{baseUrl}}/posts/100`

**请求头**:
```
Authorization: Bearer {{token}}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 2.7 获取我的帖子

**接口**: `GET {{baseUrl}}/posts/mine`

**请求头**:
```
Authorization: Bearer {{token}}
```

**请求参数**:
```
page=1
size=10
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 100,
        "title": "今天天气真好",
        "content": "阳光明媚，适合出去走走～",
        "images": [],
        "tag": "校园生活",
        "tagColor": "blue",
        "isAnon": false,
        "author": "橙子不甜",
        "authorAvatar": "🍊",
        "authorId": 1,
        "likes": 5,
        "liked": false,
        "collected": false,
        "commentCount": 2,
        "time": "刚刚",
        "createTime": "2024-03-20 14:00:00"
      }
    ],
    "total": 12,
    "size": 10,
    "current": 1,
    "pages": 2
  }
}
```

---

### 2.8 获取我的收藏

**接口**: `GET {{baseUrl}}/posts/collections`

**请求头**:
```
Authorization: Bearer {{token}}
```

**请求参数**:
```
page=1
size=10
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 5,
        "title": "分享一个超好用的备考资料整理网站",
        "content": "期末季到了，分享给大家一个整理好的备考资源站...",
        "images": [
          "https://example.com/website.png"
        ],
        "tag": "资源",
        "tagColor": "green",
        "isAnon": false,
        "author": "学习委员本委",
        "authorAvatar": "📖",
        "authorId": 5,
        "likes": 430,
        "liked": true,
        "collected": true,
        "commentCount": 62,
        "time": "3小时前",
        "createTime": "2024-03-16 09:30:00"
      }
    ],
    "total": 43,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

---

## 评论模块测试

### 3.1 获取评论列表

**接口**: `GET {{baseUrl}}/comments`

**请求参数**:
```
postId=1
page=1
size=20
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "postId": 1,
        "author": "小橘子同学",
        "avatar": "🐱",
        "authorId": 3,
        "content": "已经约好舍友明天中午去！感谢博主强推！🔥🔥",
        "time": "12:45",
        "fullTime": "2024-03-20 12:45:00",
        "likes": 23,
        "liked": false,
        "isAuthor": false
      },
      {
        "id": 2,
        "postId": 1,
        "author": "匿名树洞",
        "avatar": "🐼",
        "authorId": 4,
        "content": "北区食堂要排队的，我今天特意绕过去看了，队伍排到门口了哈哈哈",
        "time": "13:00",
        "fullTime": "2024-03-20 13:00:00",
        "likes": 11,
        "liked": false,
        "isAuthor": false
      }
    ],
    "total": 34,
    "size": 20,
    "current": 1,
    "pages": 2
  }
}
```

---

### 3.2 发布评论

**接口**: `POST {{baseUrl}}/comments`

**请求头**:
```
Authorization: Bearer {{token}}
```

**请求参数**:

```json
{
  "postId": 1,
  "content": "我也想去试试！"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "评论成功",
  "data": {
    "id": 100,
    "postId": 1,
    "content": "我也想去试试！",
    "time": "刚刚",
    "fullTime": "2024-03-20 14:05:00"
  }
}
```

---

### 3.3 点赞评论

**接口**: `POST {{baseUrl}}/comments/1/like`

**请求头**:
```
Authorization: Bearer {{token}}
```

**响应示例**:
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

**取消点赞响应**:
```json
{
  "code": 200,
  "message": "取消点赞成功",
  "data": {
    "liked": false,
    "likes": 23
  }
}
```

---

### 3.4 删除评论

**接口**: `DELETE {{baseUrl}}/comments/100`

**请求头**:
```
Authorization: Bearer {{token}}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 搜索模块测试

### 4.1 搜索

**接口**: `GET {{baseUrl}}/search`

**请求参数**:
```
keyword=美食
page=1
size=10
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "users": [
      {
        "id": 6,
        "nickname": "美食探索者",
        "avatar": "🍔",
        "school": "某某大学",
        "postCount": 18
      }
    ],
    "posts": [
      {
        "id": 1,
        "title": "北区食堂新出的麻辣香锅真的绝了！！",
        "content": "今天中午去北区食堂吃饭，偶然发现新开了个麻辣香锅窗口...",
        "author": "匿名用户",
        "likes": 128,
        "commentCount": 34,
        "time": "10分钟前"
      },
      {
        "id": 6,
        "title": "南门小吃街的烤冷面YYDS！",
        "content": "南门门口那家烤冷面真的绝了，加蛋加肠才8块钱...",
        "author": "美食探索者",
        "likes": 256,
        "commentCount": 38,
        "time": "5天前"
      }
    ],
    "userTotal": 1,
    "postTotal": 2
  }
}
```

**搜索关键词示例**:
```
# 搜索用户
keyword=数学学长

# 搜索帖子
keyword=学习

# 搜索资源
keyword=资料

# 搜索表白
keyword=表白
```

---

## 消息模块测试

### 5.1 获取会话列表

**接口**: `GET {{baseUrl}}/messages/conversations`

**请求头**:
```
Authorization: Bearer {{token}}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 2,
      "name": "数学学长",
      "avatar": "🦁",
      "lastMsg": "可以，明天下午图书馆见！",
      "lastTime": "刚刚",
      "unread": 2
    },
    {
      "id": 2,
      "userId": 5,
      "name": "学习委员本委",
      "avatar": "📖",
      "lastMsg": "好的收到，我转给负责人",
      "lastTime": "10分钟前",
      "unread": 0
    }
  ]
}
```

---

### 5.2 获取聊天记录

**接口**: `GET {{baseUrl}}/messages/chat/2`

**请求头**:
```
Authorization: Bearer {{token}}
```

**请求参数**:
```
page=1
size=20
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "fromId": 2,
        "toId": 1,
        "content": "你好，我看了你发的那道数分题",
        "type": "text",
        "time": "12:20",
        "fullTime": "2024-03-19 15:55:00",
        "fromMe": false,
        "isRead": true
      },
      {
        "id": 2,
        "fromId": 1,
        "toId": 2,
        "content": "啊好的！你有思路吗",
        "type": "text",
        "time": "12:21",
        "fullTime": "2024-03-19 15:56:00",
        "fromMe": true,
        "isRead": true
      },
      {
        "id": 3,
        "fromId": 2,
        "toId": 1,
        "content": "考虑辅助函数 g(x)=e^x·f(x)，对 g 用罗尔定理就行",
        "type": "text",
        "time": "12:23",
        "fullTime": "2024-03-19 15:58:00",
        "fromMe": false,
        "isRead": true
      },
      {
        "id": 4,
        "fromId": 1,
        "toId": 2,
        "content": "哇！懂了！那你有空可以帮我看看其他几道吗😭",
        "type": "text",
        "time": "12:25",
        "fullTime": "2024-03-19 16:00:00",
        "fromMe": true,
        "isRead": true
      },
      {
        "id": 5,
        "fromId": 2,
        "toId": 1,
        "content": "可以，明天下午图书馆见！",
        "type": "text",
        "time": "12:27",
        "fullTime": "2024-03-19 16:02:00",
        "fromMe": false,
        "isRead": false
      }
    ],
    "total": 5,
    "size": 20,
    "current": 1,
    "pages": 1
  }
}
```

---

### 5.3 发送消息

**接口**: `POST {{baseUrl}}/messages/send`

**请求头**:
```
Authorization: Bearer {{token}}
```

**请求参数**:
```json
{
  "toId": 2,
  "content": "好的，明天见！",
  "type": "text"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "发送成功",
  "data": {
    "id": 100,
    "fromId": 1,
    "toId": 2,
    "content": "好的，明天见！",
    "type": "text",
    "time": "刚刚",
    "fullTime": "2024-03-20 14:10:00",
    "fromMe": true,
    "isRead": false
  }
}
```

**发送图片消息示例**:
```json
{
  "toId": 2,
  "content": "https://example.com/image.jpg",
  "type": "image"
}
```

---

### 5.4 标记消息已读

**接口**: `PUT {{baseUrl}}/messages/read/2`

**请求头**:
```
Authorization: Bearer {{token}}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "已读成功",
  "data": null
}
```

---

## 分页参数说明

### 通用分页参数

| 参数 | 类型 | 默认值 | 说明 | 示例 |
|------|------|--------|------|------|
| `page` | Integer | 1 | 页码，从1开始 | `page=1` |
| `size` | Integer | 10 | 每页数量，最大50 | `size=10` |

### 分页响应结构

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],      // 数据列表
    "total": 100,       // 总记录数
    "size": 10,         // 每页数量
    "current": 1,       // 当前页码
    "pages": 10         // 总页数
  }
}
```

### 分页参数示例

```bash
# 第一页，每页10条
page=1&size=10

# 第二页，每页20条
page=2&size=20

# 最后一页（假设总记录数为100，每页10条）
page=10&size=10

# 每页50条（最大值）
page=1&size=50
```

### 计算总页数

```
总页数 = Math.ceil(总记录数 / 每页数量)
```

例如：
- 总记录数 100，每页 10 条 → 10 页
- 总记录数 95，每页 10 条 → 10 页
- 总记录数 101，每页 10 条 → 11 页

---

## 错误响应示例

### 400 参数错误

```json
{
  "code": 400,
  "message": "参数错误：标题不能为空",
  "data": null,
  "timestamp": 1710936000000
}
```

### 401 未登录

```json
{
  "code": 401,
  "message": "请先登录",
  "data": null,
  "timestamp": 1710936000000
}
```

### 401 Token 过期

```json
{
  "code": 401,
  "message": "登录已过期，请重新登录",
  "data": null,
  "timestamp": 1710936000000
}
```

### 403 无权限

```json
{
  "code": 403,
  "message": "无权限操作",
  "data": null,
  "timestamp": 1710936000000
}
```

### 404 资源不存在

```json
{
  "code": 404,
  "message": "帖子不存在",
  "data": null,
  "timestamp": 1710936000000
}
```

### 500 服务器错误

```json
{
  "code": 500,
  "message": "服务器内部错误",
  "data": null,
  "timestamp": 1710936000000
}
```

---

## 测试账号列表

| 用户名 | 密码 | 昵称 | 头像 | 说明 |
|--------|------|------|------|------|
| `chengzi` | `123456` | 橙子不甜 | 🍊 | 普通用户，有12条帖子 |
| `shuxue` | `123456` | 数学学长 | 🦁 | 学长用户，有25条帖子 |
| `xiaojuzi` | `123456` | 小橘子同学 | 🐱 | 新生用户，有8条帖子 |
| `anonym` | `123456` | 匿名树洞 | 🐼 | 喜欢匿名发帖 |
| `xuexi` | `123456` | 学习委员本委 | 📖 | 分享学习资源 |

---

## Apifox 导入提示

1. **创建环境变量**：
   
   - `baseUrl`: `http://localhost:8080/api`
   - `token`: 登录后自动获取
   
2. **配置全局请求头**：
   - `Content-Type`: `application/json`
   - `Authorization`: `Bearer {{token}}`

3. **登录接口后置脚本**：
   ```javascript
   const response = pm.response.json();
   if (response.code === 200 && response.data.token) {
       pm.environment.set("token", response.data.token);
       pm.environment.set("userId", response.data.user.id);
   }
   ```

4. **测试流程**：
   - 先调用登录接口获取 Token
   - Token 会自动保存到环境变量
   - 后续接口会自动带上 Token

---

## Postman 导入提示

1. **创建环境**：
   - 变量名：`baseUrl`，初始值：`http://localhost:8080/api`
   - 变量名：`token`，初始值：空

2. **配置全局请求头**：
   - 在 Collection → Authorization 中设置
   - Type: `Bearer Token`
   - Token: `{{token}}`

3. **登录接口 Tests 脚本**：
   ```javascript
   const response = pm.response.json();
   if (response.code === 200 && response.data.token) {
       pm.environment.set("token", response.data.token);
       pm.environment.set("userId", response.data.user.id);
   }
   ```

4. **测试流程**：
   - 先执行登录请求
   - Token 会自动保存到环境变量
   - 其他接口会自动使用 Token

---

**文档版本**: v1.0  
**更新时间**: 2024-03-20  
**适用版本**: 校园微墙后端 v1.0