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

import application.tool.activity.message.module.Notification;
import application.tool.activity.message.object.Data;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static application.tool.activity.message.module.Firebase.TOKEN;
import static application.tool.activity.message.module.Notification.MESSAGE;

public class SendNotification {
    FirebaseUser user;
    APIService apiService;

    public SendNotification() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    public void sendMessage(String receiver, String message,String key) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(TOKEN);
        reference.child(receiver.hashCode() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Token token = snapshot.getValue(Token.class);
                Log.e("AAA",token.getToken());
                Data data = new Data(MESSAGE, user.getEmail(),message,key);
                assert token != null;
                Sender sender = new Sender(data, token.getToken());
                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        Log.e("CODE",response.code()+"");
                        if (response.isSuccessful()){
                            Log.e("RES","SUCCESS");
                        }else {
                            Log.e("RES","FAILED");
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}