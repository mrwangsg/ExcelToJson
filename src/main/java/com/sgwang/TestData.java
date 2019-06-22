package com.sgwang;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @创建人 sgwang
 * @name TestData
 * @user shiguang.wang
 * @创建时间 2019/6/22
 * @描述
 */
public class TestData {

    public static Map<String, String> getTestMapData() {
        Map<String, String> initMap = new ConcurrentHashMap<String, String>() {
            {
                put("userName", "wang");
                put("passWord", "123");
                put("innerDemo.innerName", "shi");
                put("innerDemo.innerPass", "456");
                put("outDemo.outName", "guang");
                put("outDemo.outPass", "789");
                put("listDemo[0].list.name", "wangList01");
                put("listDemo[0].list.pass", "test");
                put("listDemo[1].list.name", "wangList02");
                put("listDemo[1].list.pass", "test");
                put("strList[0]", "test01");
                put("strList[1]", "test02");
                put("listDemo[0].list.node[0].test.name", "listandlistName");
                put("listDemo[0].list.node[0].test.pass", "listandlistPass");
            }
        };

        return initMap;
    }

    public static String[] getTestArrayData() {
        String[] initStr = new String[]{
                "userName", "passWord",
                "innerDemo.innerName", "innerDemo.innerPass",
                "outDemo.outName", "outDemo.outPass",
                "listDemo[0].list.name", "listDemo[0].list.pass",
                "listDemo[1].list.name", "listDemo[1].list.pass",
                "strList[0]", "strList[1]"
        };

        return initStr;
    }
}
