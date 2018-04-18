package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

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

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if (productId == null)
            return ServerResponse.createByErrorCodeMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMsg(Const.PRODUCT_NOT_EXIST);
        }

        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize) {
        //使用mybatis的pageHelper
        //startPage---start
        //填充自己的sql查询逻辑
        //pageHelper---收尾
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectList();
        List<ProductListVo> productListVoList = new ArrayList<>();
        for (Product product : products) {
            productListVoList.add(assembleProductListVo(product));
        }
        PageInfo pageResult = new PageInfo(products);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        //使用mybatis的pageHelper
        //startPage---start
        //填充自己的sql查询逻辑
        //pageHelper---收尾
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)){
            //MYSQL中以 %作为通配符，表示0到任意多个任意字符  %productName% 这样就可以用like进行模糊搜索
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }

        List<Product> products = productMapper.selectListByProductNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList = new ArrayList<>();
        for (Product product : products) {
            productListVoList.add(assembleProductListVo(product));
        }
        PageInfo pageResult = new PageInfo(products);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }
















    /**  
     * 填充返回商品列表的VO
     * @author heylinlook 
     * @date 2018/4/18 22:00  
     * @param   
     * @return   
     */ 
    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        //imageHost  从配置文件中读取,配置和代码分离
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.imooc.com/"));

        return productListVo;
    }

    /**  
     * 填充返回商品详情的VO
     * @author heylinlook 
     * @date 2018/4/18 22:00
     * @param   
     * @return   
     */ 
    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();

        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //imageHost  从配置文件中读取,配置和代码分离
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.imooc.com/"));
        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            productDetailVo.setParentCategoryId(0);  //默认根节点
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }
}
