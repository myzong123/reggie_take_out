package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import com.sun.media.jfxmedia.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 套餐菜品关系 前端控制器
 * </p>
 *
 * @author myz
 * @since 2022-12-29
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;

    /**
     * 套餐列表
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDishPage = new Page<>(page, pageSize);
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(name != null,"name", name);
        queryWrapper.orderByAsc("sort");
        setmealService.page(pageInfo);
        BeanUtils.copyProperties(pageInfo, setmealDishPage,"records");
        List<Setmeal> recordList = pageInfo.getRecords();
        List<SetmealDto> list = recordList.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDishPage.setRecords(list);
        return R.success(setmealDishPage);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 修改套餐状态0 停售
     */
    @PostMapping("/status/0")
    public R<String> status0(@RequestParam List<Long> ids){
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Setmeal> list = setmealService.list(queryWrapper);
        for(Setmeal setmeal:list){
            if(setmeal.getStatus() == 0){
                throw new CustomException(setmeal.getName()+"已停售,无法更改");
            }else{
                setmeal.setStatus(0);
            }
        }
        setmealService.updateBatchById(list);
        return R.success("修改状态成功");
    }
    /**
     * 修改套餐状态1 起售
     */
    @PostMapping("/status/1")
    public R<String> status1(@RequestParam List<Long> ids){
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Setmeal> list = setmealService.list(queryWrapper);
        for(Setmeal setmeal:list){
            if(setmeal.getStatus() == 1){
                throw new CustomException(setmeal.getName()+"已启售,无法更改");
            }else{
                setmeal.setStatus(1);
            }
        }
        setmealService.updateBatchById(list);
        return R.success("修改状态成功");
    }

    /**
     * 展示套餐列表
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        Long categoryId = setmeal.getCategoryId();
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(categoryId != null, "category_id", categoryId);
        queryWrapper.eq("status",1);
        queryWrapper.orderByAsc("update_time");
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}

