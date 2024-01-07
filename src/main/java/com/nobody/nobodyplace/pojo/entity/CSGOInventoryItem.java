package com.nobody.nobodyplace.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.joda.time.DateTime;

import java.time.LocalDateTime;

@Data
public class CSGOInventoryItem {

    Long id;
    Long userId;
    Long itemId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime boughtTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updateTime;
    Float boughtPrice;

}
