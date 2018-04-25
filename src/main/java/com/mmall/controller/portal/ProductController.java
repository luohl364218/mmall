package com.mmall.controller.portal;

/*
 * introductions:
 * created by Heylink on 2018/4/25 16:28
 */

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    IProductService iProductService;

    /**  
     * 前端获取产品详情
     * @author heylinlook 
     * @date 2018/4/25 17:03
     * @param   
     * @return   
     */ 
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId) {
        return iProductService.getProductDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword", required = false) String keyword,
                                          @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                          @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        return iProductService.getProductsByKeyWordAndCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }


}
