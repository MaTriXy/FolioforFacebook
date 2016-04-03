package com.creativtrendz.folio.activities;
import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.utils.PreferencesUtility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


@SuppressWarnings("unused")
public class FolioLockSetup extends Activity {

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
        editText1 = (EditText) findViewById(R.id.first_edittext);
        editText2 = (EditText) findViewById(R.id.second_edittext);

        buttonOk = (Button) findViewById(R.id.buttonSmall);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText1.getText().toString().equals(editText2.getText().toString())){
                	PreferencesUtility.putString("lockcode", editText1.getText().toString());
                    finish();
                } else if (editText1.getText().toString().equals("")){
                    descriptionText.setText(R.string.lock_empty);
                }
            }
        });
        
        buttonCancel = (Button) findViewById(R.id.buttonStandard);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                
            finish();
           
            }});
        
    }
    
}

