package com.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.biz.user.UserContext;
import com.project.common.convention.exception.ClientException;
import com.project.dao.entity.GroupDO;
import com.project.dao.mapper.GroupMapper;
import com.project.dto.req.GroupAddReqDTO;
import com.project.dto.req.GroupSortReqDTO;
import com.project.dto.req.GroupUpdateReqDTO;
import com.project.dto.resp.GroupingRespDTO;
import com.project.service.GroupService;
import com.project.util.RandomStringGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

/*
    * 短链接分租接口实现层
 */
//todo ：这个添加有问题，
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService{

    @Override
    public void addGroup(GroupAddReqDTO requestParam) {
        String gid;
        do {
            gid = RandomStringGenerator.generateSixCharacterRandomString();
        }while (hasGid(gid));
        GroupDO result = GroupDO.builder().
                gid(gid).
                name(requestParam.getName()).
                sortOrder(0).
                username(UserContext.getUsername()).
                build();
        baseMapper.insert(result);


    }

    private boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDO> eq = Wrappers.lambdaQuery(GroupDO.class).
                eq(GroupDO::getGid,gid).
                eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = baseMapper.selectOne(eq);
        return groupDO!=null;
    }

    @Override
    public List<GroupingRespDTO> queryGroup() {
        LambdaQueryWrapper<GroupDO> eq = Wrappers.lambdaQuery(GroupDO.class).eq(GroupDO::getDelFlag, 0).eq(GroupDO::getUsername, UserContext.getUsername()).eq(GroupDO::getSortOrder, 0);
        List<GroupDO> groupDOS = baseMapper.selectList(eq);
        return BeanUtil.copyToList(groupDOS, GroupingRespDTO.class);
    }

    @Override
    public void updateGroup(GroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> eq = Wrappers.lambdaUpdate(GroupDO.class).
                eq(GroupDO::getUsername, UserContext.getUsername()).
                eq(GroupDO::getGid, requestParam.getGid()).
                eq(GroupDO::getDelFlag, 0);
        GroupDO result = GroupDO.builder().name(requestParam.getName()).build();
        baseMapper.update(result,eq);
    }

    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper<GroupDO> eq = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        groupDO.setDelTime(DateTime.now());
        int update = baseMapper.update(groupDO, eq);
        if (update < 1) {
            throw new ClientException("分组删除失败");
        }

    }

    @Override
    public void sortGroup(List<GroupSortReqDTO> requestParam) {
        for (GroupSortReqDTO groupSortReqDTO : requestParam) {
            LambdaUpdateWrapper<GroupDO> eq = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getGid, groupSortReqDTO.getGid())
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getDelFlag, 0);
            GroupDO groupDO = new GroupDO();
            groupDO.setSortOrder(groupSortReqDTO.getSortOrder());
            baseMapper.update(groupDO, eq);
        }
    }
}
