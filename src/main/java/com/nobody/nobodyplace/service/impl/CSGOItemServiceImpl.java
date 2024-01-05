package com.nobody.nobodyplace.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nobody.nobodyplace.mapper.CSGOItemMapper;
import com.nobody.nobodyplace.pojo.dto.CSGOItemPageQueryDTO;
import com.nobody.nobodyplace.pojo.entity.CSGOItem;
import com.nobody.nobodyplace.response.PageResult;
import com.nobody.nobodyplace.service.CSGOItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CSGOItemServiceImpl implements CSGOItemService {

    @Autowired
    CSGOItemMapper csgoItemMapper;

    @Override
    public PageResult pageQuery(CSGOItemPageQueryDTO csgoItemPageQueryDTO) {
        PageHelper.startPage(csgoItemPageQueryDTO.getPage(), csgoItemPageQueryDTO.getPageSize());
        Page<CSGOItem> page = csgoItemMapper.getByFilterInfo(csgoItemPageQueryDTO);
        long total = page.getTotal();
        List<CSGOItem> records = page.getResult();
        return new PageResult(total, records);
    }
}
