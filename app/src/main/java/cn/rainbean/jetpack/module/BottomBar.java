package cn.rainbean.jetpack.module;

import java.util.List;

public class BottomBar {


    public String activeColor;
    public String inActiveColor;
    public List<Tab> tabs;
    public int selectTab;

    public static class Tab {
        /**
         * size : 24
         * enable : true
         * index : 0
         * pageUrl : main/tabs/home
         * title : 首页
         * tintColor : #ff678f
         */

        public int size;
        public boolean enable;
        public int index;
        public String pageUrl;
        public String title;
        public String tintColor;
    }

}
