package com.feiyang.complexlistlayout.util;


import com.feiyang.complexlistlayout.entity.TestEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * package: com.easyandroid.sectionadapter.util.DatasUtil
 * author: gyc
 * description:
 * time: create at 2017/7/8 9:58
 * <p>
 * 产生随机的数据
 */

public class DatasUtil {

    static String url1 = "http://g.hiphotos.baidu" +
            ".com/image/pic/item/4b90f603738da977c76ab6fab451f8198718e39e.jpg";
    static String url2 = "http://www.zjito" +
            ".com/upload/resources/image/2015/11/21/8577adeb-c075-409d-b910-9d29137f8b84_720x1500" +
            ".jpg?1483574072000";
    static String url3 = "http://www.zjito" +
            ".com/upload/resources/image/2015/11/21/0d362aed-edad-42a2-9fcf-d7e67fcc2429_720x1500" +
            ".jpg?1483574081000";
    static String url4 = "http://www.zjito" +
            ".com/upload/resources/image/2015/11/21/e542381b-8b4f-41d4-a917-3e276bfb1184_720x1500" +
            ".jpg?1483574093000";
    static String url5 = "http://www.zjito" +
            ".com/upload/resources/image/2016/8/29/26d65697-9b48-4cb7-aa0c-4d54eb2dacd1_720x1500" +
            ".jpg?1483478514000";
    static String url6 = "http://www.zjito" +
            ".com/upload/resources/image/2016/3/19/dbd4382c-3ddd-46fd-8a0c-a70dd8d8888f_720x1500" +
            ".jpg?1483506463000";


    public static List<TestEntity.BodyBean.EListBean> createDatas() {
        List<TestEntity.BodyBean.EListBean> mDatas = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            TestEntity.BodyBean.EListBean bean = new TestEntity.BodyBean.EListBean();
            List<String> urls = new ArrayList<>();
            bean.setPicture(url1);
            bean.setContent("炎热的夏日，深深的森林里，据说，有妖怪");
            bean.setTime(new Date(System.currentTimeMillis()).toLocaleString());
            Random random = new Random();

            bean.setBrowser(random.nextInt(200) + "");
            bean.setUserName("WD");
            urls.add(getRandomUrl());
            urls.add(getRandomUrl());
            urls.add(getRandomUrl());
            urls.add(getRandomUrl());
            urls.add(getRandomUrl());
            urls.add(getRandomUrl());
            bean.setEPicture(urls);
            mDatas.add(bean);
        }
        return mDatas;
    }

    private static String getRandomUrl() {
        Random random = new Random();
        int nextInt = random.nextInt(6);
        switch (nextInt) {
            case 1:
                return url1;
            case 2:
                return url2;
            case 3:
                return url3;
            case 4:
                return url4;
            case 5:
                return url5;
            case 6:
                return url6;
        }
        return url5;
    }

}
