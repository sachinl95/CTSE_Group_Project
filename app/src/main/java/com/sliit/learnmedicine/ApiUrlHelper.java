package com.sliit.learnmedicine;

public class ApiUrlHelper {
    private static final ApiUrlHelper ourInstance = new ApiUrlHelper();

    private static final String URL = "https://young-temple-33785.herokuapp.com/medicines";

    public static final String GET_ALL_URL = URL.concat("/get-all");
    public static final String GET_ONE_URL = URL.concat("/get-one/");
    public static final String GET_FAVORITES_URL = URL.concat("/get-favourites");
    public static final String UPDATE_FAVORITES_URL = URL.concat("/updateFavourite");

    public static ApiUrlHelper getInstance() {
        return ourInstance;
    }

    private ApiUrlHelper() {
    }


}