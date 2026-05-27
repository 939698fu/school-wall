<script setup>
import { onLaunch, onShow, onHide } from "@dcloudio/uni-app";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();

onLaunch(() => {
  console.log("App Launch");
  userStore.bootstrapSession().catch((error) => {
    console.warn("Session bootstrap failed:", error?.message || error);
  });
});

onShow(() => {
  console.log("App Show");
});

onHide(() => {
  console.log("App Hide");
});
</script>

<style>
/* 导入全局样式 */
@import "@/styles/global.css";
</style>
