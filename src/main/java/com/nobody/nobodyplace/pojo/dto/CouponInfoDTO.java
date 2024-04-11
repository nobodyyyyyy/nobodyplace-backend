package com.nobody.nobodyplace.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nobody.nobodyplace.utils.DateConverter;
import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

import javax.persistence.Convert;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class CouponInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String subtitle;
    private Long payValue;
    private Integer stock;

    private LocalDateTime startTime;

}
