package com.jeff.dsource.rxrequest.retrofit;

import retrofit2.Retrofit;

/**
 * <p>
 *
 * @author Jeff
 * @date 2020/6/12
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class RetrofitHelper {

    public static Retrofit.Builder applyNewBaseUrl(Retrofit.Builder builder,String newUrl){
        return builder.baseUrl(newUrl);
    }
}
