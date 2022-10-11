package com.Guo.GuoSend.controller;

import com.Guo.GuoSend.common.R;
import com.Guo.GuoSend.dto.DishDto;
import com.Guo.GuoSend.entity.Category;
import com.Guo.GuoSend.entity.Dish;
import com.Guo.GuoSend.service.CategoryService;
import com.Guo.GuoSend.service.DishFlavorService;
import com.Guo.GuoSend.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Resource
    private DishService dishService;

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);

        return R.success("保存成功");
    }

    /**
     * 分页查询菜品数据
     *
     * @param page     页码
     * @param pageSize 每页的数据
     * @param name     快速搜素
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dtoPage = new Page<>(page, pageSize);
        List<DishDto> list = new ArrayList<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name)
                .orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝，且忽略records属性，因为要单独处理
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        pageInfo.getRecords().forEach(e -> {
            DishDto dishDto = new DishDto();
            //再次拷贝
            BeanUtils.copyProperties(e, dishDto);

            //根据id查询分类对象
            String categoryName = categoryService.getById(e.getCategoryId()).getName();
            dishDto.setCategoryName(categoryName);
            list.add(dishDto);
        });

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }
}
