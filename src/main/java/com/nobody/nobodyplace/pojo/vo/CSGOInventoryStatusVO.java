package com.nobody.nobodyplace.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CSGOInventoryStatusVO implements Serializable {

    String spentTotalPrice;
    String currentTotalPrice;
    String gain;
    int itemCount;

}
