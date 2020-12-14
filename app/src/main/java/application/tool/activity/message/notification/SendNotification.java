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
    FirebaseUser fUser;
    APIService apiService;

    public SendNotification() {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    public void sendMessage(String receiver, String message,String key) {
        long time = Calendar.getInstance().getTimeInMillis();
        DatabaseReference refDb = FirebaseDatabase.getInstance().getReference();
        Notification notification = new Notification(MESSAGE,fUser.getEmail(),message,key, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ,Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.YEAR),time);
        refDb.child(NOTIFICATION).child(receiver.hashCode()+"").child(time+"").setValue(notification);
        refDb.child(TOKEN).child(receiver.hashCode() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.getValue()!=null){
                   Token token = snapshot.getValue(Token.class);
                   assert token != null;
                   Log.e("AAA",token.getToken());
                   Data data = new Data(MESSAGE, fUser.getEmail(),message,key);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}