package com.wardrobe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wardrobe.model.entity.ItemImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface ItemImageService extends IService<ItemImage> {
    Long uploadImage(MultipartFile file) throws IOException;

    void deleteImage(Long id);

    void deleteImageByItemId(Long itemId);

    void deleteImageByItemId(Long itemId, List<Long> fileIds);

    List<ItemImage> getImageByItemId(Long itemId);

    void associateImageWithItem(Long itemId, List<Long> fileIds, boolean add);

    HashMap<Long, List<ItemImage>> associateImageWithItemByItemIds(List<Long> itemIds);
} 