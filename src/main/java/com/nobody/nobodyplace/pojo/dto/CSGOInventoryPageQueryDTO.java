package com.nobody.nobodyplace.pojo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CSGOInventoryPageQueryDTO {

    Long userId;
    //页码
    private int page;
    //每页显示记录数
    private int pageSize;

}
