package com.wardrobe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wardrobe.model.entity.WardrobeFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface FileService extends IService<WardrobeFile> {
    Long uploadFile(MultipartFile file) throws IOException;

    void deleteFile(Long id);

    List<WardrobeFile> getFilesByClothesId(Long clothesId);

    void associateFilesWithClothes(Long clothesId, List<Long> fileIds, boolean associateOld);

    HashMap<Long, List<WardrobeFile>> associateFilesWithClothesByClothesIds(List<Long> clothesIds);
} 