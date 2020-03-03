package ants.mobile.ants_insight.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import ants.mobile.ants_insight.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static volatile InsightApiDetail mInsightServiceApi = null;
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

    private static DeliveryApiDetail createFromDelivery() {
        final String BASE_LC = "http://delivery.cdp.asia/";
        Gson gson = new GsonBuilder().serializeNulls().create();
        RxJava2CallAdapterFactory callAdapter = RxJava2CallAdapterFactory.create();

        OkHttpClient client = createHttpClient();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.baseUrl(BASE_LC);
        retrofitBuilder.client(client);

        retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson));
        retrofitBuilder.addCallAdapterFactory(callAdapter);

        Retrofit retrofit = retrofitBuilder.build();

        return retrofit.create(DeliveryApiDetail.class);
    }

    private static InsightApiDetail createFromInsight() {

        final String BASE_LC = "http://a.cdp.asia/";

        Gson gson = new GsonBuilder().serializeNulls().create();
        RxJava2CallAdapterFactory callAdapter = RxJava2CallAdapterFactory.create();

        OkHttpClient client = createHttpClient();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.baseUrl(BASE_LC);
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
