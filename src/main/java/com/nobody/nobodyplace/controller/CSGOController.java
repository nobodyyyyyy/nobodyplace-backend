package com.nobody.nobodyplace.controller;

import com.nobody.nobodyplace.context.BaseContext;
import com.nobody.nobodyplace.pojo.dto.CSGOAddUserItemDTO;
import com.nobody.nobodyplace.pojo.dto.CSGODeleteInventoryDTO;
import com.nobody.nobodyplace.pojo.dto.CSGOInventoryPageQueryDTO;
import com.nobody.nobodyplace.pojo.dto.CSGOItemPageQueryDTO;
import com.nobody.nobodyplace.response.PageResult;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.service.CSGOItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
public class CSGOController {
    private static final Logger Nlog = LoggerFactory.getLogger(CSGOController.class);

    private final CSGOItemService csgoItemService;

    private final RedisTemplate redisTemplate;

    public CSGOController(CSGOItemService csgoItemService, RedisTemplate redisTemplate) {
        this.csgoItemService = csgoItemService;
        this.redisTemplate = redisTemplate;
    }

    private void cleanCache(String pattern) {
        // todo 这是最快的方法吗？
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    @GetMapping(API.GET_ITEM_INFOS)
    @CrossOrigin
    public Result<PageResult> pageQueryItems(CSGOItemPageQueryDTO csgoItemPageQueryDTO){
        Nlog.info("GET_ITEM_INFOS: {}", csgoItemPageQueryDTO);
        // Redis 缓存

        PageResult pageResult = null;
        if (csgoItemPageQueryDTO.getName() == null || csgoItemPageQueryDTO.getName().equals("")) {
            String mainType = csgoItemPageQueryDTO.getMainType() == null ? "" : csgoItemPageQueryDTO.getMainType();
            String exterior = csgoItemPageQueryDTO.getExterior() == null ? "" : csgoItemPageQueryDTO.getExterior();
            int page = csgoItemPageQueryDTO.getPage();
            int pageSize = csgoItemPageQueryDTO.getPageSize();

            String key = "item:exterior" + exterior + ":mainType" + mainType + ":page" + page + ":pageSize" + pageSize;
            PageResult res = (PageResult) redisTemplate.opsForValue().get(key);
            if (res != null && res.getRecords() != null && res.getRecords().size() != 0) {
                Nlog.info("GET_ITEM_INFOS Redis found key = {}", key);
                return Result.success(res);
            }
            pageResult = csgoItemService.pageQuery(csgoItemPageQueryDTO);
            redisTemplate.opsForValue().set(key, pageResult);
        } else {
            pageResult = csgoItemService.pageQuery(csgoItemPageQueryDTO);
        }
        return Result.success(pageResult);
    }

    @PostMapping(API.ADD_USER_ITEM)
    @CrossOrigin
    @ResponseBody
    public Result addUserItem(@RequestBody CSGOAddUserItemDTO csgoAddUserItemDTO) {
        Nlog.info("ADD_USER_ITEM: {}", csgoAddUserItemDTO);
        csgoItemService.addUserItem(csgoAddUserItemDTO);
        return Result.success();
    }

    @GetMapping(API.GET_USER_ITEMS)
    @CrossOrigin
    @ResponseBody
    public Result<PageResult> getUserItems(CSGOInventoryPageQueryDTO csgoInventoryPageQueryDTO) {
        csgoInventoryPageQueryDTO.setUserId(BaseContext.getCurrentId());
        Nlog.info("GET_USER_ITEMS: {}", csgoInventoryPageQueryDTO);
        long userId = csgoInventoryPageQueryDTO.getUserId();
        int page = csgoInventoryPageQueryDTO.getPage();
        int pageSize = csgoInventoryPageQueryDTO.getPageSize();
        String key = "inventory:user" + userId + ":page" + page + ":pagesize" + pageSize;
        PageResult res = (PageResult) redisTemplate.opsForValue().get(key);
        if (res != null && res.getRecords() != null) {
            Nlog.info("GET_USER_ITEMS Redis found key = {}", key);
            return Result.success(res);
        }
        PageResult pageResult = csgoItemService.getUserInventory(csgoInventoryPageQueryDTO);
        redisTemplate.opsForValue().set(key, pageResult);
        return Result.success(pageResult);
    }

    @PostMapping(API.DELETE_USER_ITEM)
    @CrossOrigin
    @ResponseBody
    public Result deleteUserItem(@RequestBody CSGODeleteInventoryDTO csgoDeleteInventoryDTO) {
        long userId = BaseContext.getCurrentId();
        csgoDeleteInventoryDTO.setUserId(userId);
        Nlog.info("DELETE_USER_ITEM: {}", csgoDeleteInventoryDTO);
        cleanCache("inventory:user" + userId + "*");
        csgoItemService.deleteUserInventory(csgoDeleteInventoryDTO);
        return Result.success();
    }

}
