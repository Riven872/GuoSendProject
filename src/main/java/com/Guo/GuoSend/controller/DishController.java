package com.Guo.GuoSend.controller;

import com.Guo.GuoSend.common.R;
import com.Guo.GuoSend.dto.DishDto;
import com.Guo.GuoSend.entity.Category;
import com.Guo.GuoSend.entity.Dish;
import com.Guo.GuoSend.entity.DishFlavor;
import com.Guo.GuoSend.service.CategoryService;
import com.Guo.GuoSend.service.DishFlavorService;
import com.Guo.GuoSend.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Resource
    private DishService dishService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);

        //region 清理所有菜品的缓存数据
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(Set);
        //endregion

        //精确清理某个更新分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

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

    /**
     * 根据id查询菜品信息和对应口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);

        //region 清理所有菜品的缓存数据
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(Set);
        //endregion

        //精确清理某个更新分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("修改成功");
    }

    /**
     * 启售、停售菜品（单个或批量）
     *
     * @param ids 菜品id
     * @param arg 将要改变的状态
     * @return
     */
    @PostMapping("/status/{arg}")
    public R<String> updateStatus(@RequestParam("ids") List<Long> ids, @PathVariable Integer arg) {
        List<Dish> list = new ArrayList<>();
        ids.forEach(e -> {
            Dish dish = new Dish();
            dish.setId(e);
            dish.setStatus(arg);
            list.add(dish);
        });
        dishService.updateBatchById(list);

        return R.success("状态修改成功");
    }

    /**
     * 根据id删除菜品（单个或批量）
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        dishService.removeByIds(ids);
        return R.success("删除成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     *
     * @param dish
     * @param name 快速搜素
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish, String name) {
        List<DishDto> dtoList;

        //动态设置key值为分类id和分类状态
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //先从Redis中获取缓存数据
        dtoList = redisTemplate.opsForValue().get(key) == null ? new ArrayList<>() : (List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果存在，则直接返回
        if (dtoList != null && dtoList.size() != 0) {
            return R.success(dtoList);
        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(Dish::getStatus, 1)
                .like(name != null, Dish::getName, name)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        list.forEach(e -> {
            //region 查询每个菜品对应的口味集合
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(e, dto);
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, e.getId());
            dto.setFlavors(dishFlavorService.list(wrapper));
            dtoList.add(dto);
            //endregion
        });

        //如果Redis中不存在，则将查询到的数据放入，过期时间为10分钟
        redisTemplate.opsForValue().set(key, dtoList, 10, TimeUnit.MINUTES);
        return R.success(dtoList);
    }
}
