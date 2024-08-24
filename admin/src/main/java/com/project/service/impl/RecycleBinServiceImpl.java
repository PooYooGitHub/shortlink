package com.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.project.common.biz.user.UserContext;
import com.project.common.convention.exception.ServiceException;
import com.project.common.convention.result.Result;
import com.project.dao.entity.GroupDO;
import com.project.dao.mapper.GroupMapper;
import com.project.remote.dto.RecycleBinRemoteService;
import com.project.remote.dto.req.pageRecycleBinShortLinkReqDTO;
import com.project.remote.dto.resp.ShortLinkPageRespDTO;
import com.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final RecycleBinRemoteService recycleBinRemoteService=new RecycleBinRemoteService() {
    };
    private final GroupMapper groupMapper;

    @Override
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(String current, String size) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupDOList)) {
            throw new ServiceException("用户无分组信息");
        }
        pageRecycleBinShortLinkReqDTO requestParam = new pageRecycleBinShortLinkReqDTO();
        requestParam.setGids(groupDOList.stream().map(GroupDO::getGid).toList());
        requestParam.setCurrent(Integer.parseInt(current));
        requestParam.setSize(Integer.parseInt(size));
        return recycleBinRemoteService.pageRecycleBinShortLink(requestParam);
    }
}
