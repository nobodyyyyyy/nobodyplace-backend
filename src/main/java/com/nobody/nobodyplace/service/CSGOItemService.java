package com.nobody.nobodyplace.service;

import com.nobody.nobodyplace.pojo.dto.CSGOItemPageQueryDTO;
import com.nobody.nobodyplace.response.PageResult;

public interface CSGOItemService {

    PageResult pageQuery(CSGOItemPageQueryDTO csgoItemPageQueryDTO);

}
