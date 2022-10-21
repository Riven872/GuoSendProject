package com.Guo.GuoSend.controller;

import com.Guo.GuoSend.common.R;
import com.Guo.GuoSend.dto.DishDto;
import com.Guo.GuoSend.dto.SetmealDto;
import com.Guo.GuoSend.entity.Setmeal;
import com.Guo.GuoSend.entity.SetmealDish;
import com.Guo.GuoSend.service.CategoryService;
import com.Guo.GuoSend.service.SetmealDishService;
import com.Guo.GuoSend.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController{
    private static final long serialVersionUID = 1L;

    @Resource
    private SetmealService setmealService;

    @Resource
    private SetmealDishService setmealDishService;

    @Resource
    private CategoryService categoryService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     *
     * @param pageIndex 页码索引
     * @param pageSize  页展示数量
     * @param name      快速搜索
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(@RequestParam("page") int pageIndex, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(pageIndex, pageSize);
        Page<SetmealDto> dtoPage = new Page<>(pageIndex, pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name)
                .orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo);

        //拷贝对象
        List<SetmealDto> list = new ArrayList<>();
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        pageInfo.getRecords().forEach(e -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(e, setmealDto);
            setmealDto.setCategoryName(categoryService.getById(e.getCategoryId()).getName());
            list.add(setmealDto);
        });
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 修改售卖状态（单个和批量）
     *
     * @param ids
     * @param arg
     * @return
     */
    @PostMapping("/status/{arg}")
    public R<String> updateStatus(@RequestParam List<Long> ids, @PathVariable Integer arg) {
        List<Setmeal> dishes = new ArrayList<>();
        ids.forEach(e -> {
            Setmeal dish = new Setmeal();
            dish.setId(e);
            dish.setStatus(arg);
            dishes.add(dish);
        });
        setmealService.updateBatchById(dishes);

        return R.success("修改状态成功！");
    }

    /**
     * 获取套餐及其菜品信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        return R.success(setmealService.getWithDish(id));
    }

    /**
     * 修改套餐及其菜品信息
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }

    /**
     * 显示套餐
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime);
        return R.success(setmealService.list(queryWrapper));
    }
}
