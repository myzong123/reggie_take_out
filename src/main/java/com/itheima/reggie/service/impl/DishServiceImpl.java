package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 菜品管理 服务实现类
 * </p>
 *
 * @author myz
 * @since 2022-12-28
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealDishService setmealDishService;

    // 多表操作添加事务
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 保存dish
        super.save(dishDto);
        Long id = dishDto.getId();

        // 获取flavors 列表
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 设置id
        for(DishFlavor flavor:flavors){
            flavor.setDishId(id);
        }
        // 保存dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 根据id获取dish
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        // 将dish复制到dishDto
        BeanUtils.copyProperties(dish, dishDto);
        // 通过dish_id 得到dishFlavorList
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dish_id",id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
        // 设置dishFlavorList
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        // 删除原口味
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dish_id",dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        // 添加新口味
        List<DishFlavor> dishFlavorList = dishDto.getFlavors();
        // 设置dish_id
        for(DishFlavor dishFlavor:dishFlavorList){
            dishFlavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(dishFlavorList);
    }

    @Override
    public void removeBatch(List<Long> ids) {
        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("dish_id", ids);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        if(!list.isEmpty()){
            StringBuilder name = new StringBuilder();
            for(SetmealDish setmealDish: list){
                String dishId = setmealDish.getDishId();
                Dish dish = this.getById(dishId);
                name.append(dish.getName()).append("\r\n");
            }
            throw new CustomException(name + "在套餐中不可删除");
        }
        this.removeByIds(ids);
    }
}
