package com.dih.keye.keyegank.retrofitUtils;

import java.util.List;

import retrofit2.adapter.rxjava.Result;
import rx.functions.Func1;

/**
 * Created by zsj on 2016/3/20.
 */
public class Results {

    public static Func1<Result<?>, Boolean> DATA_FUNC =
            result -> !result.isError() && result.response().isSuccessful();

    public static Func1<Result<?>, Boolean> isSuccess() {
        return DATA_FUNC;
    }

    public static Func1<List<?>, Boolean> IMAGE_FUNC =
            images -> images.size() != 0;

    public static Func1<List<?>, Boolean> isNull() {
        return IMAGE_FUNC;
    }

    private Results() {
        throw new AssertionError("no instances ");
    }
}