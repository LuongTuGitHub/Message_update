package application.tool.activity.message.notification;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;

import application.tool.activity.message.object.Data;
import application.tool.activity.message.object.Notification;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static application.tool.activity.message.module.Firebase.NOTIFICATION;
import static application.tool.activity.message.module.Firebase.TOKEN;
import static application.tool.activity.message.module.Notification.MESSAGE;

public class SendNotification {
    FirebaseUser user;
    APIService apiService;

    public SendNotification() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    public void sendMessage(String receiver, String message, String key) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(TOKEN).child(receiver.hashCode() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(key, message, user.getEmail());
                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.isSuccessful()) {
                                Log.e("RES", "SUCCESS");
                            } else {
                                Log.e("RES", "FAILED");
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}