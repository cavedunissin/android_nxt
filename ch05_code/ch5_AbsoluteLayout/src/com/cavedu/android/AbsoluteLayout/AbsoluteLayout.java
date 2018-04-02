package com.cavedu.android.AbsoluteLayout;

import com.cavedu.android.ch5_AbsoluteLayout.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class AbsoluteLayout extends Activity {
    /** Called when the activity is first created. */
	TextView textView;
	EditText editText;
	Button button_Enter;
	SeekBar seekBar;
	String progress;
	int value;
	boolean wrong = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //找到互動式元件的物件
        textView = (TextView)findViewById(R.id.tv);
        editText = (EditText)findViewById(R.id.edt);
        button_Enter = (Button)findViewById(R.id.btn_Enter);
        seekBar = (SeekBar)findViewById(R.id.sb);
        //按鈕事件，按下後會將輸入數值顯示再TextView和SeelBar上
        button_Enter.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				progress = editText.getText().toString();
				try{
					value = Integer.parseInt(progress);
				}catch(Exception e){}
				seekBar.setProgress(value>100?0:value);
				textView.setText("The input is:" + progress);
			}
        });
        //seekBar狀態事件監聽
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
			@Override
			//當SeekBar被拉動停止時
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				textView.setText("The value is:" + seekBar.getProgress());
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
			}
		});
    }
}