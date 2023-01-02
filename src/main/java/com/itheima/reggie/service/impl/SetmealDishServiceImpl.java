package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealDishMapper;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealDishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 套餐菜品关系 服务实现类
 * </p>
 *
 * @author myz
 * @since 2022-12-29
 */
@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {

    @Autowired
    private DishService dishService;
    @Override
    public void checkIds(List<Long> ids) {
        QueryWrapper<SetmealDish> setmealDishQueryWrapper = new QueryWrapper<>();
        setmealDishQueryWrapper.in("dish_id", ids);
        List<SetmealDish> setmealDishList = super.list(setmealDishQueryWrapper);
        if(!setmealDishList.isEmpty()){
            StringBuilder stringBuilder = new StringBuilder();
            for(SetmealDish setmealDish : setmealDishList){
                String dishId = setmealDish.getDishId();
                Dish dish = dishService.getById(dishId);
                stringBuilder.append(dish.getName()).append(" ");
            }
           throw new CustomException(stringBuilder + "在套餐中");
        }
    }
}
