package com.project.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupAddReqDTO {
    /**
     * 词条名
     */
    public String groupName;
}
