package com.nobody.nobodyplace.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CSGORankingVO implements Serializable {

    Long userId;
    String userName;
    Double ownPrice;

}
