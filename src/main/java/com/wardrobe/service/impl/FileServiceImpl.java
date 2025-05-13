package com.wardrobe.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wardrobe.common.FileUtil;
import com.wardrobe.common.exception.BusinessException;
import com.wardrobe.mapper.ClothesFileMapper;
import com.wardrobe.mapper.WardrobeFileMapper;
import com.wardrobe.model.dto.FileInfoDTO;
import com.wardrobe.model.entity.ClothesFile;
import com.wardrobe.model.entity.User;
import com.wardrobe.model.entity.WardrobeFile;
import com.wardrobe.service.FileService;
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
public class FileServiceImpl extends ServiceImpl<WardrobeFileMapper, WardrobeFile> implements FileService {

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private ClothesFileMapper clothesFileMapper;

    @Autowired
    private UserService userService;

    @Override
    public Long uploadFile(MultipartFile file) throws IOException {
        FileInfoDTO fileInfoDTO = fileUtil.uploadFile(file);

        User currentUser = userService.getCurrentUser();

        String fileName = org.springframework.util.StringUtils.getFilename(fileInfoDTO.getUrl());
        WardrobeFile wardrobeFile = new WardrobeFile();
        BeanUtils.copyProperties(fileInfoDTO, wardrobeFile);
        //将/Users/youniverse/Wardrobe-API/Wardrobe-API/src/main/resources/static/替换为http://localhost:8080/
        wardrobeFile.setUrl("http://localhost:8080/upload/" + fileName);
        wardrobeFile.setUserId(currentUser.getId());
        save(wardrobeFile);

        return wardrobeFile.getId();
    }

    @Override
    public void deleteFile(Long id) {
        // 检查文件是否被引用
//        Long count = clothesFileMapper.selectCount(
//            lambdaQuery().eq(ClothesFile::getFileId, id)
//        );
//        if (count > 0) {
//            throw new BusinessException("文件正在被使用，无法删除");
//        }
        removeById(id);
    }

    @Transactional
    @Override
    public void deleteFilesByClothesId(Long clothesId) {
        deleteFilesByClothesId(clothesId, new ArrayList<>());
    }

    /**
     * 删除衣物图片
     *
     * @param clothesId
     */
    @Transactional
    @Override
    public void deleteFilesByClothesId(Long clothesId, List<Long> fileIds) {
        //获取存在映射
        List<ClothesFile> wardrobeFileList = clothesFileMapper.listByClothesIds(Collections.singletonList(clothesId));
        if (CollectionUtils.isEmpty(wardrobeFileList)) {
            log.error("衣物映射不存在");
            return;
        }
        //删除映射
        int removeNum = clothesFileMapper.deleteBatchIds(wardrobeFileList);
        if (removeNum <= 0) {
            log.error("衣物映射删除失败");
            return;
        }
        //删除图片
        List<Long> removeFileIds = wardrobeFileList.stream().map(ClothesFile::getFileId).filter(fileId -> !Optional.ofNullable(fileIds).orElse(new ArrayList<>()).contains(fileId)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(removeFileIds)) {
            return;
        }
        boolean removed = removeBatchByIds(removeFileIds);
        if (!removed) {
            log.error("衣物图片删除失败");
            throw new BusinessException("删除失败");
        }
    }

    public static void main(String[] args) {
        List<Long> a = null;
//        a.add(1L);
//        a.add(2L);
        List<Long> b = new ArrayList<>();
        b.add(1L);
        b.add(3L);
        List<Long> collect = b.stream().filter(v -> !a.contains(v)).collect(Collectors.toList());
        System.out.println(collect);
    }

    @Override
    public List<WardrobeFile> getFilesByClothesId(Long clothesId) {
        return clothesFileMapper.getFilesByClothesId(clothesId);
    }

    @Override
    @Transactional
    public void associateFilesWithClothes(Long clothesId, List<Long> fileIds, boolean add) {
        if (!add) {
            //删除关联
            deleteFilesByClothesId(clothesId, fileIds);
        }
        if (CollectionUtils.isEmpty(fileIds)) {
            return;
        }
        // 创建新关联
        for (Long fileId : fileIds) {
            ClothesFile clothesFile = new ClothesFile();
            clothesFile.setClothesId(clothesId);
            clothesFile.setFileId(fileId);
            clothesFileMapper.insert(clothesFile);
        }
    }

    @Override
    public HashMap<Long, List<WardrobeFile>> associateFilesWithClothesByClothesIds(List<Long> clothesIds) {
        HashMap<Long, List<WardrobeFile>> fileMap = new HashMap<>();
        if (CollectionUtils.isEmpty(clothesIds)) {
            return fileMap;
        }
        //获取所有相关衣物与图片映射信息
        List<ClothesFile> clothesFileAll = clothesFileMapper.listByClothesIds(clothesIds);
        if (CollectionUtils.isEmpty(clothesFileAll)){
            return fileMap;
        }
        //获取图片id
        List<Long> fileIdAll = clothesFileAll.stream().map(ClothesFile::getFileId).collect(Collectors.toList());
        //获取所有图片信息
        List<WardrobeFile> wardrobeFileAll = listByIds(fileIdAll);
        for (Long clothesId : clothesIds) {
            //获取当前衣物图片
            List<ClothesFile> clothesFileList = clothesFileAll.stream().filter(o -> clothesId.equals(o.getClothesId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(clothesFileList)) {
                continue;
            }
            //获取当前衣物图片id
            List<Long> fileIds = clothesFileList.stream().map(ClothesFile::getFileId).collect(Collectors.toList());
            //获取当前衣物图片信息
            List<WardrobeFile> wardrobeFileList = wardrobeFileAll.stream().filter(wardrobeFile -> fileIds.contains(wardrobeFile.getId())).collect(Collectors.toList());
            fileMap.put(clothesId, wardrobeFileList);
        }
        return fileMap;
    }
} 