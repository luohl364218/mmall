package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/*
 * introductions:
 * created by Heylink on 2018/5/8 10:47
 */
@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    /**  
     * 查看订单列表
     * @author heylinlook 
     * @date 2018/5/8 11:20  
     * @param   
     * @return   
     */ 
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session,
                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), Const.USER_NO_LOGIN);
        }
        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccessful()) {
            //是管理员
            //执行操作
            return iOrderService.manageList(pageNum, pageSize);
        }else {
            return ServerResponse.createByErrorMsg(Const.USER_NOT_ADMIN);
        }
    }

    /**  
     * 获取订单详情
     * @author heylinlook 
     * @date 2018/5/8 11:30  
     * @param   
     * @return   
     */ 
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), Const.USER_NO_LOGIN);
        }
        //检验一下是否是管理员
        if (iUserService.checkAdminRole(user).isSuccessful()) {
            //是管理员
            //执行操作
            return iOrderService.manageDetail(orderNo);
        }else {
            return ServerResponse.createByErrorMsg(Const.USER_NOT_ADMIN);
        }
    }
}
