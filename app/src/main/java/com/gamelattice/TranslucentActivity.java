package com.gamelattice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.yodo1.mas.Yodo1Mas;
import com.yodo1.mas.error.Yodo1MasError;

public class TranslucentActivity extends AppCompatActivity implements DialogInterface.OnClickListener {
   // public static final String EXTRA_REPLY = "com.example.android.twoactivities.extra.REPLY";
  // private EditText mReply;
    public static String reply;
    /*public void returnReply(View view) {
        String reply = mReply.getText().toString();
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, reply);
        setResult(RESULT_OK, replyIntent);

    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translucent);
        LayoutInflater li = LayoutInflater.from(this);
        View ynView = li.inflate(R.layout.yes_no, null);
        TextView textView = (TextView)ynView.findViewById(R.id.yesno);
        textView.setText("CURRENT GAME WILL OVER, ARE YOU AGREE ? (Y/N)");
        //textView.setTextColor(Color.RED);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change mode of game or restart")
                .setMessage(" ?")
                .setView(ynView)
                .setCancelable(false)
                .setNegativeButton("NO", this)
                .setPositiveButton("YES", this)
                .create();
                dialog.show();
                Yodo1Mas.getInstance().init(this, "EFbwsxe0ocS", new Yodo1Mas.InitListener() {
                    @Override
                        public void onMasInitSuccessful() {
                        }
                    @Override
                        public void onMasInitFailed(@NonNull Yodo1MasError error) {
                        }
                    });
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        //mReply = findViewById(R.id.editText);
        reply = String.valueOf(i); //mReply.getText().toString();
        //Intent replyIntent = new Intent();
        //replyIntent.putExtra(EXTRA_REPLY, reply);
        //setResult(RESULT_OK, replyIntent);
        finish();
        //navigateUpTo(new Intent(getBaseContext(), NetworkGame.class));
    }
}

