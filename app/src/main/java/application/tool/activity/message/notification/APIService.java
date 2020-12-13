package application.tool.activity.message.notification;

import application.tool.activity.message.notification.MyResponse;
import application.tool.activity.message.notification.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization: key= AAAAyV3eJ7I:APA91bFN6Oa41-tsGx6Kjk-Yug6z7vLE5FRCuHK_Z64jhKyFo1i_a1zntS5Q2xOE_w4776D8zVptezz23fKGvCyNUkNiXZ0wXkFLs2tVUN9shu-bavoI5cjF0K1o0vAuQkMFl4sAKn2Y"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
