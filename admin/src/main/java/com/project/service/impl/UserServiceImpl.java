package com.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.convention.errorcode.BaseErrorCode;
import com.project.common.convention.exception.ClientException;
import com.project.common.enums.UserErrorCode;
import com.project.dao.entity.UserDO;
import com.project.dao.mapper.UserMapper;
import com.project.dto.req.UserRegisterReqDTO;
import com.project.dto.resp.UserRespDTO;
import com.project.service.UserService;
import lombok.AllArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static com.project.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;

/**
 * 用户接口实现层
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> eq = Wrappers
                .lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(eq);
        if (userDO == null) {
            throw new ClientException(UserErrorCode.USER_NUll);
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
}
