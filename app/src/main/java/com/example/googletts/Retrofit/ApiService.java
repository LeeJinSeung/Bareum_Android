package com.example.googletts.Retrofit;

import com.example.googletts.Retrofit.DTO.ResultDTO;
import com.example.googletts.Retrofit.DTO.SynthesizeDTO;
import com.example.googletts.Retrofit.DTO.SynthesizeRequestDTO;
import com.example.googletts.Retrofit.DTO.TestDTO;
import com.example.googletts.Retrofit.DTO.analysisDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService<Int> {
    // dummy url
    @POST(Config.SYNTHESIZE_ENDPOINT)
    Call<SynthesizeDTO> TTS(@Header(Config.API_KEY_HEADER) String token, @Body SynthesizeRequestDTO requestDTO); //해더에 key Authorization String 형태의 토큰을 요구함

    @GET("/getSentence")
    Call<List<TestDTO>> requestSentence();

    @FormUrlEncoded
    @POST("/getResult")
    Call<analysisDTO> requestResult(@FieldMap HashMap<String, Object> param);

    @GET("/getTotal")
    Call<ResultDTO> requestTotal();

    @GET("/getCustomSentence")
    Call<List<TestDTO>> requestUserSentence();

    @FormUrlEncoded
    @POST("/deleteSentence")
    Call<String> postDeleteSentaence(@Field("sentenceId") ArrayList<String> list);

}