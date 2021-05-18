package com.gamelattice;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class LoadFileActivity extends AppCompatActivity {
    protected static String fName="";
    protected static Boolean readyToLoad = false;
    protected static String reply;
    private String tempName;
    private TextView fileName;
    private GameData gd;
    protected ArrayList<String> fileList;
    protected AlertDialog dialog;
    protected View savefileView;
    private boolean exist = false;
    protected static Boolean canceled = false;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gd = new GameData(GameStart.ctx);
        fileList = gd.getModeFileNames(GameStart.ctx);
        setContentView(R.layout.activity_load_file);
        LayoutInflater li = LayoutInflater.from(this);
        savefileView = li.inflate(R.layout.load_file, null);
        fileName = (EditText) savefileView.findViewById(R.id.file_name);
        final Button btnOk = (Button) savefileView.findViewById(R.id.btn_ok);
        btnOk.setText("LOAD");
        Button btnCancel = (Button) savefileView.findViewById(R.id.btn_cancel);
        final ListView listDir = (ListView) savefileView.findViewById(R.id.list_view);

        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, fileList);
        listDir.setAdapter(adapter);
        InputFilter filter2 =getCustomInputFilter(true, true, true);
        fileName.setFilters(new InputFilter[] { filter2 });
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog  .setTitle("load game file")
                //.setMessage("load game file")
                //.setMessage("Choose new gameID (the same for you and rival):")
                //.setView(inputText)
                .setView(savefileView)
                .setCancelable(false)
                .create();
        dialog.show();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName.setTextColor(Color.WHITE);
                fileName.setEnabled(true);
                btnOk.setText("LOAD");
                if (!exist) {
                    tempName = fileName.getText().toString();
                    if (!tempName.endsWith(".ltc")) tempName = tempName + ".ltc";
                    if (!fileList.contains(tempName) ) {
                        fileName.setTextColor(Color.RED);
                        fileName.setEnabled(false);
                        fileName.setText(tempName + " NO SUCH FILE, TRY ANOTHER ?");
                        btnOk.setText("YES");
                        exist = true;
                        return;
                    }
                }
                if(exist) {
                    fileName.setText("");
                    btnOk.setText("LOAD");
                    exist = false;
                    return;
                }
                fName = tempName;
                readyToLoad = true;
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName.setTextColor(Color.WHITE);
                fileName.setEnabled(true);
                if (exist) {
                    exist = false;
                    //fName = tempName;
                    //fileName.setText("");
                    //return;
                }
                canceled = true;
                finish();
            }
        });
        listDir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //System.out.println("listDir.setOnTouchListener " +  parent.getAdapter().getItem(position));
                fileName.setText(parent.getAdapter().getItem(position).toString());
            }
        });
    }
    public static InputFilter getCustomInputFilter(final boolean allowCharacters, final boolean allowDigits, final boolean allowSomeChars) {
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
                if (Character.isLetter(c) && allowCharacters) {
                    return true;
                }
                if (Character.isDigit(c) && allowDigits) {
                    return true;
                }
                if (allowSomeChars) {
                    if (Character.toString(c).equals(".") |
                            Character.toString(c).equals(",") |
                            Character.toString(c).equals(" ") |
                            Character.toString(c).equals("?") |
                            Character.toString(c).equals("_")) return true;
                }
                return false;
            }
        };
    }
}