package com.example.schoolwall.service;

import com.example.schoolwall.config.WechatMiniappProperties;
import com.example.schoolwall.common.BusinessException;
import com.example.schoolwall.dto.wechat.WxCode2SessionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 微信小程序服务端能力
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatMiniAppService {

    private static final String CODE2SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session";

    private final RestTemplate restTemplate;
    private final WechatMiniappProperties wechatMiniappProperties;

    /**
     * 使用 code 换取 openid
     */
    public WxCode2SessionResult code2Session(String code) {
        String appId = wechatMiniappProperties.getAppId();
        String appSecret = wechatMiniappProperties.getAppSecret();
        if (!StringUtils.hasText(appId)) {
            throw BusinessException.badRequest("微信小程序 AppID 未配置，请设置 WECHAT_APP_ID");
        }
        if (!StringUtils.hasText(appSecret)) {
            throw BusinessException.badRequest(
                    "微信小程序 AppSecret 未配置，请在 application-local.yml 或环境变量 WECHAT_APP_SECRET 中填写");
        }

        String url = UriComponentsBuilder.fromHttpUrl(CODE2SESSION_URL)
                .queryParam("appid", appId)
                .queryParam("secret", appSecret)
                .queryParam("js_code", code)
                .queryParam("grant_type", "authorization_code")
                .toUriString();

        WxCode2SessionResult result = restTemplate.getForObject(url, WxCode2SessionResult.class);
        if (result == null) {
            throw BusinessException.badRequest("微信登录失败，请稍后重试");
        }
        if (!result.isSuccess()) {
            log.warn("微信 code2session 失败: errcode={}, errmsg={}", result.getErrcode(), result.getErrmsg());
            throw BusinessException.badRequest("微信登录失败：" + (result.getErrmsg() != null ? result.getErrmsg() : "invalid code"));
        }
        return result;
    }
}
