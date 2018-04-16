package com.mmall.controller.backend;


import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Scanner;

@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), Const.USER_NO_LOGIN);
        }
        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccessful()) {
            //是管理员
            //执行添加操作
            return iCategoryService.addCategory(categoryName, parentId);
        }else {
           return ServerResponse.createByErrorMsg(Const.USER_NOT_ADMIN);
        }
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse updateCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), Const.USER_NO_LOGIN);
        }
        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccessful()) {
            //是管理员
            //执行更新操作
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        }else {
            return ServerResponse.createByErrorMsg(Const.USER_NOT_ADMIN);
        }
    }

    /**  
     * 获取子节点（平级，不递归）
     * @author heylinlook 
     * @date 2018/4/9 14:32
     * @param   
     * @return   
     */ 
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getParallelChildrenCategory(HttpSession session,@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), Const.USER_NO_LOGIN);
        }
        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccessful()) {
            //是管理员
            //执行查询操作
            return iCategoryService.getParallelChildrenCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMsg(Const.USER_NOT_ADMIN);
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), Const.USER_NO_LOGIN);
        }
        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccessful()) {
            //是管理员
            //执行查询操作
            return iCategoryService.getDeepChildrenCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMsg(Const.USER_NOT_ADMIN);
        }
    }

}
