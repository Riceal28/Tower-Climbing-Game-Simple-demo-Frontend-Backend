package com.szm.demo.service;

import com.szm.demo.entity.ActionInfo;

public interface ActionService {
    void addDefaultAction();
    ActionInfo getByAId(Long actionId);
}
