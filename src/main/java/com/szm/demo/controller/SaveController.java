package com.szm.demo.controller;

import com.szm.demo.common.ApiConstant;
import com.szm.demo.common.Result;
import com.szm.demo.dto.SaveLoadResp;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.service.PlayerService;
import com.szm.demo.service.SaveProviderService;
import com.szm.demo.service.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = ApiConstant.API_SAVE)
public class SaveController {

    @Autowired
    SaveService saveService;

    @Autowired
    PlayerService playerService;
    @Autowired
    private SaveProviderService saveProviderService;


    @PostMapping("/create")
    public Result<String> createDefaultSave(){
        saveService.createDefaultSave();
        return Result.success("创建存档成功");
    }

    @GetMapping("/showall")
    public Result<List<SaveInfo>> getSaves(){
        List<SaveInfo> saveInfoList = saveService.getSaveByUserId();
        return Result.success(saveInfoList);
    }

    @GetMapping("/showallp")
    public Result<List<SaveInfo>> getSavesByP(){
        List<SaveInfo> saveInfoList = saveService.getSaveByPlayerId();
        return Result.success(saveInfoList);
    }

    @GetMapping("/show")
    public Result<SaveInfo> getOneSave(){
        SaveInfo saveInfo = saveService.getSaveById();
        return Result.success(saveInfo);
    }

    @PostMapping("/save")
    public Result<String> saveSave(@RequestBody SaveInfo saveInfo){
        saveProviderService.updateSave(saveInfo);
        return Result.success("保存成功");
    }

    @PostMapping("/load")
    public Result<SaveLoadResp> loadSave(@RequestBody SaveInfo saveInfo){
        SaveLoadResp resp = playerService.updatePlayerBySave(saveInfo);
        return Result.success(resp);
    }

}
