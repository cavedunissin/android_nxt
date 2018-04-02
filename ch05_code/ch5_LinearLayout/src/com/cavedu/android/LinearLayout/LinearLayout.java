package com.cavedu.android.LinearLayout;

import com.cavedu.android.ch5_LinearLayout.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LinearLayout extends Activity {
    /** Called when the activity is first created. */
	//宣告互動式元件
	TextView tv_hint;
	Button btn_changeWord;
	Button btn_changeColor;
	int count = 0;
	int count2 = 0;
	//定義顏色代碼
	String yellowColor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //依據id找到在Layout上面的互動式元件物件
        tv_hint = (TextView) findViewById(R.id.tv_hint);
        btn_changeWord = (Button)findViewById(R.id.btn_changeWord);
        btn_changeColor = (Button)findViewById(R.id.btn_changeColor);
        //註冊按鈕點擊事件
        btn_changeWord.setOnClickListener( new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//更改TextView內容
				if((count%2)==0)
					tv_hint.setText(R.string.word_1);
				else if((count%2)==1)
					tv_hint.setText(R.string.word_2);
				count = count>2?0:count+1;
			}
        });
      //註冊按鈕點擊事件
        btn_changeColor.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//更改TextView顏色
				if((count2%3)==0){
					yellowColor = getResources().getString(R.color.yellow);
					tv_hint.setTextColor(Color.parseColor(yellowColor));
				}
				else if((count2%3)==1)
					tv_hint.setTextColor(Color.GRAY);
				else if((count2%3)==2)
					tv_hint.setTextColor(Color.rgb(0,100,1));
				count2 = count2>2?0:count2+1;
			}
        });
    }
}