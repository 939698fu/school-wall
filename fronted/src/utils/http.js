const DEFAULT_BASE_URL = "http://localhost:8080";
const TOKEN_KEY = "school_wall_token";

function getBaseUrl() {
  return uni.getStorageSync("school_wall_base_url") || DEFAULT_BASE_URL;
}

export function getToken() {
  return uni.getStorageSync(TOKEN_KEY) || "";
}

export function setToken(token) {
  if (token) {
    uni.setStorageSync(TOKEN_KEY, token);
    return;
  }
  uni.removeStorageSync(TOKEN_KEY);
}

function buildHeaders(extraHeaders = {}, useAuth = true) {
  const headers = { ...extraHeaders };
  const token = getToken();
  if (useAuth && token) {
    headers.Authorization = `Bearer ${token}`;
  }
  return headers;
}

export function request({ url, method = "GET", data, header, useAuth = true }) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: `${getBaseUrl()}${url}`,
      method,
      data,
      header: buildHeaders(header, useAuth),
      success: ({ data: response }) => {
        if (response?.code === 200) {
          resolve(response.data);
          return;
        }
        const message = response?.message || "请求失败";
        reject(new Error(message));
      },
      fail: (error) => {
        reject(new Error(error?.errMsg || "网络请求失败"));
      },
    });
  });
}

export function upload({ url, filePath, name = "file", formData, useAuth = true }) {
  return new Promise((resolve, reject) => {
    uni.uploadFile({
      url: `${getBaseUrl()}${url}`,
      filePath,
      name,
      formData,
      header: buildHeaders({}, useAuth),
      success: ({ data }) => {
        try {
          const response = JSON.parse(data);
          if (response?.code === 200) {
            resolve(response.data);
            return;
          }
          reject(new Error(response?.message || "上传失败"));
        } catch (error) {
          reject(new Error("上传响应解析失败"));
        }
      },
      fail: (error) => {
        reject(new Error(error?.errMsg || "上传失败"));
      },
    });
  });
}

export function getFileUrl(path) {
  if (!path) {
    return "";
  }
  if (/^https?:\/\//.test(path)) {
    return path;
  }
  if (String(path).startsWith("/")) {
    return `${getBaseUrl()}${path}`;
  }
  return path;
}
