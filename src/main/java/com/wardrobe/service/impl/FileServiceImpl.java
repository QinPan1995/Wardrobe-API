package com.wardrobe.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wardrobe.common.FileUtil;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

        WardrobeFile wardrobeFile = new WardrobeFile();
        BeanUtils.copyProperties(fileInfoDTO, wardrobeFile);

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

    @Override
    public List<WardrobeFile> getFilesByClothesId(Long clothesId) {
        return clothesFileMapper.getFilesByClothesId(clothesId);
    }

    @Override
    @Transactional
    public void associateFilesWithClothes(Long clothesId, List<Long> fileIds, boolean associateOld) {
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
        List<ClothesFile> clothesFiles = clothesFileMapper.listByClothesIds(clothesIds);
        List<Long> fileIds = clothesFiles.stream().map(ClothesFile::getFileId).collect(Collectors.toList());
        List<WardrobeFile> wardrobeFiles = listByIds(fileIds);
        HashMap<Long, List<WardrobeFile>> fileMap = new HashMap<>();
        for (ClothesFile clothesFile : clothesFiles) {
            Long fileId = clothesFile.getFileId();
            Long clothesId = clothesFile.getClothesId();
            List<WardrobeFile> wardrobeFileList = wardrobeFiles.stream().filter(wardrobeFile -> wardrobeFile.getId().equals(fileId)).collect(Collectors.toList());
            fileMap.put(clothesId, wardrobeFileList);
        }
        return fileMap;
    }
} 