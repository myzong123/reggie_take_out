package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 套餐 服务实现类
 * </p>
 *
 * @author myz
 * @since 2022-12-28
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setmeal_id",id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存setmeal
        this.save(setmealDto);
        // 保存setmealDish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for(SetmealDish setmealDish:setmealDishes){
            setmealDish.setSetmealId(setmealDto.getId().toString());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        // 修改setmeal
        this.updateById(setmealDto);
        // 修改setmealDish 1删除原菜品
        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setmeal_id", setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        // 2添加新菜品
        // 保存setmealDish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for(SetmealDish setmealDish:setmealDishes){
            setmealDish.setSetmealId(setmealDto.getId().toString());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void removeWithDish(List<Long> ids) {
        // 判断是否有在售套餐 在售不能删除
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",ids);
        queryWrapper.eq("status", 1);
        int count = this.count(queryWrapper);
        if(count > 0){
            // 不能删除
            throw new CustomException("套餐在售，不能删除");
        }
        // 可以删除 删除setmeal
        this.removeByIds(ids);
        // 删除setmealDish
        QueryWrapper<SetmealDish> dishQueryWrapper = new QueryWrapper<>();
        queryWrapper.in("setmeal_id", ids);
        setmealDishService.remove(dishQueryWrapper);
    }
}
