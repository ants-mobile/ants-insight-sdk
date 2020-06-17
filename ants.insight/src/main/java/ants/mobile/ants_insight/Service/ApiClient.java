package ants.mobile.ants_insight.Service;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import ants.mobile.ants_insight.BuildConfig;
import ants.mobile.ants_insight.Constants.Constants;
import ants.mobile.ants_insight.InsightSharedPref;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static volatile InsightApiDetail mInsightServiceApi = null;
    private static volatile FacebookApiDetail mFbApiDetail = null;
    private static volatile DeliveryApiDetail mDeliveryApiDetail = null;

    public static InsightApiDetail getInsightInstance() {
        if (mInsightServiceApi == null) {
            mInsightServiceApi = createFromInsight();
        }
        return mInsightServiceApi;
    }

    public static DeliveryApiDetail getDeliveryInstance() {
        if (mDeliveryApiDetail == null) {
            mDeliveryApiDetail = createFromDelivery();
        }
        return mDeliveryApiDetail;
    }

    public static FacebookApiDetail getFbApiInstance() {
        if (mFbApiDetail == null) {
            mFbApiDetail = createFromFacebook();
        }
        return mFbApiDetail;
    }

    private static DeliveryApiDetail createFromDelivery() {
        final String BASE_URL = "http://delivery.cdp.asia/";
        String deliveryURL = InsightSharedPref.getStringValue(Constants.PREF_DELIVERY_URL);

        Gson gson = new GsonBuilder().serializeNulls().setLenient().create();
        RxJava2CallAdapterFactory callAdapter = RxJava2CallAdapterFactory.create();

        OkHttpClient client = createHttpClient();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.baseUrl(TextUtils.isEmpty(deliveryURL) ? BASE_URL : deliveryURL);
        retrofitBuilder.client(client);

        retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson));
        retrofitBuilder.addCallAdapterFactory(callAdapter);

        Retrofit retrofit = retrofitBuilder.build();

        return retrofit.create(DeliveryApiDetail.class);
    }

    private static InsightApiDetail createFromInsight() {

        final String BASE_URL = "http://a.cdp.asia/";
        String insightURL = InsightSharedPref.getStringValue(Constants.PREF_INSIGHT_URL);

        Gson gson = new GsonBuilder().serializeNulls().setLenient().create();
        RxJava2CallAdapterFactory callAdapter = RxJava2CallAdapterFactory.create();

        OkHttpClient client = createHttpClient();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.baseUrl(TextUtils.isEmpty(insightURL) ? BASE_URL : insightURL);

        retrofitBuilder.client(client);

        retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson));
        retrofitBuilder.addCallAdapterFactory(callAdapter);

        Retrofit retrofit = retrofitBuilder.build();

        return retrofit.create(InsightApiDetail.class);
    }

    private static FacebookApiDetail createFromFacebook() {
        final String BASE_URL = "https://graph.facebook.com/";

        Gson gson = new GsonBuilder().serializeNulls().setLenient().create();
        RxJava2CallAdapterFactory callAdapter = RxJava2CallAdapterFactory.create();

        OkHttpClient client = createHttpClient();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.baseUrl(BASE_URL);
        retrofitBuilder.client(client);

        retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson));
        retrofitBuilder.addCallAdapterFactory(callAdapter);

        Retrofit retrofit = retrofitBuilder.build();

        return retrofit.create(FacebookApiDetail.class);
    }

    private static OkHttpClient createHttpClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder();
            builder.method(original.method(), original.body()).build();
            Request request = builder.build();

            return chain.proceed(request);
        });

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptorBody = new HttpLoggingInterceptor();
            interceptorBody.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(interceptorBody);
        }

        httpClient.connectTimeout(10, TimeUnit.SECONDS);
        httpClient.readTimeout(10, TimeUnit.SECONDS);
        httpClient.writeTimeout(10, TimeUnit.SECONDS);
        httpClient.hostnameVerifier((hostname, session) -> true);

        return httpClient.build();
    }

}
