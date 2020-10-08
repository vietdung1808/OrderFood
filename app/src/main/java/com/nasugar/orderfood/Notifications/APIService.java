package com.nasugar.orderfood.Notifications;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAXwbXEJA:APA91bFitS8AQ3_awDD296fpVTUF_Sx2jt2FSVuYBvv91GWS8INsTB00srq5gLnRFTIQtIjwkcAsOZi19ItVTPCv72zVumVT9N5slAqS533PU7uK_y6d7NTvF-C5MLPJSU7iKffsoos9"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
