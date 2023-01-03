package com.itheima.reggie.mapper;

import com.itheima.reggie.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author myz
 * @since 2022-12-30
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}
