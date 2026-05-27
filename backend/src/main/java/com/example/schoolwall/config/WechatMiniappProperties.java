package com.example.schoolwall.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 微信小程序配置（支持 yml、WECHAT_APP_ID、WECHAT_MINIAPP_APP_ID 等多种来源）
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniappProperties {

    private final Environment environment;

    private String appId;

    private String appSecret;

    public WechatMiniappProperties(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void mergeFromEnvironment() {
        if (!StringUtils.hasText(appId)) {
            appId = firstNonBlank(
                    environment.getProperty("WECHAT_APP_ID"),
                    environment.getProperty("WECHAT_MINIAPP_APP_ID"));
        }
        if (!StringUtils.hasText(appSecret)) {
            appSecret = firstNonBlank(
                    environment.getProperty("WECHAT_APP_SECRET"),
                    environment.getProperty("WECHAT_MINIAPP_APP_SECRET"));
        }

        if (StringUtils.hasText(appId)) {
            log.info("微信小程序 AppID 已加载: {}...", appId.substring(0, Math.min(8, appId.length())));
        } else {
            log.warn("微信小程序 AppID 未配置（检查 .env 或 application-local.yml）");
        }
        if (StringUtils.hasText(appSecret)) {
            log.info("微信小程序 AppSecret 已加载");
        } else {
            log.warn("微信小程序 AppSecret 未配置（检查 .env 或 application-local.yml）");
        }
    }

    private static String firstNonBlank(String... candidates) {
        for (String candidate : candidates) {
            if (StringUtils.hasText(candidate)) {
                return candidate.trim();
            }
        }
        return null;
    }
}
