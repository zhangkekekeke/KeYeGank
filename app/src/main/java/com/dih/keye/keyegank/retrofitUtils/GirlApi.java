package com.dih.keye.keyegank.retrofitUtils;

        import com.dih.keye.keyegank.model.GirlData;

        import retrofit2.adapter.rxjava.Result;
        import retrofit2.http.GET;
        import retrofit2.http.Path;
        import rx.Observable;

/**
 * Created by zsj on 2015/11/20 0020.
 */
public interface GirlApi {

    @GET("data/福利/10/{page}")
    Observable<Result<GirlData>> fetchPrettyGirl(@Path("page") int page);
}