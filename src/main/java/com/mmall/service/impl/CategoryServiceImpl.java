package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName, Integer parentId) {
        //合法性检测
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMsg(Const.PARAM_WRONG);
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0) {
            return ServerResponse.createBySuccessMsg(Const.INSERT_SUCCESS);
        }
        return ServerResponse.createByErrorMsg(Const.INSERT_FAILED);
    }

    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        //合法性检测
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMsg(Const.PARAM_WRONG);
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0) {
            return ServerResponse.createBySuccessMsg(Const.UPDATE_SUCCESS);
        }
        return ServerResponse.createByErrorMsg(Const.UPDATE_FAILED);
    }

    /**  
     * 查询本节点id的下一级子节点
     * @author heylinlook 
     * @date 2018/4/9 15:44
     * @param   
     * @return   
     */ 
    public ServerResponse<List<Category>> getParallelChildrenCategory(Integer categoryId) {
        List<Category> list = categoryMapper.selectParallelChildrenCategoryByParentId(categoryId);
        if (CollectionUtils.isEmpty(list)) {
            logger.info(Const.CATEGORY_NOT_FIND);
        }

        return ServerResponse.createBySuccess(list);
    }

    /**  
     * 递归查询本节点及子节点的id（）
     * @author heylinlook 
     * @date 2018/4/9 15:40
     * @param   categoryId 父节点的id
     * @return  所有子节点、孙子节点
     */ 
    public ServerResponse<List<Integer>> getDeepChildrenCategory(Integer categoryId) {
        if (categoryId == null)
            return ServerResponse.createByErrorMsg(Const.PARAM_WRONG);
        Set<Category> categorySet = Sets.newHashSet();
        getDeepChildrenCategoryByRecursive(categorySet, categoryId);
        List<Integer> list = Lists.newArrayList();
        for (Category category : categorySet) {
            list.add(category.getId());
        }
        return ServerResponse.createBySuccess(list);
    }

    /**  
     * 递归查询
     * @author heylinlook 
     * @date 2018/4/9 15:45
     * @param categorySet 放置子节点的容器
     * @param categoryId 子节点id
     */ 
    private void getDeepChildrenCategoryByRecursive(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        //将当前ID对应的分类加入到集合
        if (category != null) {
            categorySet.add(category);
        }
        //递归查找是否有子分类
        List<Category> categoryList = categoryMapper.selectParallelChildrenCategoryByParentId(categoryId);
        for (Category categoryItem : categoryList) {
            //遍历每个子分类
            getDeepChildrenCategoryByRecursive(categorySet, categoryItem.getId());
        }
        //return categorySet;
    }
}
