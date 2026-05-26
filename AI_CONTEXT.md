# School Wall Backend AI Context

## Purpose

This repo root mainly hosts the backend project in `backend/` and shared SQL/assets such as:

- `TEST_DATA.sql`: richer test dataset for frontend/backend integration
- `backend/DATABASE.sql`: schema bootstrap script
- `uploads/`: uploaded files served by backend static resource mapping

If you are working on backend code, most changes happen under `backend/`.

## Stack

- Java 21
- Spring Boot 3
- MyBatis Plus
- MySQL
- JWT auth
- Knife4j / OpenAPI

## Runbook

1. Import schema:
   - `backend/DATABASE.sql`
2. Import test data:
   - `TEST_DATA.sql`
3. Start backend from `backend/`
4. Default HTTP port: `8080`
5. API docs:
   - `http://localhost:8080/doc.html`

## Important Paths

### Config

- `backend/src/main/resources/application.yml`
- `backend/src/main/java/com/example/schoolwall/config/WebConfig.java`
- `backend/src/main/java/com/example/schoolwall/config/MyBatisPlusConfig.java`
- `backend/src/main/java/com/example/schoolwall/config/FileUploadConfig.java`

### Security

- `backend/src/main/java/com/example/schoolwall/security/JwtInterceptor.java`
- `backend/src/main/java/com/example/schoolwall/security/JwtTokenUtil.java`
- `backend/src/main/java/com/example/schoolwall/security/PasswordEncoder.java`

### Controllers

- `backend/src/main/java/com/example/schoolwall/controller/UserController.java`
- `backend/src/main/java/com/example/schoolwall/controller/PostController.java`
- `backend/src/main/java/com/example/schoolwall/controller/CommentController.java`
- `backend/src/main/java/com/example/schoolwall/controller/MessageController.java`
- `backend/src/main/java/com/example/schoolwall/controller/FileUploadController.java`
- `backend/src/main/java/com/example/schoolwall/controller/SearchController.java`

### Services With Core Business Logic

- `backend/src/main/java/com/example/schoolwall/service/impl/PostServiceImpl.java`
- `backend/src/main/java/com/example/schoolwall/service/impl/CommentServiceImpl.java`
- `backend/src/main/java/com/example/schoolwall/service/impl/MessageServiceImpl.java`
- `backend/src/main/java/com/example/schoolwall/service/impl/AuthServiceImpl.java`
- `backend/src/main/java/com/example/schoolwall/service/impl/SearchServiceImpl.java`

### Data Model

- `backend/src/main/java/com/example/schoolwall/entity/`
- Main tables:
  - `user`
  - `post`
  - `comment`
  - `message`
  - `follow_record`
  - `collect_record`
  - `like_record`

## API Surface

### User

- `/api/user/login`
- `/api/user/info`
- `/api/user/{userId}`
- `/api/user/{userId}/followers`
- `/api/user/{userId}/following`

### Posts

- `/api/posts`
- `/api/posts/{postId}`
- `/api/posts/mine`
- `/api/posts/user/{userId}`
- `/api/posts/collections`
- `/api/posts/{postId}/like`
- `/api/posts/{postId}/collect`

### Comments

- `/api/comments`
- `/api/comments/{commentId}/like`

### Messages

- `/api/messages/conversations`
- `/api/messages/chat/{userId}`
- `/api/messages/send`
- `/api/messages/read/{userId}`

### Upload

- `/api/posts/image/upload`
- `/api/messages/image/upload`
- `/api/user/avatar`

## Current Integration Assumptions

- Frontend expects unified response shape:
  - `code`
  - `message`
  - `data`
- Frontend uses a seeded test account:
  - `chengzi / 123456`
- Frontend expects image URLs to be directly displayable.
  - Remote URLs work as-is.
  - Local uploads are exposed via `WebConfig.addResourceHandlers(...)`.

## Known Behavior To Preserve

- Private posts (`is_private = 1`) should only be visible to the author.
- Anonymous posts still expose `authorId`, but display name/avatar are masked in post VO.
- Chat history should return records in chronological order for UI rendering.
- Conversation list is sorted by latest message time.

## Common Places To Debug

### “Frontend page shows empty”

Check:

- SQL imported correctly
- `application.yml` datasource settings
- `PostServiceImpl` visibility filtering
- `MessageServiceImpl` conversation/chat queries

### “Image exists but not displayed”

Check:

- Stored URL in DB
- `FileUploadConfig.accessUrl`
- `WebConfig.addResourceHandlers`
- whether frontend received absolute URL or backend-relative URL

### “Login fails with imported SQL”

Check:

- password format must match current `PasswordEncoder`
- expected format:
  - `salt$base64(sha256(password + salt))`

## Fast Search Suggestions

- Search controllers:
  - `rg "@RequestMapping|@GetMapping|@PostMapping" backend/src/main/java/com/example/schoolwall/controller`
- Search auth-sensitive code:
  - `rg "userId|Authorization|Bearer|unauthorized" backend/src/main/java`
- Search post/message logic:
  - `rg "createPost|getPost|getConversations|getChatHistory|sendMessage" backend/src/main/java`
