package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

public interface IProductService {

    ServerResponse addOrUpdateProduct(Product product);

    ServerResponse<String> setProductStatus(Integer productId, Integer status);
}
