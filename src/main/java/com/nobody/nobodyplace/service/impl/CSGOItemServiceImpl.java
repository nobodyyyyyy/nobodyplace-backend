package com.nobody.nobodyplace.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nobody.nobodyplace.context.BaseContext;
import com.nobody.nobodyplace.mapper.CSGOItemMapper;
import com.nobody.nobodyplace.pojo.dto.CSGOAddUserItemDTO;
import com.nobody.nobodyplace.pojo.dto.CSGODeleteInventoryDTO;
import com.nobody.nobodyplace.pojo.dto.CSGOInventoryPageQueryDTO;
import com.nobody.nobodyplace.pojo.dto.CSGOItemPageQueryDTO;
import com.nobody.nobodyplace.pojo.entity.CSGOInventoryItem;
import com.nobody.nobodyplace.pojo.entity.CSGOItem;
import com.nobody.nobodyplace.pojo.vo.CSGOInventoryVO;
import com.nobody.nobodyplace.response.PageResult;
import com.nobody.nobodyplace.service.CSGOItemService;
import com.nobody.nobodyplace.utils.TimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CSGOItemServiceImpl implements CSGOItemService {

    final CSGOItemMapper csgoItemMapper;

    public CSGOItemServiceImpl(CSGOItemMapper csgoItemMapper) {
        this.csgoItemMapper = csgoItemMapper;
    }

    @Override
    public PageResult pageQuery(CSGOItemPageQueryDTO csgoItemPageQueryDTO) {
        PageHelper.startPage(csgoItemPageQueryDTO.getPage(), csgoItemPageQueryDTO.getPageSize());
        Page<CSGOItem> page = csgoItemMapper.getByFilterInfo(csgoItemPageQueryDTO);
        long total = page.getTotal();
        List<CSGOItem> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void addUserItem(CSGOAddUserItemDTO csgoAddUserItemDTO) {
        CSGOInventoryItem item = new CSGOInventoryItem();
        BeanUtils.copyProperties(csgoAddUserItemDTO, item);
        item.setBoughtTime(TimeUtil.strToLocalDateTime(csgoAddUserItemDTO.getBoughtDate(), TimeUtil.NORMAL_FORMAT_PATTERN));
        item.setUpdateTime(LocalDateTime.now());
        item.setUserId(BaseContext.getCurrentId());  // 线程池拿当前操作的 userid
        csgoItemMapper.insertInventoryItem(item);
    }

    @Override
    public PageResult getUserInventory(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO) {
        PageHelper.startPage(csgoInventoryPageQueryDTO.getPage(), csgoInventoryPageQueryDTO.getPageSize());
        Page<CSGOInventoryVO> page = csgoItemMapper.getInventoryItem(csgoInventoryPageQueryDTO);
        long total = page.getTotal();
        List<CSGOInventoryVO> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void deleteUserInventory(CSGODeleteInventoryDTO csgoDeleteInventoryDTO) {
        csgoItemMapper.deleteInventoryItem(csgoDeleteInventoryDTO);
    }
}
