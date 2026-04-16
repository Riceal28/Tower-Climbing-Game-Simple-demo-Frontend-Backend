package com.szm.demo.service;

import com.szm.demo.entity.SaveInfo;

import java.util.List;

public interface SaveService {

    void createDefaultSave();
    List<SaveInfo> getSaveByUserId();
    SaveInfo getSaveById();
    void updateSave(SaveInfo saveInfo);
}
