package com.szm.demo.service;

import com.szm.demo.entity.ActionInfo;
import com.szm.demo.entity.PlayerActionInfo;

import java.util.List;

public interface ActionService {
    void addDefaultAction();
    ActionInfo getActionByAId(Long actionId);
    PlayerActionInfo getPaById(Long id);//todo:相关更新
    void updatePaOne(PlayerActionInfo playerActionInfo);
    void updatePaAll(List<PlayerActionInfo> list);
    void passRoundOneUpdate(PlayerActionInfo playerActionInfo);
    void passRoundAllUpdate(List<PlayerActionInfo> list);
}
