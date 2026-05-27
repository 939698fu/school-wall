import { defineStore } from "pinia";
import { request, upload, getFileUrl } from "@/utils/http";

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
      if (index >= 0) {
        const next = {
          ...this.conversations[index],
          ...normalized,
        };
        this.conversations.splice(index, 1);
        this.conversations.unshift(next);
        return next;
      } else {
        this.conversations.unshift(normalized);
      }
      return normalized;
    },
    async fetchConversations() {
      const data = await request({
        url: "/api/messages/conversations",
      });
      this.conversations = data.map((item) => normalizeConversation(item));
      return this.conversations;
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
    },
    clearMessages(userId) {
      this.messagesByUserId[userId] = [];
    },
  },
});
