package ants.mobile.ants_insight.Constants;

import java.util.ArrayList;
import java.util.List;

public class ActionEvent {
    //User searched for products
    public static final String PRODUCT_SEARCH = "product_search";

    //User viewed a product list or category
    public static final String PRODUCT_LIST_VIEW = "product_list_view";

    //User filtered a product list or category
    public static final String PRODUCT_LIST_FILTER = "product_list_filter";

    //User clicked on a product
    public static final String PRODUCT_CLICK = "click";

    //User viewed a product details
    public static final String PRODUCT_VIEW = "view";

    //User added a product to their shopping cart
    public static final String ADD_TO_CART = "add_to_cart";

    //User removed a product from their shopping cart
    public static final String REMOVE_CART = "remove_cart";

    //User viewed their shopping cart
    public static final String CART_VIEW = "view_cart";

    //User initiated the order process (a transaction is created)
    public static final String CHECKOUT = "checkout";

    //User added payment information
    public static final String PAYMENT_INFO_ENTERED = "payment";

    //User purchased and completed the order
    public static final String PURCHASE = "purchase";

    public static final String SCREEN_VIEW = "view";

    public static final String USER_IDENTIFY = "identify";

    public static final String USER_SIGN_OUT = "sign_out";

    public static final String USER_SIGN_IN = "sign-in";

    public static final String ADD_WISH = "add_wish_list";
    public static final String ADD_COMPARE = "add_compare";
    public static final String VIEW_WISH_LIST = "view_wish_list";
    public static final String VIEW_COMPARE_LIST = "view_compare_list";
    public static final String IMPRESSION_ACTION = "impression";
    public static final String VIEWABLE_ACTION = "view";
    public static final String ADX_CLICK_ACTION = "click";

    public static List<String> actionListHasCategoryProduct() {
        List<String> actionEvent = new ArrayList<>();
        actionEvent.add(PRODUCT_CLICK);
        actionEvent.add(PRODUCT_VIEW);
        actionEvent.add(ADD_TO_CART);
        actionEvent.add(REMOVE_CART);
        actionEvent.add(CART_VIEW);
        actionEvent.add(CHECKOUT);
        actionEvent.add(PAYMENT_INFO_ENTERED);
        actionEvent.add(PURCHASE);
        actionEvent.add(ADD_WISH);
        actionEvent.add(ADD_COMPARE);
        actionEvent.add(VIEW_WISH_LIST);
        actionEvent.add(VIEW_COMPARE_LIST);
        return actionEvent;
    }
}
