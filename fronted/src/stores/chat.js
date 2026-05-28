import { defineStore } from "pinia";
import { request, upload, getFileUrl } from "@/utils/http";

// 消息 tab 在 tabBar 中的 index（来自 pages.json 中的顺序：home / message / profile）
const MESSAGE_TAB_INDEX = 1;

function syncTabBarBadge(unreadTotal) {
  try {
    if (unreadTotal > 0) {
      uni.setTabBarBadge({
        index: MESSAGE_TAB_INDEX,
        text: unreadTotal > 99 ? "99+" : String(unreadTotal),
      });
    } else {
      uni.removeTabBarBadge({ index: MESSAGE_TAB_INDEX });
    }
  } catch (e) {
    // 非 tabBar 页面调用可能报错，忽略即可
  }
}

function normalizeConversation(raw = {}) {
  return {
    id: raw.id || raw.userId,
    userId: raw.userId || raw.id,
    name: raw.name || raw.nickname || "校园同学",
    avatar: getFileUrl(raw.avatar) || "👤",
    lastMsg: raw.lastMsg || raw.lastMessage || "",
    lastTime: raw.lastTime || raw.lastMessageTime || "",
    unread: Number(raw.unread || raw.unreadCount || 0),
  };
}

function normalizeMessage(raw = {}, currentUserId) {
  return {
    id: raw.id,
    fromId: raw.fromId,
    toId: raw.toId,
    content: raw.content || "",
    type: raw.type || "text",
    fileUrl: getFileUrl(raw.fileUrl),
    imageUrl: getFileUrl(raw.fileUrl),
    fromMe:
      typeof raw.fromMe === "boolean"
        ? raw.fromMe
        : Number(raw.fromId) === Number(currentUserId),
    time: raw.time || "",
    fullTime: raw.fullTime || "",
    status: "sent",
  };
}

export const useChatStore = defineStore("chat", {
  state: () => ({
    conversations: [],
    messagesByUserId: {},
  }),
  getters: {
    conversationList: (state) => state.conversations,
    getConversationById: (state) => (id) =>
      state.conversations.find((item) => Number(item.id) === Number(id)),
    getConversationByUserId: (state) => (userId) =>
      state.conversations.find((item) => Number(item.userId) === Number(userId)),
    getMessagesByUserId: (state) => (userId) => state.messagesByUserId[userId] || [],
  },
  actions: {
    upsertConversation(conversation) {
      const normalized = normalizeConversation(conversation);
      const index = this.conversations.findIndex(
        (item) => Number(item.userId) === Number(normalized.userId),
      );
      let result;
      if (index >= 0) {
        const next = {
          ...this.conversations[index],
          ...normalized,
        };
        this.conversations.splice(index, 1);
        this.conversations.unshift(next);
        result = next;
      } else {
        this.conversations.unshift(normalized);
        result = normalized;
      }
      this._refreshBadge();
      return result;
    },
    async fetchConversations() {
      const data = await request({
        url: "/api/messages/conversations",
      });
      this.conversations = data.map((item) => normalizeConversation(item));
      this._refreshBadge();
      return this.conversations;
    },
    _refreshBadge() {
      const total = this.conversations.reduce(
        (sum, conv) => sum + (Number(conv.unread) || 0),
        0,
      );
      syncTabBarBadge(total);
    },
    clearBadge() {
      syncTabBarBadge(0);
    },
    async searchContacts(keyword) {
      const trimmed = String(keyword || "").trim();
      if (!trimmed) return [];
      const data = await request({
        url: "/api/messages/search",
        data: { keyword: trimmed },
      });
      return (data || []).map((item) => normalizeConversation(item));
    },
    async fetchChatHistory(targetUserId, currentUserId) {
      const data = await request({
        url: `/api/messages/chat/${targetUserId}`,
        data: {
          page: 1,
          size: 50,
        },
      });
      this.messagesByUserId[targetUserId] = (data?.records || []).map((item) =>
        normalizeMessage(item, currentUserId),
      );
      return this.messagesByUserId[targetUserId];
    },
    async sendText(targetUserId, content, currentUserId) {
      const data = await request({
        url: "/api/messages/send",
        method: "POST",
        data: {
          toId: targetUserId,
          type: "text",
          content,
        },
        header: {
          "Content-Type": "application/json",
        },
      });
      const message = normalizeMessage(data, currentUserId);
      this.messagesByUserId[targetUserId] = [
        ...(this.messagesByUserId[targetUserId] || []),
        message,
      ];
      this.upsertConversation({
        id: targetUserId,
        userId: targetUserId,
        lastMsg: content,
        lastTime: message.time,
      });
      return message;
    },
    async uploadMessageImage(filePath) {
      const data = await upload({
        url: "/api/messages/image/upload",
        filePath,
      });
      return getFileUrl(data?.url);
    },
    async sendImage(targetUserId, fileUrl, currentUserId) {
      const data = await request({
        url: "/api/messages/send",
        method: "POST",
        data: {
          toId: targetUserId,
          type: "image",
          fileUrl,
        },
        header: {
          "Content-Type": "application/json",
        },
      });
      const message = normalizeMessage(data, currentUserId);
      this.messagesByUserId[targetUserId] = [
        ...(this.messagesByUserId[targetUserId] || []),
        message,
      ];
      this.upsertConversation({
        id: targetUserId,
        userId: targetUserId,
        lastMsg: "[图片]",
        lastTime: message.time,
      });
      return message;
    },
    async markRead(userId) {
      await request({
        url: `/api/messages/read/${userId}`,
        method: "PUT",
      });
      const conversation = this.getConversationByUserId(userId);
      if (conversation) {
        conversation.unread = 0;
      }
      this._refreshBadge();
    },
    clearMessages(userId) {
      this.messagesByUserId[userId] = [];
    },
  },
});
