package com.szm.demo.controller;

import com.szm.demo.common.ApiConstant;
import com.szm.demo.common.Result;
import com.szm.demo.service.TowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.API_TOWER)
public class TowerController {

    @Autowired
    TowerService towerService;

//    @GetMapping("/")
//    public Result<String>
}
