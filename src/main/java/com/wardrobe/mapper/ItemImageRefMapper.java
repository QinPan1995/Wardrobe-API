package com.wardrobe.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wardrobe.model.entity.ItemImageRef;
import com.wardrobe.model.entity.ItemImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ItemImageRefMapper extends BaseMapper<ItemImageRef> {
    @Select("SELECT f.* FROM item_image f " +
            "JOIN item_image_ref cf ON f.id = cf.item_image_id " +
            "WHERE f.deleted=0 and cf.deleted=0 and cf.item_id = #{itemId}")
    List<ItemImage> getFilesByItemId(Long itemId);

    default int delByItemId(Long itemId){
        LambdaQueryWrapper<ItemImageRef> itemFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        itemFileLambdaQueryWrapper.eq(ItemImageRef::getItemId, itemId);
        // 删除原有关联
        return delete(itemFileLambdaQueryWrapper);
    }

    default List<ItemImageRef> listByItemIds(List<Long> itemIds){
        LambdaQueryWrapper<ItemImageRef> itemFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        itemFileLambdaQueryWrapper.in(ItemImageRef::getItemId, itemIds);
        // 查询关联
        return selectList(itemFileLambdaQueryWrapper);
    }
}