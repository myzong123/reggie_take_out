package com.itheima.reggie.service;

import com.itheima.reggie.entity.SetmealDish;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 套餐菜品关系 服务类
 * </p>
 *
 * @author myz
 * @since 2022-12-29
 */
public interface SetmealDishService extends IService<SetmealDish> {
    void checkIds(List<Long> ids);
}
