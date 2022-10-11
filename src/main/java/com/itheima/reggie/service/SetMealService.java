package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时保存套餐和菜品的关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 根据id删除套餐,同时删除关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 修改套餐信息
     * @param setmealDto
     */
    public void updateWithDish(SetmealDto setmealDto);

    /**
     * 根据id获得setmeal信息和相关菜品
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDish(Long id);
}
