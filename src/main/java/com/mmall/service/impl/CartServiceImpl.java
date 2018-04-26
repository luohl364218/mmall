package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*
 * introductions:处理购物车逻辑的核心类
 * created by Heylink on 2018/4/26 14:14
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;

    /**  
     * 添加商品到购物车
     * @author heylinlook 
     * @date 2018/4/26 16:29  
     * @param   
     * @return   
     */ 
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        //参数判断
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
        if (cart == null) {
            //数据库中不存在当前购物车信息
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setChecked(Const.CartStatus.CHECKED);
            cart.setQuantity(count);
            cartMapper.insert(cart);
        } else {
            //这个产品已经在购物车里了
            //数量相加
            count += cart.getQuantity();
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    /**  
     * 更新购物车中某个商品的下单数量
     * @author heylinlook 
     * @date 2018/4/26 19:57  
     * @param   
     * @return   
     */ 
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        //参数判断
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
        //更新商品数量
        if (cart != null) {
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    /**  
     * 从购物车数据库中删除某个条目
     * @author heylinlook 
     * @date 2018/4/26 19:57
     * @param
     * @return
     */ 
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (productList.isEmpty()) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        cartMapper.deleteByUserIdProductIds(userId, productList);
        return this.list(userId);
    }

    /**  
     * 查询某用户的购物车信息
     * @author heylinlook
     * @date 2018/4/26 19:57  
     * @param   
     * @return   
     */ 
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**  
     * 全选/全不选 单选 单独反选功能块
     * @author heylinlook 
     * @date 2018/4/26 20:11
     * @param   
     * @return   
     */ 
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer checked, Integer productId) {
        cartMapper.checkedOrUnCheckedProduct(userId, checked, productId);
        return this.list(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    /**  
     * 从数据库中获得用户的购物车的视图对象
     * @author heylinlook
     * @date 2018/4/26 16:30  
     * @param   
     * @return   
     */ 
    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        List<CartProductVo> cartProductVoList = new ArrayList<>();

        BigDecimal totalPrice = new BigDecimal("0");
        //将获得的Cart填充到CartProductVo
        if (!cartList.isEmpty()) {
            for (Cart cart : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cart.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartProductVo.getProductId());
                if (product != null) {
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    //判断库存数量
                    int buyLimitCount = 0;
                    if (product.getStock() >= cart.getQuantity()) {
                        //产品库存大于等于购物车中的数量
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(Const.CartStatus.LIMIT_NUM_SUCCESS);
                    } else {
                        cartProductVo.setLimitQuantity(Const.CartStatus.LIMIT_NUM_FAILED);
                        buyLimitCount = product.getStock();
                        //更新购物车中的有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cart.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cart);
                    }
                    //继续完善其他信息
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(cartProductVo.getQuantity(), product.getPrice().doubleValue()));
                    //商品是否选中
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                //如果已经勾选，则将当前产品价格添加到总价中
                if (cartProductVo.getProductChecked() == Const.CartStatus.CHECKED) {
                    totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }

        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setTotalPrice(totalPrice);
        cartVo.setAllChecked(getCartAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    /**  
     * 判断某用户的购物车中的商品是否全都选中
     * @author heylinlook 
     * @date 2018/4/26 19:58  
     * @param   
     * @return   
     */ 
    private boolean getCartAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }


}
