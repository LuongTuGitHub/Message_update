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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotification {
    FirebaseUser user;
    APIService apiService;

    public SendNotification() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    public void sendMessage(String receiver, String message) {
        Log.e("AAA","Đã gửi");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        reference.child(receiver.hashCode() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Token token = snapshot.getValue(Token.class);
                Data data = new Data(receiver, message, user.getEmail());
                assert token != null;
                Sender sender = new Sender(data, token.getToken());
                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
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
