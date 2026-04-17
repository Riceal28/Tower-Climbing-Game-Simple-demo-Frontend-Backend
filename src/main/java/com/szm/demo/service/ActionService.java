package com.szm.demo.service;

import com.szm.demo.entity.ActionInfo;
import com.szm.demo.entity.PlayerActionInfo;

public interface ActionService {
    void addDefaultAction();
    ActionInfo getByAId(Long actionId);
}
