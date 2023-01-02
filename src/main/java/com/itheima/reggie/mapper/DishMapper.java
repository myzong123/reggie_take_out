package com.itheima.reggie.mapper;

import com.itheima.reggie.entity.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 菜品管理 Mapper 接口
 * </p>
 *
 * @author myz
 * @since 2022-12-28
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}
