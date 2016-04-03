package com.creativtrendz.folio.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.activities.MainActivity;
import com.creativtrendz.folio.utils.PreferencesUtility;


@SuppressWarnings("unused")
public class FolioUnlock extends Activity {

    private TextView descriptionText;
    private EditText editText1;
    private EditText editText2;
    private Button buttonOk;
    private Button buttonCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        descriptionText = (TextView) findViewById(R.id.textView);
        descriptionText.setText(R.string.unlock_text);
        editText1 = (EditText) findViewById(R.id.first_edittext);
        editText2 = (EditText) findViewById(R.id.second_edittext);
        editText2.setVisibility(View.GONE);
        setFinishOnTouchOutside(false);
        
        buttonCancel = (Button) findViewById(R.id.buttonStandard);
        buttonCancel.setVisibility(View.GONE);

        buttonOk = (Button) findViewById(R.id.buttonSmall);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String restoredText = PreferencesUtility.getString("lockcode", "");
                if(restoredText != null) {
                    if(editText1.getText().toString().equals(restoredText)){
                        finish();
                    } else if (editText1.getText().toString().equals("")){
                        descriptionText.setText(R.string.lock_confirm);
                    } else {
                        descriptionText.setText(R.string.lock_wrong);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        String restoredText = PreferencesUtility.getString("lockcode", "");
        if(restoredText != null) {
            if(editText1.getText().toString().equals(restoredText)){
                finish();
            } else if (editText1.getText().toString().equals("")){
                descriptionText.setText(R.string.lock_confirm);
            } else {
                descriptionText.setText(R.string.lock_wrong);
            }
        }
    }
}
