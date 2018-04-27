package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * introductions:
 * created by Heylink on 2018/4/27 9:37
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    ShippingMapper shippingMapper;

    /**  
     * 添加地址
     * @author heylinlook 
     * @date 2018/4/27 9:57  
     * @param   
     * @return   
     */ 
    public ServerResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0) {
            Map result = new HashMap();
            result.put(Const.SHIPPING_ADD_RESULT_KET, shipping.getId());
            return ServerResponse.createBySuccess(Const.ADDRESS_ADD_SUCCESS, result);
        }
        return ServerResponse.createByErrorMsg(Const.ADDRESS_ADD_FAILED);
    }

    /**  
     * 删除地址
     * @author heylinlook
     * @date 2018/4/27 10:08  
     * @param   
     * @return   
     */ 
    public ServerResponse<String> del(Integer userId, Integer shippingId) {
        //横向越权漏洞 需要判断删除的是不是自己的地址
        int rowCount = shippingMapper.deleteByShippingIdUserId(userId, shippingId);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMsg(Const.ADDRESS_DEL_SUCCESS);
        }
        return ServerResponse.createByErrorMsg(Const.ADDRESS_DEL_FAILED);
    }

    /**  
     * 更新地址
     * @author heylinlook 
     * @date 2018/4/27 10:10  
     * @param   
     * @return   
     */ 
    public ServerResponse update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess(Const.UPDATE_SUCCESS);
        }
        return ServerResponse.createByErrorMsg(Const.UPDATE_FAILED);
    }

    /**  
     * 查询地址
     * @author heylinlook 
     * @date 2018/4/27 10:16  
     * @param   
     * @return   
     */ 
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
        //横向越权漏洞
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId, shippingId);
        if (shipping == null) {
            return ServerResponse.createByErrorMsg(Const.SELECT_FAILED);
        }
        return ServerResponse.createBySuccess(Const.SELECT_SUCCESS, shipping);
    }

    /**  
     * 地址列表
     * @author heylinlook
     * @date 2018/4/27 10:42  
     * @param   
     * @return   
     */ 
    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }

}
