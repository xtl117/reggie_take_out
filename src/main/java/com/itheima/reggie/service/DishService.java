package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    //根据菜品id查询菜品信息和口味
    public DishDto getByIdWithFlavor(Long id);

    //修改菜品信息
    public void updateWithFlavor(DishDto dishDto);
}
