package com.nobody.nobodyplace.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class CSGOItemHistoryPriceDTO implements Serializable {

    Long itemId;

    Float price;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime time;


}
