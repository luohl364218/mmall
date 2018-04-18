package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 该类用于产品管理
 * @author heylinlook 
 * @date 2018/4/16 21:35
 */

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse addProduct(HttpSession session, Product product){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), Const.USER_NO_LOGIN);
        }
        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccessful()) {
            //是管理员
            //执行添加操作
            return iProductService.addOrUpdateProduct(product);
        }else {
            return ServerResponse.createByErrorMsg(Const.USER_NOT_ADMIN);
        }
    }

    /**  
     * 产品上下架 更改产品状态
     * @author heylinlook 
     * @date 2018/4/18 20:44
     * @param   productId 商品ID
     * @param status 商品状态 1--在售  2--下架  3--删除
     * @return   
     */ 
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), Const.USER_NO_LOGIN);
        }
        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccessful()) {
            //是管理员
            //执行操作
            return iProductService.setProductStatus(productId, status);
        }else {
            return ServerResponse.createByErrorMsg(Const.USER_NOT_ADMIN);
        }
    }

    /**  
     * 获取商品详情
     * @author heylinlook 
     * @date 2018/4/18 20:43
     * @param   
     * @return   
     */ 
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), Const.USER_NO_LOGIN);
        }
        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccessful()) {
            //是管理员
            //执行操作
            return iProductService.manageProductDetail(productId);
        }else {
            return ServerResponse.createByErrorMsg(Const.USER_NOT_ADMIN);
        }
    }

    /**  
     * 获取商品列表
     * @author heylinlook 
     * @date 2018/4/18 21:48
     * @param  pageNum 分页数
     * @param  pageSize 一页中的商品数
     * @return   
     */ 
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), Const.USER_NO_LOGIN);
        }
        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccessful()) {
            //是管理员
            //执行操作
            return iProductService.getProductList(pageNum, pageSize);
        }else {
            return ServerResponse.createByErrorMsg(Const.USER_NOT_ADMIN);
        }
    }

    /**  
     * 商品搜索
     * @author heylinlook 
     * @date 2018/4/18 22:26
     * @param  productName 对商品名进行模糊搜索
     * @param  productId 商品ID
     * @return   
     */ 
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), Const.USER_NO_LOGIN);
        }
        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccessful()) {
            //是管理员
            //执行操作
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        }else {
            return ServerResponse.createByErrorMsg(Const.USER_NOT_ADMIN);
        }
    }

}
