package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 后台管理员登录的控制器
 * @author heylinlook 
 * @date 2018/4/8 14:39
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    IUserService iUserService;

    /**  
     * 管理员登录方法
     * @author heylinlook 
     * @date 2018/4/8 15:01
     * @param   
     * @return   
     */ 
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccessful()){
            User user = response.getData();
            //判断是否是管理员
            if(user.getRole() == Const.Role.ROLE_ADMIN) {
                //更新到session
                session.setAttribute(Const.CURRENT_USER, user);
                return response;
            }else{
                return ServerResponse.createByErrorMsg(Const.USER_NOT_ADMIN);
            }
        }
        return response;
    }

}
