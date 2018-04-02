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
        //��줬�ʦ����󪺪���
        textView = (TextView)findViewById(R.id.tv);
        editText = (EditText)findViewById(R.id.edt);
        button_Enter = (Button)findViewById(R.id.btn_Enter);
        seekBar = (SeekBar)findViewById(R.id.sb);
        //���s�ƥ�A���U��|�N��J�ƭ���ܦATextView�MSeelBar�W
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
        //seekBar���A�ƥ��ť
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
			@Override
			//��SeekBar�Q�԰ʰ����
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