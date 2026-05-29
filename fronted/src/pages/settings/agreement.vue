<template>
  <view class="container">
    <scroll-view scroll-y class="content-scroll">
      <view class="content">
        <text class="h1">用户协议</text>
        <text class="time">发布日期：2024年05月29日</text>
        
        <text class="p">欢迎使用校园墙！在您开始使用我们的服务之前，请务必仔细阅读并理解本《用户协议》。</text>
        
        <text class="h2">1. 服务说明</text>
        <text class="p">校园墙是一个为校园用户提供信息分享、交流的平台。用户可以在此发布动态、评论、点赞等。</text>
        
        <text class="h2">2. 用户行为规范</text>
        <text class="p">用户在使用本服务时，必须遵守国家法律法规，不得发布包含色情、暴力、恐怖、诈骗等违法违规内容。</text>
        <text class="p">用户应对自己发布的内容承担全部法律责任。</text>
        
        <text class="h2">3. 隐私保护</text>
        <text class="p">我们非常重视您的隐私，将按照《隐私政策》保护您的个人信息。</text>
        
        <text class="h2">4. 免责声明</text>
        <text class="p">由于网络环境的复杂性，我们不保证服务不会中断，对因不可抗力导致的损失不承担责任。</text>
        
        <view class="footer-gap"></view>
      </view>
    </scroll-view>
  </view>
</template>

<style scoped>
.container {
  height: 100vh;
  background-color: #fff;
}
.content-scroll {
  height: 100%;
}
.content {
  padding: 40rpx;
}
.h1 {
  font-size: 40rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 20rpx;
  text-align: center;
  color: var(--primary, #ff5a35);
}
.time {
  font-size: 24rpx;
  color: #999;
  display: block;
  margin-bottom: 40rpx;
  text-align: center;
}
.h2 {
  font-size: 32rpx;
  font-weight: bold;
  display: block;
  margin-top: 40rpx;
  margin-bottom: 20rpx;
  color: var(--primary-mid, #ff7a5a);
  border-left: 8rpx solid var(--primary, #ff5a35);
  padding-left: 20rpx;
}
.p {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  display: block;
  margin-bottom: 20rpx;
}
.footer-gap {
  height: 60rpx;
}
</style>
