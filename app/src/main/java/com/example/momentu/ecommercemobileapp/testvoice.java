package com.example.momentu.ecommercemobileapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class testvoice extends AppCompatActivity {
    private EditText searchtext;
    private ImageView searchImage;
    private static final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testvoice);
        searchtext=(EditText)findViewById(R.id.editTexttest);
        searchImage=(ImageView)findViewById(R.id.imageViewtest);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, What do you want to search for?");
                try {
                    startActivityForResult(intent,REQ_CODE_SPEECH_INPUT);
                }
                catch (ActivityNotFoundException a){
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if ( data!=null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("speeech", "onActivityResult: "+result.get(0));
                    searchtext.setText(result.get(0));
                }
                else{
                    Log.d("speeech", "Resultcode: "+resultCode);
                    Log.d("speeech", "data: "+data);
                    Log.d("speeech", "onActivityResult: "+"not worrrrrrrrrking");
                }
                break;
            }

        }
    }
}
