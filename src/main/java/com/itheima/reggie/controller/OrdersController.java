package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrdersService;
import com.itheima.reggie.service.ShoppingCartService;
import com.itheima.reggie.service.UserService;
import jdk.nashorn.internal.runtime.regexp.joni.encoding.IntHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.mail.FetchProfile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author myz
 * @since 2022-12-30
 */
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;
    /**
     * 下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 用户端订单分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(Integer page, Integer pageSize){
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> pageOrderDto = new Page<>(page, pageSize);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("order_time");
        ordersService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,pageOrderDto,"records");

        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtos = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);
            String number = item.getNumber();
            QueryWrapper<OrderDetail> queryWrapperOD = new QueryWrapper<>();
            queryWrapperOD.eq("order_id", number);
            List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapperOD);
            orderDto.setOrderDetails(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());

        pageOrderDto.setRecords(orderDtos);
        return R.success(pageOrderDto);
    }

    /**
     * 后来分页查询
     * @return
     */
    @GetMapping("page")
    public R<Page> page(Integer page, Integer pageSize, Integer number, String beginTime, String endTime){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> pageOrderDto = new Page<>(page, pageSize);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(number != null, "number", number);
        if(beginTime != null){
            LocalDateTime begin = LocalDateTime.parse(beginTime, df);
            queryWrapper.ge("order_time", begin);
        }
        if(endTime != null){
            LocalDateTime end = LocalDateTime.parse(endTime, df);
            queryWrapper.le( "order_time", end);
        }
        queryWrapper.orderByDesc("order_time");
        ordersService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,pageOrderDto,"records");

        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtos = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);
            String itemNumber = item.getNumber();
            QueryWrapper<OrderDetail> queryWrapperOD = new QueryWrapper<>();
            queryWrapperOD.eq("order_id", itemNumber);
            List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapperOD);
            orderDto.setOrderDetails(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());

        pageOrderDto.setRecords(orderDtos);
        return R.success(pageOrderDto);
    }

    /**
     * 开始派送
     * @param order
     * @return
     */
    @PutMapping
    public R<String> status(@RequestBody Orders order){
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", order.getId());
        Orders orders = ordersService.getOne(queryWrapper);
        orders.setStatus(order.getStatus());
        ordersService.updateById(orders);
        return R.success("开始派送");
    }

    @PostMapping("again")
    public R<List<ShoppingCart>> again(@RequestBody Orders order){
        Long orderId = order.getId();
        Long userId = BaseContext.getCurrentId();
        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
        shoppingCartQueryWrapper.eq("user_id",userId);
        shoppingCartService.remove(shoppingCartQueryWrapper);
        QueryWrapper<OrderDetail> orderDetailQueryWrapper = new QueryWrapper<>();
        orderDetailQueryWrapper.eq("order_id",orderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailQueryWrapper);
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setDishId(item.getDishId());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setSetmealId(item.getSetmealId());
            shoppingCart.setImage(item.getImage());
            shoppingCart.setName(item.getName());
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartService.saveBatch(shoppingCartList);
        return R.success(shoppingCartList);
    }
}

