package com.nobody.nobodyplace.mapper;

import com.github.pagehelper.Page;
import com.nobody.nobodyplace.pojo.dto.CSGOItemPageQueryDTO;
import com.nobody.nobodyplace.pojo.entity.CSGOItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CSGOItemMapper {

    Page<CSGOItem> getByFilterInfo(CSGOItemPageQueryDTO csgoItemPageQueryDTO);


}
