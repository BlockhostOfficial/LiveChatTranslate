package net.blockhost.livechattranslate;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DeepLApi {
    @FormUrlEncoded
    @POST("/v2/translate")
    Call<DeepLResponse> translate(@Field("auth_key") String authKey,
                                  @Field("text") String text,
                                  @Field("target_lang") String targetLang);

    @FormUrlEncoded
    @POST("/v2/detect")
    Call<DeepLResponse> detect(@Field("auth_key") String authKey,
                               @Field("text") String text);
}