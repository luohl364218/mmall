package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

/*
 * introductions:
 * created by Heylink on 2018/5/4 21:15
 */
public interface IOrderService {

    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse aliCallback(Map<String, String> params);
}
