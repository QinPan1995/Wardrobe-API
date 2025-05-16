package com.wardrobe.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wardrobe.common.FileUtil;
import com.wardrobe.common.exception.BusinessException;
import com.wardrobe.mapper.ItemImageRefMapper;
import com.wardrobe.mapper.ItemImageMapper;
import com.wardrobe.model.dto.ItemImageInfoDTO;
import com.wardrobe.model.entity.ItemImageRef;
import com.wardrobe.model.entity.User;
import com.wardrobe.model.entity.ItemImage;
import com.wardrobe.service.ItemImageService;
import com.wardrobe.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemImageServiceImpl extends ServiceImpl<ItemImageMapper, ItemImage> implements ItemImageService {

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private ItemImageRefMapper itemImageRefMapper;

    @Autowired
    private UserService userService;

    @Override
    public Long uploadImage(MultipartFile file) throws IOException {
        ItemImageInfoDTO itemImageInfoDTO = fileUtil.uploadFile(file);

        User currentUser = userService.getCurrentUser();

        String fileName = org.springframework.util.StringUtils.getFilename(itemImageInfoDTO.getUrl());
        ItemImage itemImage = new ItemImage();
        BeanUtils.copyProperties(itemImageInfoDTO, itemImage);
        //将/Users/youniverse/Wardrobe-API/Wardrobe-API/src/main/resources/static/替换为http://localhost:8080/
        itemImage.setUrl("http://localhost:8080/upload/" + fileName);
        itemImage.setUserId(currentUser.getId());
        save(itemImage);

        return itemImage.getId();
    }

    @Override
    public void deleteImage(Long id) {
        // 检查文件是否被引用
//        Long count = itemFileMapper.selectCount(
//            lambdaQuery().eq(ItemFile::getFileId, id)
//        );
//        if (count > 0) {
//            throw new BusinessException("文件正在被使用，无法删除");
//        }
        removeById(id);
    }

    @Transactional
    @Override
    public void deleteImageByItemId(Long itemId) {
        deleteImageByItemId(itemId, new ArrayList<>());
    }

    /**
     * 删除物品图片
     *
     * @param itemId
     */
    @Transactional
    @Override
    public void deleteImageByItemId(Long itemId, List<Long> fileIds) {
        //获取存在映射
        List<ItemImageRef> wardrobeFileList = itemImageRefMapper.listByItemIds(Collections.singletonList(itemId));
        if (CollectionUtils.isEmpty(wardrobeFileList)) {
            return;
        }
        //删除映射
        int removeNum = itemImageRefMapper.deleteBatchIds(wardrobeFileList);
        if (removeNum <= 0) {
            log.error("物品映射删除失败");
            return;
        }
        //删除图片
        List<Long> removeFileIds = wardrobeFileList.stream().map(ItemImageRef::getItemImageId).filter(fileId -> !Optional.ofNullable(fileIds).orElse(new ArrayList<>()).contains(fileId)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(removeFileIds)) {
            return;
        }
        boolean removed = removeBatchByIds(removeFileIds);
        if (!removed) {
            log.error("物品图片删除失败");
            throw new BusinessException("删除失败");
        }
    }

    @Override
    public List<ItemImage> getImageByItemId(Long itemId) {
        return itemImageRefMapper.getFilesByItemId(itemId);
    }

    @Override
    @Transactional
    public void associateImageWithItem(Long itemId, List<Long> fileIds, boolean add) {
        if (!add) {
            //删除关联
            deleteImageByItemId(itemId, fileIds);
        }
        if (CollectionUtils.isEmpty(fileIds)) {
            return;
        }
        // 创建新关联
        for (Long fileId : fileIds) {
            ItemImageRef itemImageRef = new ItemImageRef();
            itemImageRef.setItemId(itemId);
            itemImageRef.setItemImageId(fileId);
            itemImageRefMapper.insert(itemImageRef);
        }
    }

    @Override
    public HashMap<Long, List<ItemImage>> associateImageWithItemByItemIds(List<Long> itemIds) {
        HashMap<Long, List<ItemImage>> fileMap = new HashMap<>();
        if (CollectionUtils.isEmpty(itemIds)) {
            return fileMap;
        }
        //获取所有相关物品与图片映射信息
        List<ItemImageRef> itemImageRefAll = itemImageRefMapper.listByItemIds(itemIds);
        if (CollectionUtils.isEmpty(itemImageRefAll)){
            return fileMap;
        }
        //获取图片id
        List<Long> fileIdAll = itemImageRefAll.stream().map(ItemImageRef::getItemImageId).collect(Collectors.toList());
        //获取所有图片信息
        List<ItemImage> itemImageAll = listByIds(fileIdAll);
        for (Long itemId : itemIds) {
            //获取当前物品图片
            List<ItemImageRef> itemImageRefList = itemImageRefAll.stream().filter(o -> itemId.equals(o.getItemId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(itemImageRefList)) {
                continue;
            }
            //获取当前物品图片id
            List<Long> fileIds = itemImageRefList.stream().map(ItemImageRef::getItemImageId).collect(Collectors.toList());
            //获取当前物品图片信息
            List<ItemImage> itemImageList = itemImageAll.stream().filter(wardrobeFile -> fileIds.contains(wardrobeFile.getId())).collect(Collectors.toList());
            fileMap.put(itemId, itemImageList);
        }
        return fileMap;
    }
} 