package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

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
    public static final String UPDATE_USER_SUCCESS = "更新用户信息成功";
    public static final String UPDATE_USER_FAILED = "更新用户信息失败";
    public static final String USER_NOT_ADMIN = "不是管理员，需要管理员权限";
    public static final String PARAM_WRONG = "参数错误";
    public static final String PARAM_NULL = "参数为空";
    public static final String INSERT_SUCCESS = "添加成功";
    public static final String INSERT_FAILED = "添加失败";
    public static final String UPDATE_SUCCESS = "更新成功";
    public static final String UPDATE_FAILED = "更新失败";
    public static final String CATEGORY_NOT_FIND = "未找到当前分类的子分类";
    public static final String PRODUCT_NOT_EXIST = "产品已经删除或下架";
    public static final String UPLOAD_FAILED = "上传文件失败";
    public static final String UPLOAD_SUCCESS = "上传文件成功";
    public static final String ORDER_NOT_EXIST = "用户没有该订单";
    public static final String ORDER_ALREADY_PAY = "订单已支付，无法取消";
    public static final String VERIFY_FAILED = "验证失败";
    public static final String UNDER_STOCK_PRE = "产品";
    public static final String UNDER_STOCK_SUF = "库存不足";
    public static final String GENERATE_ORDER_ERR = "生成订单错误";
    public static final String CART_EMPTY = "购物车为空";
    public static final String ORDER_EMPTY = "订单为空";

    public static final String USERNAME_TYPE = "username";
    public static final String EMAIL_TYPE = "email";

    public static final String TOKEN_KEY_PREFIX = "token_";

    public static final String ADDRESS_ADD_SUCCESS = "新建地址成功";
    public static final String ADDRESS_ADD_FAILED = "新建地址失败";
    public static final String ADDRESS_DEL_SUCCESS = "删除地址成功";
    public static final String ADDRESS_DEL_FAILED = "删除地址失败";
    public static final String SELECT_FAILED = "查询结果为空";
    public static final String SELECT_SUCCESS = "查询成功";
    public static final String SHIPPING_ADD_RESULT_KET = "shippingId";

    public interface Role{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1;    //管理员
    }

    public interface CartStatus {
        int CHECKED = 1;//即购物车选中状态
        int UN_CHECKED = 0;//购物车中未选中状态

        String LIMIT_NUM_FAILED = "LIMIT_NUM_FAILED";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public enum ProductStatusEnum {
        ON_SALE(1, "在售");

        int code;
        String status;
        ProductStatusEnum(int code, String status) {
            this.code = code;
            this.status = status;
        }
        public int getCode() {
            return code;
        }
        public String getStatus() {
            return status;
        }

        public static ProductStatusEnum codeOf(int code) {
            for (ProductStatusEnum typeEnum : values()) {
                if (typeEnum.code == code) {
                    return typeEnum;
                }
            }
            throw  new RuntimeException("未找到对应类型");
        }
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }

    public enum OrderStatusEnum{
        CANCELED(0, "已取消"),
        NO_PAY(10, "未支付"),
        PAID(20, "已付款"),
        SHIPPED(40, "已发货"),
        ORDER_SUCCESS(50, "订单完成"),
        ORDER_CLOSED(60, "订单关闭");

        private String value;
        private int code;

        OrderStatusEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum typeEnum : values()) {
                if (typeEnum.code == code) {
                    return typeEnum;
                }
            }
            throw  new RuntimeException("未找到对应类型");
        }
    }

    public interface AlipayCallback {
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAIED = "failed";
    }

    public enum PayPlatformEnum{
        ALIPAY(1, "支付宝");

        private String value;
        private int code;
        PayPlatformEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum {
        ONLINE_PAY(0, "在线支付");

        private String value;
        private int code;
        PaymentTypeEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum typeEnum : values()) {
                if (typeEnum.code == code) {
                    return typeEnum;
                }
            }
            throw  new RuntimeException("未找到对应类型");
        }
    }
}
