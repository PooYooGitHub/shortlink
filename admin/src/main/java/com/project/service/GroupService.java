package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.dao.entity.GroupDO;
import com.project.dto.req.GroupAddReqDTO;

/*
   * 短链接分租接口
 */
public interface GroupService extends IService<GroupDO> {

    void addGroup(GroupAddReqDTO requestParam);
}
