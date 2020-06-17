package ants.mobile.ants_insight.Constants;

import java.util.ArrayList;
import java.util.List;

public class Event {
    //User searched for products
    public static final String PRODUCT_SEARCH = "product_search";

    //User viewed a product list or category
    public static final String PRODUCT_LIST_VIEW = "product_list_view";

    //User filtered a product list or category
    public static final String PRODUCT_LIST_FILTER = "product_list_filter";

    //User clicked on a product
    public static final String CLICK = "click";

    //User viewed a product details
    public static final String VIEW = "view";

    //User added a product to their shopping cart
    public static final String ADD_TO_CART = "add_to_cart";

    //User removed a product from their shopping cart
    public static final String REMOVE_CART = "remove_cart";

    //User viewed their shopping cart
    public static final String VIEW_CART = "view_cart";

    //User initiated the order process (a transaction is created)
    public static final String CHECKOUT = "checkout";

    //User added payment information
    public static final String PAYMENT = "payment";

    //User purchased and completed the order
    public static final String PURCHASE = "purchase";

    public static final String SCREEN_VIEW = "view";

    public static final String IDENTIFY = "identify";

    public static final String SIGN_OUT = "sign_out";

    public static final String SIGN_IN = "sign-in";

    public static final String IMPRESSION = "impression";
    public static final String VIEWABLE = "view";
    public static final String ADX_CLICK = "click";
    public static final String VIEW_COMPARE_LIST = "compare_list";
    public static final String VIEW_WISH_LIST = "view_wish_list";
    public static final String ADD_COMPARE = "add_compare";
    public static final String ADD_WISH_LIST = "add_wish_list";

    public static List<String> actionListHasCategoryProduct() {
        List<String> actionEvent = new ArrayList<>();
        actionEvent.add(CLICK);
        actionEvent.add(VIEW);
        actionEvent.add(ADD_TO_CART);
        actionEvent.add(REMOVE_CART);
        actionEvent.add(VIEW_CART);
        actionEvent.add(CHECKOUT);
        actionEvent.add(PAYMENT);
        actionEvent.add(PURCHASE);
        actionEvent.add(VIEW_COMPARE_LIST);
        actionEvent.add(VIEW_WISH_LIST);
        actionEvent.add(ADD_COMPARE);
        actionEvent.add(ADD_WISH_LIST);
        return actionEvent;
    }
}
