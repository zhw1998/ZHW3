package com.zhw.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class  TypeUtil {


    public static List<String> gettypeList(String type){
        String[] strs = null;
        switch (type){
            case "pic":
                strs = new String[]{".jpeg", "j.pg", ".png", ".gif"};
                break;
            case "doc":
                strs = new String[]{".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx"};
                break;
            case "mv":
                strs = new String[]{".mp4", ".wmv"};
                break;
            case "music":
                strs = new String[]{".mp3"};
                break;
            default:
                strs = new String[]{};
                break;
        }
        return  Arrays.asList(strs);
    }
}
