<script setup>
import { onLaunch, onShow, onHide } from "@dcloudio/uni-app";
import { useUserStore } from "@/stores/user";
import { useChatStore } from "@/stores/chat";

const userStore = useUserStore();
const chatStore = useChatStore();

function refreshUnreadBadge() {
  if (!userStore.isLoggedIn) return;
  chatStore.fetchConversations().catch(() => {});
}

onLaunch(() => {
  userStore
    .bootstrapSession()
    .then(refreshUnreadBadge)
    .catch((error) => {
      console.warn("Session bootstrap failed:", error?.message || error);
    });
});

onShow(() => {
  refreshUnreadBadge();
});

onHide(() => {});
</script>

<style>
/* 导入全局样式 */
@import "@/styles/global.css";
</style>
