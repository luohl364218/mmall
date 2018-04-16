package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    UserMapper mUserMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int result = mUserMapper.checkUsername(username);
        if (result == 0) {
            return ServerResponse.createByErrorMsg(Const.USER_NO_EXIST);
        }
        //MD5密码登录
        String MD5Password = MD5Util.MD5EncodeUtf8(password);

        User user = mUserMapper.selectLogin(username, MD5Password);
        if (user == null) {
            return ServerResponse.createByErrorMsg(Const.PASSWORD_ERROR);
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(Const.LOGIN_SUCCESS, user);
    }

    public ServerResponse<String> register(User user) {
        //校验用户名是否存在
        ServerResponse valid = this.checkValid(user.getUsername(), Const.USERNAME_TYPE);
        if (!valid.isSuccessful()) {
            return valid;
        }
        //校验邮箱是否存在
        valid = this.checkValid(user.getEmail(), Const.EMAIL_TYPE);
        if (!valid.isSuccessful()) {
            return valid;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int result = mUserMapper.insert(user);
        if (result == 0) {
            return ServerResponse.createByErrorMsg(Const.REGISTER_ERR);
        }
        return ServerResponse.createBySuccessMessage(Const.REGISTER_SUCCESS);
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(str)) {
            if (Const.USERNAME_TYPE.equals(type)) {
                //校验用户名是否存在
                int result = mUserMapper.checkUsername(str);
                if (result > 0) {
                    return ServerResponse.createByErrorMsg(Const.USER_NAME_EXIST);
                }
            } else if (Const.EMAIL_TYPE.equals(type)) {
                //校验邮箱是否存在
                int result = mUserMapper.checkEmail(str);
                if (result > 0) {
                    return ServerResponse.createByErrorMsg(Const.EMAIL_EXIST);
                }
            }
        } else {
            return ServerResponse.createByErrorMsg(Const.CHECK_FAILED);
        }
        return ServerResponse.createBySuccessMessage(Const.CHECK_SUCCESS);
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse valid = this.checkValid(username, Const.USERNAME_TYPE);
        if (valid.isSuccessful()) {
            //用户名不存在
            return ServerResponse.createByErrorMsg(Const.USER_NO_EXIST);
        }
        String question = mUserMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMsg(Const.QUESTION_EMPTY);
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int result = mUserMapper.checkAnswer(username, question, answer);
        if (result > 0) {
            //说明用户答对了
            String forgotToken = UUID.randomUUID().toString();
            TokenCache.setKey(Const.TOKEN_KEY_PREFIX + username, forgotToken);
            return ServerResponse.createBySuccessMessage(forgotToken);
        }
        //用户没答对
        return ServerResponse.createByErrorMsg(Const.ANSWER_WRONG);
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String token) {
        //校验一下username
        ServerResponse valid = this.checkValid(username, Const.USERNAME_TYPE);
        if (valid.isSuccessful()) {
            //用户名不存在
            return ServerResponse.createByErrorMsg(Const.USER_NO_EXIST);
        }
        //判断传入token是否为空
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMsg(Const.TOKEN_EMPTY);
        }
        //获取缓存token
        String tokenCache = TokenCache.getKey(Const.TOKEN_KEY_PREFIX + username);
        //判断缓存值是否为空
        if (StringUtils.isBlank(tokenCache)) {
            return ServerResponse.createByErrorMsg(Const.TOKEN_EXPIRED);
        }
        //判断token是否匹配
        if (!StringUtils.equals(token, tokenCache)) {
            return ServerResponse.createByErrorMsg(Const.TOKEN_ERR);
        }
        //更新密码
        String passwordMD5 = MD5Util.MD5EncodeUtf8(passwordNew);
        int rowCount = mUserMapper.updatePasswordByUsername(username, passwordMD5);
        if (rowCount > 0) {
            //生效行数大于0即更新成功
            return ServerResponse.createBySuccessMessage(Const.PWD_RESET_SUCCESS);
        }
        return ServerResponse.createByErrorMsg(Const.PWD_RESET_FAILED);
    }

    public ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew) {
        //防止横向越权
        int result = mUserMapper.checkPassword(user.getId(), MD5Util.MD5EncodeUtf8(passwordOld));
        if (result == 0) {
            return ServerResponse.createByErrorMsg(Const.PASSWORD_ERROR);
        }
        //更新用户密码信息
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        result = mUserMapper.updateByPrimaryKeySelective(user);
        if (result > 0) {
            return ServerResponse.createBySuccessMessage(Const.PWD_RESET_SUCCESS);
        }
        return ServerResponse.createByErrorMsg(Const.PWD_RESET_FAILED);
    }

    public ServerResponse<User> updateInformation(User user) {
        //username不能更新
        //检查email是否已经被其他用户使用
        int result = mUserMapper.checkEmailByUserId(user.getId(), user.getEmail());
        if(result > 0) {
            return ServerResponse.createByErrorMsg(Const.EMAIL_EXIST);
        }

        User updateUser = new User();
        //禁止更新username
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());

        result = mUserMapper.updateByPrimaryKeySelective(updateUser);
        if(result > 0) {
            return ServerResponse.createBySuccess(Const.UPDATE_USER_SUCCESS, updateUser);
        }
        return ServerResponse.createByErrorMsg(Const.UPDATE_USER_FAILED);
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = mUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMsg(Const.USER_NO_EXIST);
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    /**  
     * 后端调用的方法，检验用户是否为管理员身份
     * @author heylinlook 
     * @date 2018/4/9 12:53
     * @param   
     * @return   
     */ 
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}