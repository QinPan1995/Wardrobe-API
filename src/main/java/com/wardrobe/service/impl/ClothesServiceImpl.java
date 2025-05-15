package com.wardrobe.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wardrobe.common.exception.BusinessException;
import com.wardrobe.mapper.ClothesFileMapper;
import com.wardrobe.mapper.ClothesMapper;
import com.wardrobe.model.dto.ClothesDTO;
import com.wardrobe.model.entity.Clothes;
import com.wardrobe.model.entity.User;
import com.wardrobe.model.entity.WardrobeFile;
import com.wardrobe.model.vo.ClothesDetailVO;
import com.wardrobe.model.vo.ClothesMainVO;
import com.wardrobe.model.vo.WardrobeImageVO;
import com.wardrobe.service.ClothesService;
import com.wardrobe.service.FileService;
import com.wardrobe.service.UserService;
import com.wardrobe.util.BeanCopyUtil;
import com.wardrobe.util.OrikaUtil;
import com.wardrobe.util.SeasonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ClothesServiceImpl extends ServiceImpl<ClothesMapper, Clothes> implements ClothesService {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ClothesFileMapper clothesFileMapper;

    @Override
    @Transactional
    public Clothes addClothes(ClothesDTO clothesDTO) {
        return updateClothes(null, clothesDTO);
    }

    @Override
    public ClothesDetailVO getClothes(Long id) {
        //获取衣物信息
        Clothes clothes = getById(id);
        //获取衣物图片
        HashMap<Long, List<WardrobeFile>> longWardrobeFileHashMap = fileService.associateFilesWithClothesByClothesIds(Collections.singletonList(clothes.getId()));
        List<WardrobeFile> wardrobeFileList = Optional.ofNullable(longWardrobeFileHashMap.get(id)).orElse(new ArrayList<>());
        List<WardrobeImageVO> wardrobeImageVOList = BeanCopyUtil.copyProperties(wardrobeFileList, WardrobeImageVO.class);
        ClothesDetailVO clothesDetailVO = new ClothesDetailVO();
        clothesDetailVO.setId(clothes.getId());
        clothesDetailVO.setName(clothes.getName());
        clothesDetailVO.setCategory(clothes.getCategory());
        clothesDetailVO.setSeasons(SeasonUtil.stringToSeasons(clothes.getSeason()));
        clothesDetailVO.setOccasion(clothes.getOccasion());
        clothesDetailVO.setBrand(clothes.getBrand());
        clothesDetailVO.setImages(wardrobeImageVOList);
        clothesDetailVO.setPrice(clothes.getPrice());
        clothesDetailVO.setPurchaseDate(clothes.getPurchaseDate());
        clothesDetailVO.setStorageLocation(clothes.getStorageLocation());
        clothesDetailVO.setRemark(clothes.getRemark());
        return clothesDetailVO;
    }

    @Override
    public Page<ClothesMainVO> getClothes(Integer page, Integer size, String category, String season) {
        Page<Clothes> clothesPage = lambdaQuery()
                .eq(Clothes::getUserId, userService.getCurrentUser().getId())
                .eq(StringUtils.hasText(category), Clothes::getCategory, category)
                .eq(StringUtils.hasText(season), Clothes::getSeason, season)
                .orderByDesc(Clothes::getCreateTime)
                .page(new Page<>(page, size));

        List<Clothes> records = clothesPage.getRecords();

        Page<ClothesMainVO> clothesMainPagePage = new Page<>();
        clothesMainPagePage.setRecords(clothesMains(records));
        clothesMainPagePage.setCurrent(clothesPage.getCurrent());
        clothesMainPagePage.setTotal(clothesPage.getTotal());
        clothesMainPagePage.setSize(clothesPage.getSize());
        clothesMainPagePage.setPages(clothesPage.getPages());
        return clothesMainPagePage;
    }

    /**
     * clothesMainPages
     *
     * @param list
     * @return
     */
    @Override
    public List<ClothesMainVO> clothesMains(List<Clothes> list) {
        List<ClothesMainVO> clothesMainPages = new ArrayList<>();
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
            ClothesMainVO clothesMainPage = OrikaUtil.convert(clothes, ClothesMainVO.class);
            clothesMainPage.setImages(images);
            clothesMainPages.add(clothesMainPage);
        }
        return clothesMainPages;
    }

    @Transactional
    @Override
    public Clothes updateClothes(Long id, ClothesDTO clothesDTO) {
        // 参数校验
        checkClothes(clothesDTO);

        User currentUser = userService.getCurrentUser();

        //新增标记
        boolean add = id == null;
        Clothes clothes = add ? new Clothes() : lambdaQuery()
                .eq(Clothes::getId, id)
                .eq(Clothes::getUserId, currentUser.getId())
                .one();

        if (clothes == null) {
            throw new BusinessException("衣物不存在或无权限修改");
        }

        BeanUtils.copyProperties(clothesDTO, clothes);
        clothes.setUserId(currentUser.getId());
        // 特殊处理
        List<String> seasons = clothesDTO.getSeasons();
        if (!CollectionUtils.isEmpty(seasons)) {
            //seasons排个序
            SeasonUtil.sortSeasons(seasons);
            // 按照自定义顺序排序
            clothes.setSeason(SeasonUtil.seasonsToString(seasons));
        }
        saveOrUpdate(clothes);

        // 关联文件
        fileService.associateFilesWithClothes(clothes.getId(), clothesDTO.getFileIds(), add);
        return clothes;
    }

    @Transactional
    @Override
    public void deleteClothes(Long id) {
        if (id == null) {
            throw new BusinessException("衣物ID不能为空");
        }

        User currentUser = userService.getCurrentUser();

        //删除衣物
        boolean success = lambdaUpdate()
                .eq(Clothes::getId, id)
                .eq(Clothes::getUserId, currentUser.getId())
                .remove();
        if (!success) {
            log.error("衣物删除失败");
            throw new BusinessException("删除失败");
        }
        //删除衣物图片
        fileService.deleteFilesByClothesId(id);
    }

    @Override
    public List<ClothesMainVO> allClotheByUserId(Long userId) {
        List<ClothesMainVO> clothesMainPages = new ArrayList<>();
        List<Clothes> list = lambdaQuery()
                .eq(Clothes::getUserId, userId)
                .list();
        if (CollectionUtils.isEmpty(list)) {
            return clothesMainPages;
        }
        return clothesMains(list);
    }

    /**
     * 参数校验
     *
     * @param clothesDTO
     */
    private void checkClothes(ClothesDTO clothesDTO) {
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
    }
} 