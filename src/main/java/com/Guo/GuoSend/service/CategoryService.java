package com.Guo.GuoSend.service;

import com.Guo.GuoSend.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CategoryService extends IService<Category> {
    /**
     * 分类删除自定义方法
     * @param id
     */
    public void remove(Long id);
}
