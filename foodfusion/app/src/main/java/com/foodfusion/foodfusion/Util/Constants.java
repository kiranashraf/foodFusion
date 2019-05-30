package com.foodfusion.foodfusion.Util;

/**
 * Created by Rameez on 2/11/2018.
 */

public class Constants {


    public static String cacheDir="Android/data/foodfusion";

    public enum radio{ALL("all"), RECENT("recent"),FAV("fav"),VIEW("view");
        private final String sst;
        radio(String sst) {this.sst=sst;
        }
        public String getValue() {return sst;}
//        public static String name(String value){
//            drawer[] values = drawer.values();
//            int enumValue = 0;
//            for(drawer eachValue : values) {
//                enumValue =eachValue.id;
//
//                if (enumValue==value) {
//                    return eachValue.name();
//                }
//            }
//            return null;
//        }
    };

    public enum drawer {LOGIN(-10),LOGOUT(-9),SIGNUP(-8),FAVOURITES(-7),LOGGEDIN(-6),ALLCATEGORIES(-5);
        private final int id;
        drawer(int id) {this.id = id;}
        public int getValue() {return id;}
        public static String name(int value){
            drawer[] values = drawer.values();
            int enumValue = 0;
            for(drawer eachValue : values) {
                enumValue =eachValue.id;

                if (enumValue==value) {
                    return eachValue.name();
                }
            }
            return null;
        }}
        public static final int SEARCH_QUERY_THRESHOLD=2;
    public static final String MySharedPreference = "FOODFUSION_SHARED_PREFERENCE";
//    public static final String SP_LANGUAGE = "language";
    public static final String SP_USER = "userInfo";
//    public static final String SP_BARBER = "barber";
//    public static final String SP_INTIALIMAGE = "intialimage";
//    public static final String SP_REMEMBERME = "rememberme";
//    public static final String SP_BARBERREMEMBERME = "barberrememberme";
    public static final String SP_TEMP_USERID = "tempuserid";
    public static final String SP_ISLOGGEDIN = "userStatus";
    public static final String SP_ISBOARDING = "boarding";
    public static final String SP_ISSOCIALLOGGEDIN = "socialUserStatus" ;
    public static final String SP_USERPIC = "userPic" ;
    public static final String SP_CURRENT_RADIO = "currentRadio" ;







    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String SP_CATEGORY = "categoryList";


}
