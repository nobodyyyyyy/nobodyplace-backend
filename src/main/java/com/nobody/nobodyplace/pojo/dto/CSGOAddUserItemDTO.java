package com.nobody.nobodyplace.pojo.dto;

import lombok.Data;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

@Data
public class CSGOAddUserItemDTO implements Serializable {

    Long itemId;
    String boughtDate;
    Float boughtPrice;
}
