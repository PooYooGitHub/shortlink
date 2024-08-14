package com.project.common.biz.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.project.dao.entity.UserDO;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;

import static com.project.common.constant.RedisCacheConstant.USER_LOGIN_KEY;

/**
 * 用户信息传输过滤器
 */
@AllArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String username = httpServletRequest.getHeader("username");
        String token = httpServletRequest.getHeader("token");
        // Log the headers
        System.out.println("Received headers - username: " + username + ", token: " + token);
        String jsonString = (String) stringRedisTemplate.opsForHash().get(USER_LOGIN_KEY+ username,token);
        if (jsonString != null) {
            JSONObject jsonObject = JSONUtil.parseObj(jsonString);
            UserDO userDO = JSONUtil.toBean( jsonObject, UserDO.class);
            UserInfoDTO userInfoDTO = BeanUtil.toBean(userDO,UserInfoDTO.class);
            UserContext.setUser(userInfoDTO);
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
