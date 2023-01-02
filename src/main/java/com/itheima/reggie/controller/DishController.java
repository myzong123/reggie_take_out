package com.itheima.reggie.controller;


import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品口味关系表 前端控制器
 * </p>
 *
 * @author myz
 * @since 2022-12-29
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 获取菜品列表
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        // 设置条件
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(name != null,"name",name);
        queryWrapper.orderByAsc("sort");
        // 查询
        dishService.page(pageInfo,queryWrapper);
        // 复制
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        // 得到records
        List<Dish> records = pageInfo.getRecords();
        // 将records 转为List<DishDto> 类型
        List<DishDto> dishDtos = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            // 复制
            BeanUtils.copyProperties(item, dishDto);
            // 通过id得到name
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtos);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id获取菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品信息成功");
    }

    /**
     * 更改菜品状态 停售
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> Stop(@RequestParam List<Long> ids){
        setmealDishService.checkIds(ids);
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Dish> list = dishService.list(queryWrapper);
        for(Dish dish:list){
            if(dish.getStatus() == 0){
                R.error(dish.getName()+"已停售,无法更改");
            }else{
                dish.setStatus(0);
            }
        }
        dishService.updateBatchById(list);
        return R.success("修改状态成功");
    }

    /**
     * 更改菜品状态 启售
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> Start(@RequestParam List<Long> ids){
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Dish> list = dishService.list(queryWrapper);
        for(Dish dish:list){
            if(dish.getStatus() == 1){
                throw new CustomException(dish.getName()+"已启售,无法更改");
            }else{
                dish.setStatus(1);
            }
        }
        dishService.updateBatchById(list);
        return R.success("修改状态成功");
    }

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("添加菜品成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam  List<Long> ids){
        setmealDishService.checkIds(ids);
        dishService.removeBatch(ids);
        return R.success("删除成功");
    }

    /**
     * 获取菜品信息
     * @param dish
     * @return
     */
    @GetMapping("/list")
    // public R<List<Dish>> list(Dish dish){
    //     QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
    //     queryWrapper.eq(dish.getCategoryId() != null,"category_id",dish.getCategoryId());
    //     queryWrapper.eq("status",1);
    //     queryWrapper.orderByAsc("sort");
    //     List<Dish> list = dishService.list(queryWrapper);
    //     return R.success(list);
    // }
    public R<List<DishDto>> list(Dish dish){
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,"category_id",dish.getCategoryId());
        queryWrapper.eq("status",1);
        queryWrapper.orderByAsc("sort");
        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long dishId = item.getId();

            QueryWrapper<DishFlavor> dishFlavorQueryWrapper = new QueryWrapper<>();
            dishFlavorQueryWrapper.eq("dish_id", dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }
}

