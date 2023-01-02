package com.itheima.reggie.service;

import com.itheima.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author myz
 * @since 2022-12-30
 */
public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);
}
