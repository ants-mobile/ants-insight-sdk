package ants.mobile.ants_insight.Constants;

import java.util.ArrayList;
import java.util.List;

public class ActionEvent {
    //User searched for products
    public static final String PRODUCTS_SEARCHED_ACTION = "product_search";

    //User viewed a product list or category
    public static final String PRODUCT_LIST_VIEWED_ACTION = "product_list_view";

    //User filtered a product list or category
    public static final String PRODUCT_LIST_FILTERED_ACTION = "product_list_filter";

    //User clicked on a product
    public static final String PRODUCT_CLICK_ACTION = "click";

    //User viewed a product details
    public static final String PRODUCT_VIEW_ACTION = "view";

    //User added a product to their shopping cart
    public static final String ADD_TO_CART_ACTION = "add_to_cart";

    //User removed a product from their shopping cart
    public static final String REMOVE_CART_ACTION = "remove_cart";

    //User viewed their shopping cart
    public static final String CART_VIEW_ACTION = "view_cart";

    //User initiated the order process (a transaction is created)
    public static final String CHECKOUT_ACTION = "checkout";

    //User added payment information
    public static final String PAYMENT_INFO_ENTERED_ACTION = "payment";

    //User purchased and completed the order
    public static final String PURCHASE_ACTION = "purchase";

    public static final String SCREEN_VIEW_ACTION = "view";

    public static final String USER_IDENTIFY_ACTION = "identify";

    public static final String USER_SIGN_OUT_ACTION = "sign_out";

    public static final String USER_SIGN_IN_ACTION = "sign-in";

    public static final String SCREEN_VIEW_CATEGORY = "screenview";
    public static final String USER_IDENTIFY_CATEGORY = "user";
    public static final String BROWSING_CATEGORY = "browsing";
    public static final String PRODUCT_CATEGORY = "product";

    public static final String ADVERTISING_CATEGORY = "advertising";
    public static final String IMPRESSION_ACTION = "impression";
    public static final String VIEWABLE_ACTION = "view";
    public static final String ADX_CLICK_ACTION = "click";

    public static List<String> actionListHasCategoryProduct() {
        List<String> actionEvent = new ArrayList<>();
        actionEvent.add(PRODUCT_CLICK_ACTION);
        actionEvent.add(PRODUCT_VIEW_ACTION);
        actionEvent.add(ADD_TO_CART_ACTION);
        actionEvent.add(REMOVE_CART_ACTION);
        actionEvent.add(CART_VIEW_ACTION);
        actionEvent.add(CHECKOUT_ACTION);
        actionEvent.add(PAYMENT_INFO_ENTERED_ACTION);
        actionEvent.add(PURCHASE_ACTION);
        return actionEvent;
    }
}
