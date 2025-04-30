# Wardrobe-API

基于Spring Boot的衣物管理系统后端API

## 项目概述

本项目是一个衣物管理小程序的后端API服务，提供用户认证、衣物管理等功能。

## 技术栈

- Spring Boot 2.7.x
- Spring Security
- MySQL 8.0
- MyBatis-Plus
- JWT (JSON Web Token)
- Maven

## 实现步骤

### 数据库设计

#### 用户表(user)

```sql
CREATE TABLE user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    openid VARCHAR(64) NOT NULL COMMENT '微信openid',
    nickname VARCHAR(32) DEFAULT NULL COMMENT '用户昵称',
    avatar VARCHAR(256) DEFAULT NULL COMMENT '头像URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE clothes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(64) NOT NULL COMMENT '衣物名称',
    category VARCHAR(32) NOT NULL COMMENT '分类',
    seasons VARCHAR(64) NOT NULL COMMENT '适用季节，多个以逗号分隔',
    color VARCHAR(32) DEFAULT NULL COMMENT '颜色',
    description VARCHAR(512) DEFAULT NULL COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='衣物表';

CREATE TABLE category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(32) NOT NULL COMMENT '分类名称',
    description VARCHAR(256) DEFAULT NULL COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE file (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT '文件名',
    size BIGINT NOT NULL COMMENT '文件大小（字节）',
    type VARCHAR(64) NOT NULL COMMENT 'MIME类型',
    url VARCHAR(256) NOT NULL COMMENT '文件存储路径',
    last_modified DATETIME NOT NULL COMMENT '最后修改时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

CREATE TABLE clothes_file (
    id BIGINT NOT NULL AUTO_INCREMENT,
    clothes_id BIGINT NOT NULL COMMENT '衣物ID',
    file_id BIGINT NOT NULL COMMENT '文件ID',
    is_main TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否主图，1是0否',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_clothes_file (clothes_id, file_id),
    KEY idx_file_id (file_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='衣物图片关联表';

用户认证模块
实现JWT工具类(JwtUtil)
配置Spring Security
实现用户登录接口
添加JWT认证过滤器
衣物管理模块
实现衣物CRUD接口
添加分页查询功能
实现图片上传功能
添加分类管理功能
异常处理
自定义业务异常(BusinessException)
实现全局异常处理器
添加文件上传异常处理
API 示例
微信登录接口
请求参数

json
深色版本
{
    "code": "微信登录code"
}
返回结果

json
深色版本
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1..."
    }
}
新增衣物接口
请求参数

json
深色版本
{
    "name": "衣物名称",
    "category": "分类",
    "season": "季节",
    "color": "颜色",
    "imageUrl": "图片地址",
    "description": "描述"
}
返回结果

json
深色版本
{
    "code": 200,
    "message": "success",
    "data": "图片访问地址"
}
新增分类接口
请求参数

json
深色版本
{
    "name": "分类名称",
    "description": "描述"
}
文件上传配置
yaml
深色版本
file:
  upload-path: /path/to/upload/ # 文件上传路径
  access-path: /upload/ # 文件访问路径
JWT配置
yaml
深色版本
jwt:
  secret: your_jwt_secret_key # JWT密钥
  expiration: 604800 # Token有效期(秒)
微信小程序配置
yaml
深色版本
wx:
  miniapp:
    appid: your_appid # 小程序appid
    secret: your_secret # 小程序secret
注意事项
运行前需要创建数据库和相关表。
配置文件中的路径、密钥等需要根据实际环境修改。
文件上传目录需要有写入权限。
所有接口除登录外都需要携带token。
token格式：Bearer {token}。
下一步优化计划
添加Redis缓存。
实现模糊搜索。
添加数据统计功能。
优化图片上传（支持压缩、水印等）。
添加接口访问频率限制。