package com.example.googletts.Retrofit;

import com.example.googletts.Retrofit.DTO.SynthesizeDTO;
import com.example.googletts.Retrofit.DTO.SynthesizeRequestDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    // dummy url
    @POST(Config.SYNTHESIZE_ENDPOINT)
    Call<SynthesizeDTO> TTS(@Header (Config.API_KEY_HEADER) String token, @Body SynthesizeRequestDTO requestDTO); //해더에 key Authorization String 형태의 토큰을 요구함

    @GET("/posts")
    Call<List<TestDTO>> REQUESTSENTENCE();

}