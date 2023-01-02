package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author myz
 * @since 2022-12-28
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜品 添加套餐
     * @param category
     * @return
     */
    @PostMapping
    public R<String> addCategory(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品和套餐分页查询
     * @param map
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam Map<String, String> map){
        String page = map.get("page");
        String pageSize = map.get("pageSize");

        Page<Category> pageInfo = new Page<>(Integer.parseInt(page), Integer.parseInt(pageSize));
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除菜品 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        categoryService.delete(ids);
        return R.success("删除成功");
    }

    /**
     * 修改菜品 修改套餐
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 获取菜品列表
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        // 设置条件
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        // 查询菜品并按照sort升序排列 然后 创建时间降序排列
        queryWrapper.eq(category.getType() != null,"type", category.getType());
        queryWrapper.orderByAsc("sort").orderByDesc("create_time");
        // 根据条件查询
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}

