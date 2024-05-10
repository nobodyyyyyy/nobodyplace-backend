package com.nobody.nobodyplace.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.nobody.nobodyplace.pojo.entity.CSGOItem;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CSGOInventoryVO implements Serializable {

    Long id;
    Long itemId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime boughtTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
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
