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

}
