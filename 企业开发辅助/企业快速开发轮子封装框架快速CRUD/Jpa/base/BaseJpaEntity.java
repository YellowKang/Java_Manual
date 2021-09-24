package com.kang.shop.jpa.base;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author BigKang
 * @Date 2019/5/12 16:27
 * 通用实体父类，，基于Jpa快速开发
 */
@Data
@MappedSuperclass
public class BaseJpaEntity implements Serializable {
    /**
     * id主键
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 创建时间
     */
    @CreatedBy
    protected Date createTime;

    /**
     * 修改时间
     */
    @LastModifiedBy
    protected Date updateTime;


    /**
     * 逻辑删除
     */
    protected Integer deleted;
}
