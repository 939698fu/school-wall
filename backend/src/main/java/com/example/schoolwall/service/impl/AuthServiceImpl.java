package com.example.schoolwall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.schoolwall.dto.request.LoginRequest;
import com.example.schoolwall.dto.request.RegisterRequest;
import com.example.schoolwall.dto.request.WxLoginRequest;
import com.example.schoolwall.dto.response.LoginResponse;
import com.example.schoolwall.dto.response.UserVO;
import com.example.schoolwall.dto.wechat.WxCode2SessionResult;
import com.example.schoolwall.entity.User;
import com.example.schoolwall.common.BusinessException;
import com.example.schoolwall.mapper.UserMapper;
import com.example.schoolwall.security.JwtTokenUtil;
import com.example.schoolwall.security.PasswordEncoder;
import com.example.schoolwall.service.AuthService;
import com.example.schoolwall.service.WechatMiniAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final WechatMiniAppService wechatMiniAppService;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User existingUser = userMapper.selectOne(wrapper);
        if (existingUser != null) {
            throw BusinessException.badRequest("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        
        // 生成盐值并加密密码
        String salt = passwordEncoder.generateSalt();
        String encodedPassword = passwordEncoder.encode(request.getPassword(), salt);
        // 将盐值混入密码存储（格式：salt$encodedPassword）
        user.setPassword(salt + "$" + encodedPassword);
        
        user.setNickname(request.getNickname());
        user.setAvatar("🍊"); // 默认头像
        user.setSchool(request.getSchool());
        user.setBio(request.getBio());
        user.setPostCount(0);
        user.setLikeCount(0);
        user.setCollectCount(0);

        userMapper.insert(user);
        log.info("用户注册成功: {}", request.getUsername());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }

        // 验证密码
        String storedPassword = user.getPassword();
        if (storedPassword == null || !storedPassword.contains("$")) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }
        
        String[] parts = storedPassword.split("\\$");
        String salt = parts[0];
        String encodedPassword = parts[1];
        
        if (!passwordEncoder.matches(request.getPassword(), encodedPassword, salt)) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }

        return buildLoginResponse(user);
    }

    @Override
    @Transactional
    public LoginResponse wxLogin(WxLoginRequest request) {
        WxCode2SessionResult session = wechatMiniAppService.code2Session(request.getCode());
        String openId = session.getOpenid();

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenId, openId);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            user = createWechatUser(openId, request.getNickname(), request.getAvatar());
            log.info("微信新用户注册: openId={}, userId={}", openId, user.getId());
        } else {
            updateWechatProfile(user, request.getNickname(), request.getAvatar());
            userMapper.updateById(user);
            log.info("微信用户登录: openId={}, userId={}", openId, user.getId());
        }

        return buildLoginResponse(user);
    }

    private User createWechatUser(String openId, String nickname, String avatar) {
        User user = new User();
        user.setOpenId(openId);
        user.setUsername(buildWxUsername(openId));
        user.setPassword(buildRandomPassword());
        user.setNickname(resolveNickname(openId, nickname));
        user.setAvatar(resolveAvatar(avatar));
        user.setSchool("");
        user.setBio("");
        user.setPostCount(0);
        user.setLikeCount(0);
        user.setCollectCount(0);
        user.setFollowerCount(0);
        user.setFollowingCount(0);
        userMapper.insert(user);
        return user;
    }

    private void updateWechatProfile(User user, String nickname, String avatar) {
        if (StringUtils.hasText(nickname)) {
            user.setNickname(nickname.trim());
        }
        if (StringUtils.hasText(avatar)) {
            user.setAvatar(avatar.trim());
        }
    }

    private String buildWxUsername(String openId) {
        String suffix = openId.length() > 20 ? openId.substring(openId.length() - 20) : openId;
        return "wx_" + suffix;
    }

    private String resolveNickname(String openId, String nickname) {
        if (StringUtils.hasText(nickname)) {
            return nickname.trim();
        }
        String suffix = openId.length() > 6 ? openId.substring(openId.length() - 6) : openId;
        return "微信用户" + suffix;
    }

    private String resolveAvatar(String avatar) {
        if (StringUtils.hasText(avatar)) {
            return avatar.trim();
        }
        return "🙂";
    }

    private String buildRandomPassword() {
        String salt = passwordEncoder.generateSalt();
        String encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString(), salt);
        return salt + "$" + encodedPassword;
    }

    private LoginResponse buildLoginResponse(User user) {
        String token = jwtTokenUtil.generateToken(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar()
        );

        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .school(user.getSchool())
                .bio(user.getBio())
                .postCount(user.getPostCount())
                .likeCount(user.getLikeCount())
                .collectCount(user.getCollectCount())
                .followerCount(user.getFollowerCount())
                .followingCount(user.getFollowingCount())
                .createTime(user.getCreateTime())
                .build();

        return LoginResponse.builder()
                .token(token)
                .user(userVO)
                .build();
    }
}
