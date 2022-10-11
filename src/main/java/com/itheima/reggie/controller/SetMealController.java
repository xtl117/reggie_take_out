package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author XTL117
 * @version 1.
 */

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;
    @Autowired
    private SetMealDishService setMealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setMealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        //查询条件
        queryWrapper.like(name != null, Setmeal::getName, name);
        //排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setMealService.page(setmealPage, queryWrapper);
        //拷贝
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);

            }
            return setmealDto;
        }).collect(Collectors.toList());


        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        setMealService.removeWithDish(ids);
        return R.success("删除成功");
    }
    @PostMapping("/status/0")
    public R<String> statusdown(@RequestParam List<Long> ids){
        //改变售卖状态
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId,ids).set(Setmeal::getStatus,0);
        setMealService.update(updateWrapper);

        return R.success("停售成功");
    }

    @PostMapping("/status/1")
    public R<String> statusup(@RequestParam List<Long> ids){
        //改变售卖状态
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId,ids).set(Setmeal::getStatus,1);
        setMealService.update(updateWrapper);

        return R.success("停售成功");
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setMealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    /**
     * 修改套餐时，根据id回显到修改菜单
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> showBack(@PathVariable("id") Long id ){

        return R.success(setMealService.getByIdWithDish(id));
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setMealService.list(queryWrapper);

        return R.success(list);
    }
}
