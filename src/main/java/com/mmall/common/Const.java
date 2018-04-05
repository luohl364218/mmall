package com.mmall.common;

public class Const {

    public static final String CURRENT_USER = "currentUser";
    public static final String USER_NO_EXIST = "用户名不存在";
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String LOGIN_SUCCESS = "登录成功";
    public static final String USER_NAME_EXIST = "用户名已存在";
    public static final String EMAIL_EXIST = "邮箱已存在";
    public static final String LOGOUT_SUCCESS = "退出登录";
    public static final String REGISTER_ERR = "注册失败";
    public static final String REGISTER_SUCCESS = "注册成功";
    public static final String CHECK_SUCCESS = "校验成功";
    public static final String CHECK_FAILED = "校验失败";
    public static final String USER_NO_LOGIN = "用户未登录，无法获取用户信息";
    public static final String QUESTION_EMPTY = "找回问题的密码是空的";
    public static final String ANSWER_WRONG = "问题的答案错误";
    public static final String TOKEN_EMPTY = "token为空，请传入参数";
    public static final String TOKEN_EXPIRED = "token过期，请重新获取";
    public static final String TOKEN_ERR = "token错误，请重新获取";
    public static final String PWD_RESET_SUCCESS = "密码修改成功";
    public static final String PWD_RESET_FAILED = "密码修改失败";

    public static final String USERNAME_TYPE = "username";
    public static final String EMAIL_TYPE = "email";

    public static final String TOKEN_KEY_PREFIX = "token_";

    public interface Role{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1;    //管理员
    }
}