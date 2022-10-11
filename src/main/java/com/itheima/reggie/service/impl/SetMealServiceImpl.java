package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetMealMapper;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("all")
@Service
@Slf4j
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
    @Autowired
    private SetMealDishService setMealDishService;

    /**
     * 新增套餐，同时保存套餐和菜品的关系
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setMealDishService.saveBatch(setmealDishes);
    }

    /**
     * 根据id删除套餐，同时删除关联数据
     *
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        //查看status，查询状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        //sql语句
        //select count(*) from setmeal where id in(id1,id2,id3) and status = 1
        queryWrapper.in(Setmeal::getId, ids).eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        if (count > 0) {
            //如果不能删除，抛出异常
            throw new CustomException("套餐正在售卖，不能删除");
        }

        //如果可以删除，先删除套餐中setmeal的数据
        this.removeByIds(ids);

        //再删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);

        setMealDishService.remove(queryWrapper1);
    }

    /**
     * 修改套餐信息
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //先修改setmeal表
        this.updateById(setmealDto);

        //删除setMealDish表中信息,因为不是主键
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setMealDishService.remove(queryWrapper);

        //获取dish列表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //赋值setmealId
        setmealDishes= setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setMealDishService.saveBatch(setmealDishes);
    }

    /**
     * 根据id获得setmeal信息和相关菜品
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        //拷贝到setmealDto
        BeanUtils.copyProperties(setmeal,setmealDto);
        //查询相关菜品，
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //查询条件
        queryWrapper.eq(SetmealDish::getSetmealId,id).orderByDesc(SetmealDish::getSort);
        //查询
        List<SetmealDish> list = setMealDishService.list(queryWrapper);

        //添加到setmealDto
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }


}
