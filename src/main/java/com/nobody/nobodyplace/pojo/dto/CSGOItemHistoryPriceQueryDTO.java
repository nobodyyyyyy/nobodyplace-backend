package com.nobody.nobodyplace.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class CSGOItemHistoryPriceQueryDTO implements Serializable {
    Long itemId;
    String buffCookie;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String from;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String to;
}
