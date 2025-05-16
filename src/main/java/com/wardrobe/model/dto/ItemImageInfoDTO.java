package com.wardrobe.model.dto;

import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author ：luke
 * @date ：Created in 2025/4/30 13:42
 * @description：
 * @modified By：
 */

@Data
public class ItemImageInfoDTO {
    private String name; // 图片名
    private Long size; // 文件大小（字节）
    private String type; // 类型
    private String lastModified; // 最后修改时间
    private String url; // 文件存储路径

    public ItemImageInfoDTO(File file) throws IOException {
        this.name = file.getName();
        this.size = file.length();
        this.type = Files.probeContentType(file.toPath());
        this.lastModified = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(file.lastModified()));
        this.url = file.getAbsolutePath();
    }
}
