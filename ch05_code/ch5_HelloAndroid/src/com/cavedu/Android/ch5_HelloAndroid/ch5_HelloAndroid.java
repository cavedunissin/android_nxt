package com.cavedu.Android.ch5_HelloAndroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class ch5_HelloAndroid extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.i("Tag","onCreat()");
    }
    public void onStart(){
    	super.onStart();
    	Log.i("Tag","onStart()");
    }
    public void onRestart(){
    	super.onRestart();
    	Log.i("Tag","onRestart()");
    }
    public void onResume(){
    	super.onResume();
    	Log.i("Tag","onResume()");
    }
    public void onPause(){
    	super.onPause();
    	Log.i("Tag","onPause()");
    }
    public void onStop(){
    	super.onStop();
    	Log.i("Tag","onStop()");
    }
    public void onDestroy(){
    	super.onDestroy();
    	Log.i("Tag","onDestroy()");
    }
}