package com.example.googletts.Retrofit;

import com.example.googletts.Retrofit.DTO.ResultDTO;
import com.example.googletts.Retrofit.DTO.SynthesizeDTO;
import com.example.googletts.Retrofit.DTO.SynthesizeRequestDTO;
import com.example.googletts.Retrofit.DTO.TestDTO;
import com.example.googletts.Retrofit.DTO.analysisDTO;
import com.example.googletts.Retrofit.DTO.messageDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService<Int> {
    // dummy url
    @POST(Config.SYNTHESIZE_ENDPOINT)
    Call<SynthesizeDTO> TTS(@Header(Config.API_KEY_HEADER) String token, @Body SynthesizeRequestDTO requestDTO); //해더에 key Authorization String 형태의 토큰을 요구함

    @GET("/getSentence")
    Call<List<TestDTO>> requestSentence();

    @GET("/getCustomSentence")
    Call<List<TestDTO>> requestUserSentence();

    @FormUrlEncoded
    @POST("/insertSentence")
    Call<messageDTO> insertSentaence(@Field("sentenceData") String sentence);

    @FormUrlEncoded
    @POST("/deleteSentence")
    Call<messageDTO> deleteSentaence(@Field("sentenceId") ArrayList<Integer> list);

    @GET("/getTotal")
    Call<ResultDTO> requestTotal();

    @Multipart
    @POST("/getResult")
    Call<analysisDTO> requestResult(@Part MultipartBody.Part file, @Part("fileName") RequestBody name, @Part("sentenceId") RequestBody sid);

    @Multipart
    @POST("/insertWordBook")
    Call<ResponseBody> insertWordBook(@Part("wordData") RequestBody wordData);
}