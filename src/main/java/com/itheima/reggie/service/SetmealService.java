package com.itheima.reggie.service;

import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.SetmealDish;

import java.util.List;

/**
 * <p>
 * 套餐 服务类
 * </p>
 *
 * @author myz
 * @since 2022-12-28
 */
public interface SetmealService extends IService<Setmeal> {

    SetmealDto getByIdWithDish(Long id);

    void saveWithDish(SetmealDto setmealDto);

    void updateWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}
