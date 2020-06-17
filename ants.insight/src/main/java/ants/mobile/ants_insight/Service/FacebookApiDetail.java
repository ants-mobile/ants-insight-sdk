package ants.mobile.ants_insight.Service;


import java.util.Map;

import ants.mobile.ants_insight.Model.DataRequestFaceBook;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.QueryName;

/**
 * Created by luonglc on 15/6/2020
 * E: lecongluong94@gmail.com
 * C: ANTS Programmatic Company
 * A: HCMC, VN
 */
public interface FacebookApiDetail {
    @POST("/v7.0/{app_id}/activities?")
    Observable<Object> fbLogEvent(@Path("app_id") String appId, @QueryMap Map<String, String> query,
                                  @Body DataRequestFaceBook requestParam);
}
