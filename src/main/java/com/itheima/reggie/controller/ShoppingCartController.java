package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 购物车 前端控制器
 * </p>
 *
 * @author myz
 * @since 2022-12-30
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 获取购物车列表
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        Long dishId = shoppingCart.getDishId();
        Long userId = BaseContext.getCurrentId();
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        if(dishId != null){
            // 添加是菜品
            queryWrapper.eq("dish_id",dishId);
        }else{
            // 添加的是套餐
            Long setmealId = shoppingCart.getSetmealId();
            queryWrapper.eq("setmeal_id",setmealId);
        }
        ShoppingCart sc = shoppingCartService.getOne(queryWrapper);
        if(sc == null){
            // 添加的是第一份
            shoppingCart.setUserId(userId);
            shoppingCart.setNumber(1);
            sc = shoppingCart;
            shoppingCartService.save(sc);
        }else{
            sc.setNumber(sc.getNumber() + 1);
            shoppingCartService.updateById(sc);
        }
        return R.success(sc);
    }

    @PostMapping("sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        Long dishId = shoppingCart.getDishId();
        Long userId = BaseContext.getCurrentId();
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        if(dishId != null){
            queryWrapper.eq("dish_id", dishId);
        } else {
            queryWrapper.eq("setmeal_id",shoppingCart.getSetmealId());
        }
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        if(cart.getNumber() <= 0){
            throw new CustomException("购物车没有该商品");
        }
        cart.setNumber(cart.getNumber() - 1);
        shoppingCartService.updateById(cart);
        return R.success(cart);
    }
    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long currentId = BaseContext.getCurrentId();
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",currentId);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}

