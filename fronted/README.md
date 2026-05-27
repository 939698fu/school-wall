# 🏫 校园墙 Campus Wall

基于 UniApp + Vue3 + Pinia 的校园匿名社区小程序前端原型。

## 技术栈

- **框架**：UniApp (Vue 3 Composition API)
- **构建**：Vite
- **状态管理**：Pinia
- **UI 风格**：自定义，仿国内主流校园墙设计
- **数据**：Mock 数据（无需后端）

## 项目结构

```
src/
├── pages/
│   ├── login/index.vue       # 微信登录页
│   ├── home/index.vue        # 首页帖子流
│   ├── post-detail/index.vue # 帖子详情 + 评论
│   ├── message/
│   │   ├── index.vue         # 私信列表
│   │   └── chat.vue          # 聊天会话页
│   ├── publish/index.vue     # 发帖页
│   └── profile/index.vue     # 个人主页
├── components/
│   └── PostCard.vue          # 帖子卡片组件
├── stores/
│   ├── user.js               # 用户状态
│   └── posts.js              # 帖子状态
├── mock/
│   └── data.js               # Mock 数据
├── styles/
│   └── global.css            # 全局变量与样式
├── App.vue
├── main.js
├── pages.json                # UniApp 路由配置
└── manifest.json             # 应用配置
```

## 初始化步骤

### 方式一：HBuilderX（推荐，微信小程序直接预览）

1. 下载安装 [HBuilderX](https://www.dcloud.io/hbuilderx.html)
2. 打开 HBuilderX → 文件 → 导入 → 从本地目录导入，选择本项目根目录
3. 运行 → 运行到小程序模拟器 → 微信开发者工具
4. 微信开发者工具会自动打开并加载

### 方式二：命令行（H5 预览，最快）

```bash
# 1. 克隆 UniApp 官方脚手架模板（保留配置结构）
npx degit dcloudio/uni-preset-vue#vite my-campus-wall
cd my-campus-wall

# 2. 将本项目 src/ 内所有文件复制覆盖进去
cp -r /path/to/campus-wall/src/* ./src/

# 3. 安装依赖
npm install
npm install pinia

# 4. H5 预览
npm run dev:h5
# 浏览器打开 http://localhost:5173
```

### 方式三：直接用 CLI 跑微信小程序

```bash
npm install -g @vue/cli
npm install -g @dcloudio/vue-cli-plugin-uni

# 安装依赖
npm install

# 编译微信小程序
npm run dev:mp-weixin

# 用微信开发者工具导入 dist/dev/mp-weixin 目录
```

## Tab 图标说明

项目使用 `pages.json` 中的 tabBar 配置，需要在 `src/static/tab/` 目录下放置以下图标：

- `home.png` / `home-active.png`
- `message.png` / `message-active.png`
- `publish.png` / `publish-active.png`
- `profile.png` / `profile-active.png`

图标尺寸建议：81x81px，PNG 格式。可以从 [iconfont](https://www.iconfont.cn) 免费下载。

> 💡 H5 预览模式下 tabBar 图标缺失不影响功能演示，仅小程序编译时需要实际图片文件。

## 页面导航流程

```
登录页 ──登录成功──▶ 首页
首页 ──点击帖子──▶ 帖子详情
帖子详情 ──评论/点赞──▶（当前页交互）
底部Tab ──切换──▶ 私信 / 发帖 / 我的
私信列表 ──点击会话──▶ 聊天页
我的 ──编辑资料──▶（弹窗内交互）
```

## 功能说明（当前版本）

| 功能                          | 状态      |
| ----------------------------- | --------- |
| 微信登录（Mock）              | ✅        |
| 帖子流（最新/热门/树洞/表白） | ✅        |
| 帖子详情 + 评论               | ✅        |
| 点赞 / 收藏                   | ✅        |
| 私信列表                      | ✅        |
| 一对一聊天                    | ✅        |
| 发帖（含图片上传）            | ✅        |
| 个人主页 + 编辑资料           | ✅        |
| 真实后端接口                  | ⬜ 待开发 |
| 图片云存储                    | ⬜ 待开发 |
| 推送通知                      | ⬜ 待开发 |

## 颜色设计规范

| 变量              | 值        | 用途           |
| ----------------- | --------- | -------------- |
| `--primary`       | `#FF5A35` | 主色调（橙红） |
| `--primary-light` | `#FFF0EC` | 主色浅色背景   |
| `--text-main`     | `#1a1a1a` | 主要文字       |
| `--text-sub`      | `#666666` | 次要文字       |
| `--text-hint`     | `#aaaaaa` | 提示文字       |
| `--bg`            | `#F5F5F0` | 页面背景       |
| `--border`        | `#EBEBEB` | 边框/分割线    |
