package com.wardrobe.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wardrobe.model.entity.ClothesFile;
import com.wardrobe.model.entity.WardrobeFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClothesFileMapper extends BaseMapper<ClothesFile> {
    @Select("SELECT f.* FROM file f " +
            "JOIN clothes_file cf ON f.id = cf.file_id " +
            "WHERE cf.clothes_id = #{clothesId}")
    List<WardrobeFile> getFilesByClothesId(Long clothesId);

    default int delByClothesId(Long clothesId){
        LambdaQueryWrapper<ClothesFile> clothesFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        clothesFileLambdaQueryWrapper.eq(ClothesFile::getClothesId, clothesId);
        // 删除原有关联
        return delete(clothesFileLambdaQueryWrapper);
    }

    default List<ClothesFile> listByClothesIds(List<Long> clothesIds){
        LambdaQueryWrapper<ClothesFile> clothesFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        clothesFileLambdaQueryWrapper.in(ClothesFile::getClothesId, clothesIds);
        // 查询关联
        return selectList(clothesFileLambdaQueryWrapper);
    }
}