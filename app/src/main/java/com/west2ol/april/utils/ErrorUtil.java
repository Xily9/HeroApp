package com.west2ol.april.utils;

public class ErrorUtil {
    public static void error(int code) throws RuntimeException {
        switch (code) {
            case 1:
                throw new RuntimeException("抱歉,活动时间已过!");
            case 2:
                throw new RuntimeException("错误,用户信息验证失败!!");
            case 3:
                throw new RuntimeException("超时");
            case 4:
                throw new RuntimeException("错误!你没有抽奖权限!");
            case 5:
                throw new RuntimeException("错误!没有奖品!");
            case 6:
                throw new RuntimeException("答案数据错误,请重新答题!");
            case 7:
                throw new RuntimeException("错误,该用户名已被注册!");
            default:
                throw new RuntimeException("未知错误,错误码:" + code);
        }
    }
}
