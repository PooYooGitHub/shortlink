package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.dao.entity.GroupDO;
import com.project.dto.req.GroupAddReqDTO;
import com.project.dto.req.GroupSortReqDTO;
import com.project.dto.req.GroupUpdateReqDTO;
import com.project.dto.resp.GroupingRespDTO;

import java.util.List;

/*
   * 短链接分租接口
 */
public interface GroupService extends IService<GroupDO> {

    /**
     * 添加分组
     * @param requestParam
     */
    void addGroup(GroupAddReqDTO requestParam);

    /**
     * 查询用户分组
     * @return
     */
    List<GroupingRespDTO> listGroup();

    /**
     * 根据分组gid修改分组名称
     * @param requestParam
     */
    void updateGroup(GroupUpdateReqDTO requestParam);

    /**
     * 根据gid软删除分组
     * @param gid 分组标识
     */
    void deleteGroup(String gid);

    /**
     * 分組排序
     * @param requestParam
     */

    void sortGroup(List<GroupSortReqDTO> requestParam);
}
