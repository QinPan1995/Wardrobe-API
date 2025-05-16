package com.wardrobe.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wardrobe.common.exception.BusinessException;
import com.wardrobe.mapper.ItemMapper;
import com.wardrobe.model.dto.ItemDTO;
import com.wardrobe.model.entity.Item;
import com.wardrobe.model.entity.User;
import com.wardrobe.model.entity.ItemImage;
import com.wardrobe.model.vo.ItemDetailVO;
import com.wardrobe.model.vo.ItemMainVO;
import com.wardrobe.model.vo.ItemImageVO;
import com.wardrobe.service.ItemImageService;
import com.wardrobe.service.ItemService;
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
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemImageService itemImageService;

    @Override
    @Transactional
    public Item addItem(ItemDTO itemDTO) {
        return updateItem(null, itemDTO);
    }

    @Override
    public ItemDetailVO getItem(Long id) {
        //获取物品信息
        Item item = getById(id);
        //获取物品图片
        HashMap<Long, List<ItemImage>> longWardrobeFileHashMap = itemImageService.associateImageWithItemByItemIds(Collections.singletonList(item.getId()));
        List<ItemImage> itemImageList = Optional.ofNullable(longWardrobeFileHashMap.get(id)).orElse(new ArrayList<>());
        List<ItemImageVO> itemImageVOList = BeanCopyUtil.copyProperties(itemImageList, ItemImageVO.class);
        ItemDetailVO itemDetailVO = new ItemDetailVO();
        itemDetailVO.setId(item.getId());
        itemDetailVO.setName(item.getName());
        itemDetailVO.setCategory(item.getCategory());
        itemDetailVO.setSeasons(SeasonUtil.stringToSeasons(item.getSeason()));
        itemDetailVO.setOccasion(item.getOccasion());
        itemDetailVO.setBrand(item.getBrand());
        itemDetailVO.setImages(itemImageVOList);
        itemDetailVO.setPrice(item.getPrice());
        itemDetailVO.setPurchaseDate(item.getPurchaseDate());
        itemDetailVO.setStorageLocation(item.getStorageLocation());
        itemDetailVO.setRemark(item.getRemark());
        return itemDetailVO;
    }

    @Override
    public Page<ItemMainVO> getItem(Integer page, Integer size, String category, String season) {
        Page<Item> itemPage = lambdaQuery()
                .eq(Item::getUserId, userService.getCurrentUser().getId())
                .eq(StringUtils.hasText(category), Item::getCategory, category)
                .eq(StringUtils.hasText(season), Item::getSeason, season)
                .orderByDesc(Item::getCreateTime)
                .page(new Page<>(page, size));

        List<Item> records = itemPage.getRecords();

        Page<ItemMainVO> itemMainPagePage = new Page<>();
        itemMainPagePage.setRecords(itemMains(records));
        itemMainPagePage.setCurrent(itemPage.getCurrent());
        itemMainPagePage.setTotal(itemPage.getTotal());
        itemMainPagePage.setSize(itemPage.getSize());
        itemMainPagePage.setPages(itemPage.getPages());
        return itemMainPagePage;
    }

    /**
     * itemMainPages
     *
     * @param list
     * @return
     */
    @Override
    public List<ItemMainVO> itemMains(List<Item> list) {
        List<ItemMainVO> itemMainPages = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return itemMainPages;
        }
        //获取所有物品id
        List<Long> clotheIds = list.stream().map(Item::getId).collect(Collectors.toList());
        //获取所有物品的图片
        HashMap<Long, List<ItemImage>> longWardrobeFileHashMap = itemImageService.associateImageWithItemByItemIds(clotheIds);
        //wardrobeFiles转为
        // 填充文件信息
        for (Item item : list) {
            Long id = item.getId();
            List<ItemImage> itemImageList = Optional.ofNullable(longWardrobeFileHashMap.get(id)).orElse(new ArrayList<>());
            List<String> images = itemImageList.stream().map(ItemImage::getUrl).collect(Collectors.toList());
            ItemMainVO itemMainPage = OrikaUtil.convert(item, ItemMainVO.class);
            itemMainPage.setImages(images);
            itemMainPages.add(itemMainPage);
        }
        return itemMainPages;
    }

    @Transactional
    @Override
    public Item updateItem(Long id, ItemDTO itemDTO) {
        // 参数校验
        checkItem(itemDTO);

        User currentUser = userService.getCurrentUser();

        //新增标记
        boolean add = id == null;
        Item item = add ? new Item() : lambdaQuery()
                .eq(Item::getId, id)
                .eq(Item::getUserId, currentUser.getId())
                .one();

        if (item == null) {
            throw new BusinessException("物品不存在或无权限修改");
        }

        BeanUtils.copyProperties(itemDTO, item);
        item.setUserId(currentUser.getId());
        // 特殊处理
        List<String> seasons = itemDTO.getSeasons();
        if (!CollectionUtils.isEmpty(seasons)) {
            //seasons排个序
            SeasonUtil.sortSeasons(seasons);
            // 按照自定义顺序排序
            item.setSeason(SeasonUtil.seasonsToString(seasons));
        }
        saveOrUpdate(item);

        // 关联文件
        itemImageService.associateImageWithItem(item.getId(), itemDTO.getFileIds(), add);
        return item;
    }

    @Transactional
    @Override
    public void deleteItem(Long id) {
        if (id == null) {
            throw new BusinessException("物品ID不能为空");
        }

        User currentUser = userService.getCurrentUser();

        //删除物品
        boolean success = lambdaUpdate()
                .eq(Item::getId, id)
                .eq(Item::getUserId, currentUser.getId())
                .remove();
        if (!success) {
            log.error("物品删除失败");
            throw new BusinessException("删除失败");
        }
        //删除物品图片
        itemImageService.deleteImageByItemId(id);
    }

    @Override
    public List<ItemMainVO> allClotheByUserId(Long userId) {
        List<ItemMainVO> itemMainPages = new ArrayList<>();
        List<Item> list = lambdaQuery()
                .eq(Item::getUserId, userId)
                .list();
        if (CollectionUtils.isEmpty(list)) {
            return itemMainPages;
        }
        return itemMains(list);
    }

    /**
     * 参数校验
     *
     * @param itemDTO
     */
    private void checkItem(ItemDTO itemDTO) {
        // 参数校验
        if (!StringUtils.hasText(itemDTO.getName())) {
            throw new BusinessException("物品名称不能为空");
        }
        if (!StringUtils.hasText(itemDTO.getCategory())) {
            throw new BusinessException("类型不能为空");
        }
    }
} 