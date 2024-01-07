package com.nobody.nobodyplace.mapper;

import com.github.pagehelper.Page;
import com.nobody.nobodyplace.config.AutoFill;
import com.nobody.nobodyplace.pojo.dto.CSGODeleteInventoryDTO;
import com.nobody.nobodyplace.pojo.dto.CSGOInventoryPageQueryDTO;
import com.nobody.nobodyplace.pojo.dto.CSGOItemPageQueryDTO;
import com.nobody.nobodyplace.pojo.entity.CSGOInventoryItem;
import com.nobody.nobodyplace.pojo.entity.CSGOItem;
import com.nobody.nobodyplace.pojo.vo.CSGOInventoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CSGOItemMapper {

    Page<CSGOItem> getByFilterInfo(CSGOItemPageQueryDTO csgoItemPageQueryDTO);

    void insertInventoryItem(CSGOInventoryItem csgoInventoryItem);

    Page<CSGOInventoryVO> getInventoryItem(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO);

    void deleteInventoryItem(CSGODeleteInventoryDTO csgoDeleteInventoryDTO);

}
