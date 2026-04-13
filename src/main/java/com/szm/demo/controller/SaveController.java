package com.szm.demo.controller;

import com.szm.demo.common.ApiConstant;
import com.szm.demo.common.Result;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.service.SaveService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = ApiConstant.API_SAVE)
public class SaveController {

    @Autowired
    SaveService saveService;

    @PostMapping("/create")
    public Result<String> createDefaultSave(HttpServletRequest request){
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        saveService.createDefaultSave(userId);
        return Result.success("创建存档成功");
    }

    @GetMapping("/showall")
    public Result<List<SaveInfo>> getSaves(HttpServletRequest request){
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        List<SaveInfo> saveInfoList = saveService.getSaveByUserId(userId);
        return Result.success(saveInfoList);
    }

    @GetMapping("/showone/{id}")
    public Result<SaveInfo> getOneSave(@PathVariable Long id ,HttpServletRequest request){
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        SaveInfo saveInfo = saveService.getSaveById(userId,id);
        return Result.success(saveInfo);
    }

    @PostMapping("/active/{id}")
    public Result<String> playOneSave(@PathVariable Long id,HttpServletRequest request){
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        saveService.setSaveActive(userId,id);
        return Result.success("激活存档成功");
    }

    @GetMapping("/showactive")
    public Result<SaveInfo> getActiveSave(HttpServletRequest request){
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        SaveInfo saveInfo = saveService.getActiveSave(userId);
        return Result.success(saveInfo);
    }
}
