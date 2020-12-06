package application.tool.activity.message.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization: key= AAAAyV3eJ7I:APA91bFBQuGfn1JWaAhGWrSCuZC1-qdBmqohAOx_fesjrJmnHTZfJ41uZ8nh9HYOzuHJ9SvSzGNahelY2n5k1Pkxhk16qjhBRWv-2yLvQuszXu1yAkaDKVywcF809raDisZj-yzZLapB"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
