package com.itheima.reggie.service;

import com.itheima.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜品及套餐分类 服务类
 * </p>
 *
 * @author myz
 * @since 2022-12-28
 */
public interface CategoryService extends IService<Category> {
    public void delete(Long id);
}
