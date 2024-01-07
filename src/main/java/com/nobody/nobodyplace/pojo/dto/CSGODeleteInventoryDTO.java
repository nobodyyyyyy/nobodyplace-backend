package com.nobody.nobodyplace.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CSGODeleteInventoryDTO  implements Serializable {

    Long itemId;
    Long userId;
}
