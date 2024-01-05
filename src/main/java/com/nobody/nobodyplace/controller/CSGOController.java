package com.nobody.nobodyplace.controller;

import com.nobody.nobodyplace.pojo.dto.CSGOItemPageQueryDTO;
import com.nobody.nobodyplace.properties.JwtProperties;
import com.nobody.nobodyplace.response.PageResult;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.service.CSGOItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CSGOController {
    private static final Logger Nlog = LoggerFactory.getLogger(CSGOController.class);

    @Autowired
    private CSGOItemService csgoItemService;

    @Autowired
    private JwtProperties jwtProperties;


    @GetMapping(API.GET_ITEM_INFOS)
    @CrossOrigin
    public Result<PageResult> page(CSGOItemPageQueryDTO csgoItemPageQueryDTO){
        Nlog.info("GET_ITEM_INFOS: {}", csgoItemPageQueryDTO);
        PageResult pageResult = csgoItemService.pageQuery(csgoItemPageQueryDTO);//后续定义
        return Result.success(pageResult);
    }


}
