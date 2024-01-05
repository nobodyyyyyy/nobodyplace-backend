package com.nobody.nobodyplace.pojo.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CSGOItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemId;
    private String nameCn;
    private String nameEng;
    private String picUrl;
    private String exterior;
    private String mainType;
    private String subType;
}
