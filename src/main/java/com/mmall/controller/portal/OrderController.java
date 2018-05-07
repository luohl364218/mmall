package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * introductions:订单接口
 * created by Heylink on 2018/5/4 21:12
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    /**  
     * 创建订单的函数
     * @author heylinlook 
     * @date 2018/5/7 17:34  
     * @param   
     * @return   
     */ 
    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(), shippingId);
    }

    /**  
     * 订单调用支付宝支付方法，生成支付二维码
     * @author heylinlook 
     * @date 2018/5/5 16:37  
     * @param   
     * @return   
     */ 
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo, user.getId(), path);
    }

    /**  
     * 支付宝回调函数
     * @author heylinlook
     * @date 2018/5/5 23:11  
     * @param   
     * @return   
     */ 
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map requestParam = request.getParameterMap();
        for (Iterator iterator = requestParam.keySet().iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            String[] value = (String[]) requestParam.get(name);
            String valueStr = "";
            for (int i = 0; i < value.length; i++) {
                valueStr = (i == value.length - 1) ? valueStr + value[i] : valueStr + value[i] + ",";
            }
            params.put(name, valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"),params.toString());

        /******************异步返回结果的验签*******************/
        //step1:在通知返回参数列表中，除去sign、sign_type两个参数外，凡是通知返回回来的参数皆是待验签的参数。
        params.remove("sign_type");  //源码中已经删除sign参数    AlipaySignature——>rsaCheckV2——>getSignCheckContentV2
        try {
            boolean alipayRSACheckV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckV2) {
                return ServerResponse.createByErrorMsg(Const.VERIFY_FAILED);
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常", e);
        }
        //step2：todo 商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
        // 并判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
        // 同时需要校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（
        // 有的时候，一个商户可能有多个seller_id/seller_email），上述有任何一个验证不通过，
        // 则表明本次通知是异常通知，务必忽略。

        //业务代码
        ServerResponse response = iOrderService.aliCallback(params);
        if (response.isSuccessful()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAIED;
    }

    /**  
     * 供前端轮询订单状态
     * @author heylinlook
     * @date 2018/5/5 23:26  
     * @param   
     * @return   
     */ 
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.queryOrderPayStatus(user.getId(), orderNo).isSuccessful() ?
                ServerResponse.createBySuccess(true) : ServerResponse.createBySuccess(false);
    }

}
