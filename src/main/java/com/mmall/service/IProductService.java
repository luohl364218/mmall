package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {

    ServerResponse addOrUpdateProduct(Product product);

    ServerResponse<String> setProductStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse manageProductList(Integer pageNum, Integer pageSize);
}
