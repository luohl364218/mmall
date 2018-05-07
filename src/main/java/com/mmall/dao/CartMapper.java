package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> selectByUserId(Integer userId);

    /**  
     * 查找用户购物车中选中状态的物品数量
     * @author heylinlook 
     * @date 2018/4/26 22:08
     * @param   
     * @return   
     */ 
    int selectCartProductCheckedStatusByUserId(Integer userId);

    /**  
     * 删除用户的购物车商品信息
     * @author heylinlook 
     * @date 2018/4/26 22:07
     * @param   
     * @return   
     */ 
    int deleteByUserIdProductIds(@Param("userId") Integer userId,@Param("productIdList") List<String> productIdList);

    int checkedOrUnCheckedProduct(@Param("userId") Integer userId, @Param("checked") Integer checked, @Param("productId") Integer productId);

    /**  
     * 查找用户购物车中的商品件数
     * @author heylinlook 
     * @date 2018/4/26 22:24  
     * @param   
     * @return   
     */ 
    int selectCartProductCount(@Param("userId")Integer userId);

    /**  
     * 返回购物车中所有勾选中的商品信息
     * @author heylinlook
     * @date 2018/5/7 10:42  
     * @param   
     * @return   
     */ 
    List<Cart> selectCheckedCartByUserId(Integer userId);
}