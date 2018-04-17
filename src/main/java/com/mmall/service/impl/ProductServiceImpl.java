package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse addOrUpdateProduct(Product product) {
        if (product == null)
            return ServerResponse.createByErrorMsg(Const.PARAM_NULL);
        //更新主图的路径
        if (StringUtils.isNotBlank(product.getSubImages())) {
            String[] images = product.getSubImages().split(",");
            if (images.length > 0)
                product.setMainImage(images[0]);
        }
        //判断更新还是添加
        if (product.getId() != null) {
            int rowCount = productMapper.updateByPrimaryKey(product);
            if (rowCount > 0)
                return ServerResponse.createBySuccessMsg(Const.UPDATE_SUCCESS);
            else
                return ServerResponse.createByErrorMsg(Const.UPDATE_FAILED);
        }else {
            int rowCount = productMapper.insert(product);
            if (rowCount > 0)
                return ServerResponse.createBySuccessMsg(Const.INSERT_SUCCESS);
            else
                return ServerResponse.createByErrorMsg(Const.INSERT_FAILED);
        }
    }

    public ServerResponse<String> setProductStatus(Integer productId, Integer status) {
        if (productId == null || status == null)
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0)
            return ServerResponse.createBySuccess(Const.UPDATE_SUCCESS);
        else
            return ServerResponse.createByErrorMsg(Const.UPDATE_FAILED);
    }
}
