import { defineStore } from "pinia";
import { request, upload, getFileUrl } from "@/utils/http";

const EMPTY_TAB_STATE = () => ({
  records: [],
  cursor: null,
  size: 10,
  hasMore: true,
});

function normalizeComment(comment = {}) {
  return {
    ...comment,
    avatar: getFileUrl(comment.authorAvatar || comment.avatar) || "👤",
  };
}

function normalizePost(post = {}) {
  return {
    ...post,
    images: Array.isArray(post.images)
      ? post.images.map((item) => getFileUrl(item))
      : [],
    authorAvatar: getFileUrl(post.authorAvatar) || "👤",
    comments: Array.isArray(post.comments)
      ? post.comments.map((item) => normalizeComment(item))
      : [],
  };
}

function mergePosts(posts = [], sortByTime = true) {
  const map = new Map();
  posts.forEach((post) => {
    if (post?.id == null) {
      return;
    }
    map.set(Number(post.id), normalizePost(post));
  });
  const result = Array.from(map.values());
  if (sortByTime) {
    return result.sort(
      (a, b) => new Date(b.createTime || b.fullTime || 0) - new Date(a.createTime || a.fullTime || 0),
    );
  }
  return result;
}

export const usePostsStore = defineStore("posts", {
  state: () => ({
    activeTab: "latest",
    tabs: {
      latest: EMPTY_TAB_STATE(),
      hot: EMPTY_TAB_STATE(),
      hole: EMPTY_TAB_STATE(),
      love: EMPTY_TAB_STATE(),
    },
    detailMap: {},
    myPosts: [],
    myCollections: [],
    userPostsMap: {},
    hotTags: [],
    loading: false,
  }),
  getters: {
    postList: (state) => state.tabs[state.activeTab]?.records || [],
    allPosts: (state) =>
      mergePosts(
        Object.values(state.tabs).flatMap((tab) => tab.records).concat(Object.values(state.detailMap)),
      ),
    getPostsByTab: (state) => (tab) => state.tabs[tab]?.records || [],
    getPostById: (state) => (id) => {
      const detail = state.detailMap[id];
      if (detail) {
        return detail;
      }
      return Object.values(state.tabs)
        .flatMap((tab) => tab.records)
        .find((post) => Number(post.id) === Number(id));
    },
    searchPosts: (state) => (keyword) => {
      const normalizedKeyword = String(keyword || "").trim().toLowerCase();
      const posts = mergePosts(
        Object.values(state.tabs).flatMap((tab) => tab.records).concat(Object.values(state.detailMap)),
      );
      if (!normalizedKeyword) {
        return posts;
      }
      return posts.filter((post) => {
        const haystacks = [
          post.title,
          post.content,
          post.tag,
          post.author,
          ...(post.comments || []).map((comment) => comment.content),
        ];
        return haystacks.some((item) =>
          String(item || "").toLowerCase().includes(normalizedKeyword),
        );
      });
    },
  },
  actions: {
    setActiveTab(tab) {
      this.activeTab = tab;
    },
    syncPost(post) {
      const normalized = normalizePost(post);
      Object.keys(this.tabs).forEach((tabKey) => {
        this.tabs[tabKey].records = this.tabs[tabKey].records.map((item) =>
          Number(item.id) === Number(normalized.id) ? { ...item, ...normalized } : item,
        );
      });
      this.myPosts = this.myPosts.map((item) =>
        Number(item.id) === Number(normalized.id) ? { ...item, ...normalized } : item,
      );
      this.myCollections = this.myCollections
        .map((item) =>
          Number(item.id) === Number(normalized.id) ? { ...item, ...normalized } : item,
        )
        .filter((item) => item.collected !== false);
      Object.keys(this.userPostsMap).forEach((userId) => {
        this.userPostsMap[userId] = this.userPostsMap[userId].map((item) =>
          Number(item.id) === Number(normalized.id) ? { ...item, ...normalized } : item,
        );
      });
      this.detailMap[normalized.id] = normalized;
      return normalized;
    },
    insertLatestPost(post) {
      const normalized = normalizePost(post);
      this.tabs.latest.records = mergePosts([normalized, ...this.tabs.latest.records]);
      this.detailMap[normalized.id] = normalized;
      this.myPosts = mergePosts([normalized, ...this.myPosts]);
    },
    async fetchPostsByTab(tab = this.activeTab, { refresh = false } = {}) {
      const tabState = this.tabs[tab];
      if (!tabState) {
        return [];
      }
      if (this.loading || (!refresh && !tabState.hasMore)) {
        return tabState.records;
      }

      const cursor = refresh ? null : tabState.cursor;
      const requestData = {
        size: tabState.size,
        type: tab,
      };
      if (cursor != null) {
        requestData.cursor = cursor;
      }
      this.loading = true;
      try {
        const result = await request({
          url: "/api/posts/cursor",
          data: requestData,
        });
        const records = (result?.records || []).map((item) => normalizePost(item));
        const sortByTime = tab !== "hot";
        tabState.records = refresh ? records : mergePosts([...tabState.records, ...records], sortByTime);
        tabState.cursor = result?.nextCursor;
        tabState.hasMore = !!result?.hasMore;
        
        records.forEach((post) => {
          this.detailMap[post.id] = { ...(this.detailMap[post.id] || {}), ...post };
        });
        return tabState.records;
      } finally {
        this.loading = false;
      }
    },
    async fetchPostDetail(postId) {
      const result = await request({
        url: `/api/posts/${postId}`,
      });
      const normalized = normalizePost(result);
      this.detailMap[postId] = normalized;
      return normalized;
    },
    async uploadPostImages(filePaths = []) {
      const urls = [];
      for (const filePath of filePaths) {
        const data = await upload({
          url: "/api/posts/image/upload",
          filePath,
        });
        urls.push(getFileUrl(data?.url));
      }
      return urls;
    },
    async createPost(payload) {
      const created = await request({
        url: "/api/posts",
        method: "POST",
        data: payload,
        header: {
          "Content-Type": "application/json",
        },
      });
      const normalized = normalizePost(created);
      this.insertLatestPost(normalized);
      return normalized;
    },
    async toggleLike(postId) {
      const result = await request({
        url: `/api/posts/${postId}/like`,
        method: "POST",
      });
      const post = this.getPostById(postId);
      if (!post) {
        return result;
      }
      this.syncPost({
        ...post,
        liked: result.liked,
        likes: result.likes,
      });
      return result;
    },
    async toggleCollect(postId) {
      const result = await request({
        url: `/api/posts/${postId}/collect`,
        method: "POST",
      });
      const post = this.getPostById(postId);
      if (!post) {
        return result;
      }
      this.syncPost({
        ...post,
        collected: result.collected,
      });
      if (result.collected) {
        this.myCollections = mergePosts([{ ...post, collected: true }, ...this.myCollections]);
      }
      return result;
    },
    async addComment(postId, comment) {
      await request({
        url: "/api/comments",
        method: "POST",
        data: {
          postId,
          content: comment.content,
        },
        header: {
          "Content-Type": "application/json",
        },
      });
      return this.fetchPostDetail(postId);
    },
    async fetchMyPosts() {
      const result = await request({
        url: "/api/posts/mine",
        data: {
          page: 1,
          size: 50,
        },
      });
      this.myPosts = (result?.records || []).map((item) => normalizePost(item));
      this.myPosts.forEach((post) => {
        this.detailMap[post.id] = { ...(this.detailMap[post.id] || {}), ...post };
      });
      return this.myPosts;
    },
    async fetchMyCollections() {
      const result = await request({
        url: "/api/posts/collections",
        data: {
          page: 1,
          size: 50,
        },
      });
      this.myCollections = (result?.records || []).map((item) => normalizePost(item));
      this.myCollections.forEach((post) => {
        this.detailMap[post.id] = { ...(this.detailMap[post.id] || {}), ...post };
      });
      return this.myCollections;
    },
    async fetchUserPosts(userId) {
      const result = await request({
        url: `/api/posts/user/${userId}`,
        data: {
          page: 1,
          size: 50,
        },
      });
      const records = (result?.records || []).map((item) => normalizePost(item));
      this.userPostsMap[userId] = records;
      records.forEach((post) => {
        this.detailMap[post.id] = { ...(this.detailMap[post.id] || {}), ...post };
      });
      return records;
    },
    async deletePost(postId) {
      await request({
        url: `/api/posts/${postId}`,
        method: "DELETE",
      });
      const id = Number(postId);
      Object.keys(this.tabs).forEach((tabKey) => {
        this.tabs[tabKey].records = this.tabs[tabKey].records.filter(
          (item) => Number(item.id) !== id,
        );
        if (this.tabs[tabKey].total > 0) {
          this.tabs[tabKey].total = Math.max(0, this.tabs[tabKey].total - 1);
        }
      });
      this.myPosts = this.myPosts.filter((item) => Number(item.id) !== id);
      this.myCollections = this.myCollections.filter((item) => Number(item.id) !== id);
      Object.keys(this.userPostsMap).forEach((userId) => {
        this.userPostsMap[userId] = this.userPostsMap[userId].filter(
          (item) => Number(item.id) !== id,
        );
      });
      delete this.detailMap[id];
    },
    async deleteComment(commentId, postId) {
      await request({
        url: `/api/comments/${commentId}`,
        method: "DELETE",
      });
      if (postId) {
        return this.fetchPostDetail(postId);
      }
      return null;
    },
    async fetchMentionedUsers(keyword) {
      const trimmed = String(keyword || "").trim();
      const data = await request({
        url: "/api/posts/mentioned",
        data: { keyword: trimmed },
      });
      return (data || []).map((item) => ({
        ...item,
        avatar: getFileUrl(item.avatar) || "👤",
      }));
    },
    async fetchHotTags(limit = 10) {
      const data = await request({
        url: "/api/posts/tags",
        data: { limit },
        useAuth: false,
      });
      this.hotTags = Array.isArray(data) ? data : [];
      return this.hotTags;
    },
    async searchFromServer(keyword, { page = 1, size = 20 } = {}) {
      const trimmed = String(keyword || "").trim();
      if (!trimmed) {
        return { posts: [], users: [], postTotal: 0, userTotal: 0 };
      }
      const result = await request({
        url: "/api/search",
        data: { keyword: trimmed, page, size },
      });
      const posts = (result?.posts || []).map((item) => normalizePost(item));
      const users = (result?.users || []).map((item) => ({
        ...item,
        avatar: getFileUrl(item.avatar) || "👤",
      }));
      return {
        posts,
        users,
        postTotal: Number(result?.postTotal || posts.length),
        userTotal: Number(result?.userTotal || users.length),
      };
    },
    async likeComment(comment) {
      const result = await request({
        url: `/api/comments/${comment.id}/like`,
        method: "POST",
      });
      const post = this.getPostById(comment.postId);
      if (!post?.comments?.length) {
        return result;
      }
      const nextComments = post.comments.map((item) =>
        Number(item.id) === Number(comment.id)
          ? { ...item, liked: result.liked, likes: result.likes }
          : item,
      );
      this.syncPost({
        ...post,
        comments: nextComments,
      });
      return result;
    },
  },
});
