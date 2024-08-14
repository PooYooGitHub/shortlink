package com.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.convention.errorcode.BaseErrorCode;
import com.project.common.convention.exception.ClientException;
import com.project.common.enums.UserErrorCode;
import com.project.dao.entity.UserDO;
import com.project.dao.mapper.UserMapper;
import com.project.dto.req.UserDeleteRequestDTO;
import com.project.dto.req.UserLoginReqDTO;
import com.project.dto.req.UserRegisterReqDTO;
import com.project.dto.req.UserUpdateReqDTO;
import com.project.dto.resp.UserLoginRespDTO;
import com.project.dto.resp.UserRespDTO;
import com.project.service.UserService;
import lombok.AllArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.project.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.project.common.constant.RedisCacheConstant.USER_LOGIN_KEY;
import static com.project.common.enums.UserErrorCode.USER_NUll;

/**
 * 用户接口实现层
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> eq = Wrappers
                .lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(eq);
        if (userDO == null) {
            throw new ClientException(USER_NUll);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    @Override
    public Boolean hasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        String username = requestParam.getUsername();
        if (hasUsername(username)) {
            throw new ClientException(BaseErrorCode.USER_NAME_EXIST_ERROR);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + username);
        try {
            if (lock.tryLock()){
                UserDO userDO = BeanUtil.toBean(requestParam, UserDO.class);
                int insert = baseMapper.insert(userDO);
                if (insert < 1) {
                    throw new ClientException(UserErrorCode.USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                return ;
            }else
                throw new ClientException(BaseErrorCode.USER_NAME_EXIST_ERROR);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        //TODO: 验证当前用户可不可以修该用户
        LambdaUpdateWrapper<UserDO> eq = Wrappers.lambdaUpdate(UserDO.class).
                eq(UserDO::getUsername, requestParam.getUsername());
        baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class), eq);
        return ;
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        //用户存在并且没有被注销
        LambdaQueryWrapper<UserDO> eq = Wrappers.lambdaQuery(UserDO.class).
                eq(UserDO::getUsername, requestParam.getUsername()).
                eq(UserDO::getPassword, requestParam.getPassword()).
                eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(eq);
        if (userDO == null) {
            throw new ClientException("用户名或密码错误");
        }

        String username = userDO.getUsername();
        if (stringRedisTemplate.hasKey(USER_LOGIN_KEY+username)){
            throw new ClientException("用户已经登录,请先退出登录");
        }
        String uuid = UUID.randomUUID().toString();

        stringRedisTemplate.opsForHash().put(USER_LOGIN_KEY+ username,uuid, JSONUtil.toJsonStr(userDO));
        stringRedisTemplate.expire(USER_LOGIN_KEY+ username,30L, TimeUnit.DAYS);

        return new UserLoginRespDTO(uuid);


    }

    @Override
    public Boolean checkLogin(String token,String username) {
        Boolean aBoolean = stringRedisTemplate.opsForHash().hasKey(USER_LOGIN_KEY+ username,token);
        return BooleanUtil.isTrue(aBoolean);
    }

    @Override
    public void logout(String token, String username) {
        Long delete = stringRedisTemplate.opsForHash().delete(USER_LOGIN_KEY + username, token);
        if (delete < 1) {
            throw new ClientException("退出登录失败");
        }
        return ;
    }

    @Override
    public void deleteAccount(UserDeleteRequestDTO requestParam) {
        LambdaQueryWrapper<UserDO> eq = Wrappers.lambdaQuery(UserDO.class).
                eq(UserDO::getUsername, requestParam.getUsername()).
                eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(eq);
        if (userDO == null) {
            throw new ClientException("用户不存在");
        }else {
            userDO.setDelFlag(1);
            userDO.setDelTime(DateTime.now());
            baseMapper.update(userDO, Wrappers.lambdaUpdate(UserDO.class).eq(UserDO::getUsername, requestParam.getUsername()));
            //布隆过滤器删除不了元素
        }
    }
}
