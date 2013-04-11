package com.powerSearch;

import java.util.ArrayList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SpeechToTextActivity extends Activity {

	protected static final int RESULT_SPEECH = 1;
	 
    private ImageButton btnSpeak;
    private TextView txtText;
    private Button btnSearch;
    private Button btnRerecord;
    private ArrayList<String> text = new ArrayList<String>();
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);
 
        txtText = (TextView) findViewById(R.id.txtText);
 
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnRerecord = (Button) findViewById(R.id.btnRerecord);
        
        btnSearch.setOnClickListener(new OnClickListener(){
			public void onClick(View V){
			System.out.println("IN onclick");
			text_search();
			finish();
			}
			
	});
        
        btnRerecord.setOnClickListener(new OnClickListener(){
			public void onClick(View V){
			System.out.println("IN onclick");
			recordAgain(V);
			finish();
			}
			
	});
        
        
        btnSpeak.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
 
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
 
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
 
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    txtText.setText("");
                    btnSearch.setVisibility(View.VISIBLE);
                    btnRerecord.setVisibility(View.VISIBLE);
                    
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                } 
                        
            }
        });
 
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.speech_to_text, menu);
        return true;
    }
 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case RESULT_SPEECH: {
            if (resultCode == RESULT_OK && null != data) {
 
               text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
 //TODO: DO we need to make getter/setter pair for text?
                txtText.setText(text.get(0));
            }
            break;
        }
 
        }
    }

    private void text_search(){
		
		Intent search = new Intent(Intent.ACTION_WEB_SEARCH);  
		search.putExtra(SearchManager.QUERY, text.get(0));  
		startActivity(search);  
	}

    public void recordAgain(View view){
		Intent intent = new Intent(this, SpeechToTextActivity.class);
		startActivity(intent);
	}
}
