package com.zhw.utils;

/**
 * 获取一个随机验证码 3个英文 3个数字 打乱
 */
public class GetCodeUtil {
    public static String getCode(){
        //获取三个英文小写
        String eng = "qwertyuiopasdfghjklzxcvbnm";
        StringBuilder sb = new StringBuilder();
        char e[] = eng.toCharArray();
        for(int i=0;i<3;i++){
            sb.append(e[(int) (Math.random()*26)]);
            sb.append((int) (Math.random()*10));
        }
        return sb.toString();
    }
}
