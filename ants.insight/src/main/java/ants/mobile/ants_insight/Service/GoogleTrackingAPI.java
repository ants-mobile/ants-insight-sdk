package ants.mobile.ants_insight.Service;

import java.util.Map;

import ants.mobile.ants_insight.Model.DataRequestFaceBook;
import ants.mobile.ants_insight.Model.GoogleTrackingModel;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by luonglc on 18/6/2020
 * E: lecongluong94@gmail.com
 * C: ANTS Programmatic Company
 * A: HCMC, VN
 */
public interface GoogleTrackingAPI {
    @POST("/pagead/conversion/app/1.0")
    Observable<Object> trackingEvent(@QueryMap Map<String, String> query,
                                     @Body GoogleTrackingModel requestParam);
}
