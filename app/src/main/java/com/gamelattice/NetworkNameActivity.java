package com.gamelattice;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yodo1.mas.Yodo1Mas;
import com.yodo1.mas.error.Yodo1MasError;

public class NetworkNameActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    //public static final String EXTRA_REPLY = "com.example.android.twoactivities.extra.REPLY";
    //private EditText mReply;
    protected static String gameName;
    protected static String reply;
    protected EditText inputText ;
    protected AlertDialog dialog;
    //protected Button btnSubmit;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        Yodo1Mas.getInstance().init(this, "EFbwsxe0ocS", new Yodo1Mas.InitListener() {
            @Override
            public void onMasInitSuccessful() {
            }

            @Override
            public void onMasInitFailed(@NonNull Yodo1MasError error) {
            }
        });
        setContentView(R.layout.activity_network_name);
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts, null);
        TextView textView = (TextView)promptsView.findViewById(R.id.tv);
        textView.setText(NetworkGame.response);
        inputText = (EditText) promptsView.findViewById(R.id.input_text);//promptsView.
        inputText.setHint("Enter name using lower case letters and digits");
        inputText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        InputFilter filter2 =getCustomInputFilter(true, true, true);
        inputText.setFilters(new InputFilter[] { filter2 });
        dialog = new AlertDialog.Builder(this)
                .setTitle("input network gameID")
                //.setMessage("input network gameID")
                .setView(promptsView)
                .setCancelable(false)
                .setNegativeButton("Cancel", this)
                .setPositiveButton("Ok", this)
                .create();
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
            gameName = inputText.getText().toString();
            reply = String.valueOf(i);
            finish();
            navigateUpTo(new Intent(getBaseContext(), NetworkGame.class));
    }

    public static InputFilter getCustomInputFilter(final boolean allowCharacters, final boolean allowDigits, final boolean allowSpaceChar) {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) {
                        sb.append(c);
                    } else {
                        keepOriginal = false;
                    }
                }
                if (keepOriginal) {
                    return null;
                } else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }
            private boolean isCharAllowed(char c) {
                if (Character.isLetter(c) & Character.isLowerCase(c) && allowCharacters) {
                    return true;
                }
                if (Character.isDigit(c) && allowDigits) {
                    return true;
                }
                //if (Character.isSpaceChar(c) && allowSpaceChar) {
                //    return true;
                //}
                return false;
            }
        };
    }
}
/*InputFilter filter1 = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isUpperCase(source.charAt(i))){
                        Toast.makeText(GameStart.ctx, "Invalid Input", Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))){
                        Toast.makeText(GameStart.ctx, "Invalid Input", Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };*/
        /*btnSubmit.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            gameName = inputText.getText().toString();
            reply = "-1";
            finish();
        }
        });*/