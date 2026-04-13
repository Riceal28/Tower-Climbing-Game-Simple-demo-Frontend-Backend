package com.szm.demo.service;

import com.szm.demo.entity.SaveInfo;

import java.util.List;

public interface SaveService {

    void createDefaultSave(Long userId);
    List<SaveInfo> getSaveByUserId(Long userId);
    SaveInfo getSaveById(Long userId, Long id);
    void clearAllActiveSave(Long userId);
    void setSaveActive(Long userId, Long id);
    SaveInfo getActiveSave(Long userId);
    void updateSave(SaveInfo saveInfo,Long userId);
}
