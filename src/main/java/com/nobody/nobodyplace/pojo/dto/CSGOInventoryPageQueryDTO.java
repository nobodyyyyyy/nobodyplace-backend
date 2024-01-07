package com.nobody.nobodyplace.pojo.dto;

import lombok.Data;

@Data
public class CSGOInventoryPageQueryDTO {

    Long userId;
    //页码
    private int page;
    //每页显示记录数
    private int pageSize;

}
