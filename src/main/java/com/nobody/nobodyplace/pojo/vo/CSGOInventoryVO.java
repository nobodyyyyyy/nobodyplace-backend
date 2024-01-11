package com.nobody.nobodyplace.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nobody.nobodyplace.pojo.entity.CSGOItem;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CSGOInventoryVO implements Serializable {

    Long id;
    Long itemId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime boughtTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updateTime;
    Float boughtPrice;

//    // 用于联查保存商品信息
//    CSGOItem item;
    String nameCn;
    String nameEng;
    String picUrl;
    String exterior;
    String mainType;
    String subType;

    Float currentPrice;

}
