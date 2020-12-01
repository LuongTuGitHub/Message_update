package application.tool.activity.message.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import application.tool.activity.message.R;
import application.tool.activity.message.ViewProfilePersonActivity;

public class ConversationAlert {
    Context context;
    public ConversationAlert(Context context){
        this.context = context;
    }
    public AlertDialog getAlertForCouple(String email){
        AlertDialog.Builder dialog  = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_conversation_couple,null);
        dialog.setView(view);
        final  AlertDialog alertDialog = dialog.create();
        Button viewProfile = view.findViewById(R.id.viewProfile);
        viewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewProfilePersonActivity.class);
            intent.putExtra("email",email);
            intent.putExtra("to","conversation");
            context.startActivity(intent);
            alertDialog.dismiss();
        });
        return  alertDialog;
    }

    public AlertDialog getAlertGroup(String key){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        final  AlertDialog alertDialog = builder.create();


        return alertDialog;
    }
}
