package com.example.schoolwall.dto.wechat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 微信 jscode2session 接口响应
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WxCode2SessionResult {

    private Integer errcode;

    private String errmsg;

    private String openid;

    @JsonProperty("session_key")
    private String sessionKey;

    private String unionid;

    public boolean isSuccess() {
        return openid != null && !openid.isBlank() && (errcode == null || errcode == 0);
    }
}
