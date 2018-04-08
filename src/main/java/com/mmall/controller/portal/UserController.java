package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        //service->mybatis->dao
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccessful()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess(Const.LOGOUT_SUCCESS);
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user != null) {
            return ServerResponse.createBySuccess(user);
        } else {
            return ServerResponse.createByErrorMsg(Const.USER_NO_LOGIN);
        }
    }

    /**  
     * 获取用户密保问题
     * @author heylinlook 
     * @date 2018/4/5 11:11  
     * @param   
     * @return 如果有密保问题则返回，没有则返回提示
     */ 
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    /**  
     * 忘记密码后回答问题校验
     * @author heylinlook
     * @date 2018/4/5 11:09  
     * @param   
     * @return  如果问题与答案匹配，则返回token，用于密码重置；如果不匹配则返回错误信息
     */ 
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**  
     * 忘记密码时的重置密码
     * @author heylinlook 
     * @date 2018/4/5 11:52
     * @param   username
     * @param  passwordNew
     * @param token
     * @return
     */ 
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String token) {
        return iUserService.forgetResetPassword(username, passwordNew, token);
    }

    /**  
     * 登录后的重置密码
     * @author heylinlook
     * @date 2018/4/5 11:53  
     * @param   
     * @return   
     */ 
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>  resetPassword(HttpSession session, String passwordOld,String passwordNew) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMsg(Const.USER_NO_LOGIN);
        }
        return iUserService.resetPassword(user, passwordOld, passwordNew);
    }

    /**  
     * 更新用户信息
     * @author heylinlook 
     * @date 2018/4/5 14:15  
     * @param   
     * @return   
     */ 
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session, User user) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMsg(Const.USER_NO_LOGIN);
        }
        //防止越权
        user.setId(currentUser.getId());
        ServerResponse response = iUserService.updateInformation(user);
        if (response.isSuccessful()) {
            //更新成功，把更新后的User放入session
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }
    /**  
     * 获取用户详细信息
     * @author heylinlook 
     * @date 2018/4/8 14:38
     * @param   session
     * @return   返回的值对密码设空
     */ 
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), Const.USER_NO_LOGIN);
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
