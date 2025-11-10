package com.example.robles_farma.retrofit;

import com.example.robles_farma.response.ChatResponse;
import com.example.robles_farma.response.MessageResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChatService {

    @GET("chats/")
    Call<List<ChatResponse>> getChats();

    @GET("chats/{chatId}/messages/")
    Call<List<MessageResponse>> getMessages(@Path("chatId") String chatId);

}
