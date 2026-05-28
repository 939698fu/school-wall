import { defineStore } from "pinia";
import { request, setToken, upload, getToken, getFileUrl } from "@/utils/http";

const USER_KEY = "school_wall_user";

function readStoredUser() {
  return uni.getStorageSync(USER_KEY) || null;
}

function persistUser(user) {
  if (user) {
    uni.setStorageSync(USER_KEY, user);
    return;
  }
  uni.removeStorageSync(USER_KEY);
}

function normalizeUser(user) {
  if (!user) {
    return null;
  }
  return {
    ...user,
    avatar: getFileUrl(user.avatar) || "👤",
  };
}

function getWxLoginCode() {
  return new Promise((resolve, reject) => {
    uni.login({
      provider: "weixin",
      success: (res) => {
        if (res.code) {
          resolve(res.code);
          return;
        }
        reject(new Error("未获取到微信登录凭证"));
      },
      fail: (err) => {
        reject(new Error(err?.errMsg || "微信登录失败"));
      },
    });
  });
}

function getWxUserProfile() {
  return new Promise((resolve) => {
    uni.getUserProfile({
      desc: "用于展示昵称和头像",
      success: (res) => {
        resolve({
          nickname: res.userInfo?.nickName || "",
          avatar: res.userInfo?.avatarUrl || "",
        });
      },
      fail: () => resolve({ nickname: "", avatar: "" }),
    });
  });
}

export const useUserStore = defineStore("user", {
  state: () => ({
    token: getToken(),
    userInfo: normalizeUser(readStoredUser()),
    users: [],
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token && state.userInfo?.id),
    getUserById: (state) => (id) =>
      [state.userInfo, ...state.users]
        .filter(Boolean)
        .find((user) => Number(user?.id) === Number(id)),
    getUserByName: (state) => (name) =>
      [state.userInfo, ...state.users]
        .filter(Boolean)
        .find((user) => user?.nickname === name || user?.username === name),
  },
  actions: {
    setSession(payload) {
      this.token = payload?.token || "";
      this.userInfo = normalizeUser(payload?.user || null);
      setToken(this.token);
      persistUser(this.userInfo);
      if (this.userInfo) {
        this.upsertUsers([this.userInfo]);
      }
    },
    upsertUsers(users = []) {
      const map = new Map(
        this.users.map((user) => [Number(user.id), normalizeUser(user)]),
      );
      users.filter(Boolean).forEach((user) => {
        map.set(Number(user.id), normalizeUser(user));
      });
      if (this.userInfo?.id) {
        map.delete(Number(this.userInfo.id));
      }
      this.users = Array.from(map.values());
    },
    async loginWithUsername({ username, password }) {
      const data = await request({
        url: "/api/user/login",
        method: "POST",
        data: { username, password },
        header: { "Content-Type": "application/json" },
        useAuth: false,
      });
      this.setSession(data);
      return data;
    },
    async register(payload) {
      await request({
        url: "/api/user/register",
        method: "POST",
        data: payload,
        header: { "Content-Type": "application/json" },
        useAuth: false,
      });
      return this.loginWithUsername({
        username: payload.username,
        password: payload.password,
      });
    },
    async loginWithWechat() {
      // #ifdef MP-WEIXIN
      const code = await getWxLoginCode();
      const profile = await getWxUserProfile();
      const data = await request({
        url: "/api/user/wx-login",
        method: "POST",
        data: {
          code,
          nickname: profile.nickname,
          avatar: profile.avatar,
        },
        header: {
          "Content-Type": "application/json",
        },
        useAuth: false,
      });
      this.setSession(data);
      return data;
      // #endif

      // #ifndef MP-WEIXIN
      throw new Error("微信登录仅支持在微信小程序中使用");
      // #endif
    },
    async bootstrapSession() {
      if (!this.token) {
        return null;
      }
      try {
        await this.fetchCurrentUser();
        return this.userInfo;
      } catch (error) {
        this.logout();
        return null;
      }
    },
    async fetchCurrentUser() {
      if (!this.token) {
        return null;
      }
      const user = await request({
        url: "/api/user/info",
      });
      this.userInfo = normalizeUser(user);
      persistUser(this.userInfo);
      this.upsertUsers([this.userInfo]);
      return this.userInfo;
    },
    async fetchUserById(userId) {
      const user = await request({
        url: `/api/user/${userId}`,
      });
      const normalized = normalizeUser(user);
      this.upsertUsers([normalized]);
      return normalized;
    },
    async followUser(userId) {
      await request({
        url: `/api/user/${userId}/follow`,
        method: "POST",
      });
      this._patchUserFollow(userId, true);
    },
    async unfollowUser(userId) {
      await request({
        url: `/api/user/${userId}/follow`,
        method: "DELETE",
      });
      this._patchUserFollow(userId, false);
    },
    async fetchFollowers(userId) {
      const list = await request({
        url: `/api/user/${userId}/followers`,
      });
      return (list || []).map(normalizeUser);
    },
    async fetchFollowing(userId) {
      const list = await request({
        url: `/api/user/${userId}/following`,
      });
      return (list || []).map(normalizeUser);
    },
    _patchUserFollow(userId, isFollowed) {
      this.users = this.users.map((u) =>
        Number(u.id) === Number(userId) ? { ...u, isFollowed } : u,
      );
    },
    async updateProfile(data) {
      const user = await request({
        url: "/api/user/info",
        method: "PUT",
        data,
        header: {
          "Content-Type": "application/json",
        },
      });
      this.userInfo = normalizeUser(user);
      persistUser(this.userInfo);
      this.upsertUsers([this.userInfo]);
      return this.userInfo;
    },
    async uploadAvatar(filePath) {
      const data = await upload({
        url: "/api/user/avatar",
        filePath,
      });
      const avatar = getFileUrl(data?.url);
      return this.updateProfile({ avatar });
    },
    logout() {
      this.token = "";
      this.userInfo = null;
      setToken("");
      persistUser(null);
      try {
        uni.removeTabBarBadge({ index: 1 });
      } catch (e) {}
    },
  },
});
