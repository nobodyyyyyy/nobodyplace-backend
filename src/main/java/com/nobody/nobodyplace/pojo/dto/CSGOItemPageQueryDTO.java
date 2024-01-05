package com.nobody.nobodyplace.pojo.dto;

import lombok.Data;

@Data
public class CSGOItemPageQueryDTO {

    // 商品的名字（中文）
    private String name;

    // 主要类型
    private String mainType;

    // 次要类型
    private String subType;

    // 磨损
    private String exterior;

    //页码
    private int page;  // 必须
    //每页显示记录数
    private int pageSize;  // 必须

}
