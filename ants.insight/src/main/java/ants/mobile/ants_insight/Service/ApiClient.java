package ants.mobile.ants_insight.Service;

import android.content.Context;
import android.text.TextUtils;

import androidx.multidex.BuildConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import adx.Utils;
import ants.mobile.ants_insight.Constants.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static volatile InsightApiDetail mInsightServiceApi = null;
    private static volatile DeliveryApiDetail mDeliveryApiDetail = null;

    public static InsightApiDetail getInsightInstance(Context mContext) {
        if (mInsightServiceApi == null) {
            mInsightServiceApi = createFromInsight(mContext);
        }
        return mInsightServiceApi;
    }

    public static DeliveryApiDetail getDeliveryInstance(Context mContext) {
        if (mDeliveryApiDetail == null) {
            mDeliveryApiDetail = createFromDelivery(mContext);
        }
        return mDeliveryApiDetail;
    }

    private static DeliveryApiDetail createFromDelivery(Context mContext) {
        final String BASE_URL = "http://delivery.cdp.asia/";
        String deliveryURL = Utils.getSharedPreValue(mContext, Constants.DELIVERY_URL);

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

    private static InsightApiDetail createFromInsight(Context mContext) {

        final String BASE_URL = "http://a.cdp.asia/";
        String insightURL = Utils.getSharedPreValue(mContext, Constants.INSIGHT_URL);

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
