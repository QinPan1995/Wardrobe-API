package com.wardrobe.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wardrobe.common.exception.BusinessException;
import com.wardrobe.mapper.ClothesMapper;
import com.wardrobe.model.dto.ClothesDTO;
import com.wardrobe.model.entity.Clothes;
import com.wardrobe.model.entity.User;
import com.wardrobe.model.entity.WardrobeFile;
import com.wardrobe.model.vo.ClothesMainVo;
import com.wardrobe.service.ClothesService;
import com.wardrobe.service.FileService;
import com.wardrobe.service.UserService;
import com.wardrobe.util.SeasonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClothesServiceImpl extends ServiceImpl<ClothesMapper, Clothes> implements ClothesService {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Override
    @Transactional
    public Clothes addClothes(ClothesDTO clothesDTO) {
        // 参数校验
        if (!StringUtils.hasText(clothesDTO.getName())) {
            throw new BusinessException("衣物名称不能为空");
        }
        if (!StringUtils.hasText(clothesDTO.getCategory())) {
            throw new BusinessException("衣物类型不能为空");
        }
        if (CollectionUtils.isEmpty(clothesDTO.getSeasons())) {
            throw new BusinessException("适用季节不能为空");
        }

        User currentUser = userService.getCurrentUser();

        Clothes clothes = new Clothes();
        BeanUtils.copyProperties(clothesDTO, clothes);

        clothes.setUserId(currentUser.getId());

        // 特殊处理
        List<String> seasons = clothesDTO.getSeasons();
        if (!CollectionUtils.isEmpty(seasons)) {
            //seasons排个序
            SeasonUtil.sortSeasons(seasons);
            // 按照自定义顺序排序
            clothes.setSeason(String.join(",", seasons));
        }

        // 保存衣物基本信息
        save(clothes);

        // 关联文件
        if (!CollectionUtils.isEmpty(clothesDTO.getFileIds())) {
            fileService.associateFilesWithClothes(clothes.getId(), clothesDTO.getFileIds(), false);
        }
        return clothes;
    }

    @Override
    public Page<ClothesMainVo> getClothes(Integer page, Integer size, String category, String season) {
        Page<Clothes> clothesPage = lambdaQuery()
                .eq(Clothes::getUserId, userService.getCurrentUser().getId())
                .eq(StringUtils.hasText(category), Clothes::getCategory, category)
                .eq(StringUtils.hasText(season), Clothes::getSeason, season)
                .orderByDesc(Clothes::getCreateTime)
                .page(new Page<>(page, size));

        List<Clothes> records = clothesPage.getRecords();

        Page<ClothesMainVo> clothesMainPagePage = new Page<>();
        clothesMainPagePage.setRecords(clothesMains(records));
        clothesMainPagePage.setCurrent(clothesPage.getCurrent());
        clothesMainPagePage.setTotal(clothesPage.getTotal());
        clothesMainPagePage.setSize(clothesPage.getSize());
        clothesMainPagePage.setPages(clothesPage.getPages());
        return clothesMainPagePage;
    }

    /**
     *  clothesMainPages
     * @param list
     * @return
     */
    @Override
    public List<ClothesMainVo> clothesMains(List<Clothes> list) {
        List<ClothesMainVo> clothesMainPages = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return clothesMainPages;
        }
        //获取所有衣物id
        List<Long> clotheIds = list.stream().map(Clothes::getId).collect(Collectors.toList());
        //获取所有衣物的图片
        HashMap<Long, List<WardrobeFile>> longWardrobeFileHashMap = fileService.associateFilesWithClothesByClothesIds(clotheIds);
        //wardrobeFiles转为
        // 填充文件信息
        for (Clothes clothes : list) {
            Long id = clothes.getId();
            List<WardrobeFile> wardrobeFileList = Optional.ofNullable(longWardrobeFileHashMap.get(id)).orElse(new ArrayList<>());
            List<String> images = wardrobeFileList.stream().map(WardrobeFile::getUrl).collect(Collectors.toList());
            ClothesMainVo clothesMainPage = new ClothesMainVo();
            clothesMainPage.setId(id);
            clothesMainPage.setCategory(clothes.getCategory());
            clothesMainPage.setImages(images);
            clothesMainPages.add(clothesMainPage);
        }
        return clothesMainPages;
    }

    @Override
    public Clothes updateClothes(Long id, ClothesDTO clothesDTO) {
        // 参数校验
        if (id == null) {
            throw new BusinessException("衣物ID不能为空");
        }

        User currentUser = userService.getCurrentUser();

        Clothes clothes = lambdaQuery()
                .eq(Clothes::getId, id)
                .eq(Clothes::getUserId, currentUser.getId())
                .one();

        if (clothes == null) {
            throw new BusinessException("衣物不存在或无权限修改");
        }

        BeanUtils.copyProperties(clothesDTO, clothes);

        updateById(clothes);
        return clothes;
    }

    @Override
    public void deleteClothes(Long id) {
        if (id == null) {
            throw new BusinessException("衣物ID不能为空");
        }

        User currentUser = userService.getCurrentUser();

        boolean success = lambdaUpdate()
                .eq(Clothes::getId, id)
                .eq(Clothes::getUserId, currentUser.getId())
                .remove();

        if (!success) {
            throw new BusinessException("衣物不存在或无权限删除");
        }
    }

    @Override
    public List<ClothesMainVo> allClotheByUserId(Long userId) {
        List<ClothesMainVo> clothesMainPages = new ArrayList<>();
        List<Clothes> list = lambdaQuery()
                .eq(Clothes::getUserId, userId)
                .list();
        if (CollectionUtils.isEmpty(list)){
            return clothesMainPages;
        }
        return clothesMains(list);
    }
} 