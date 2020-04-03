package ants.mobile.ants_insight.Service;
import com.google.gson.JsonObject;

import java.util.Map;

import ants.mobile.ants_insight.Model.DeliveryResponse;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface DeliveryApiDetail {

    @POST("/delivery/trigger/")
    Observable<DeliveryResponse> logDelivery(@QueryMap Map<String, String> query, @Body Object requestParam);
}
