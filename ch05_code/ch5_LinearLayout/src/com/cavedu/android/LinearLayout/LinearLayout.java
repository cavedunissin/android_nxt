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
	//�ŧi���ʦ�����
	TextView tv_hint;
	Button btn_changeWord;
	Button btn_changeColor;
	int count = 0;
	int count2 = 0;
	//�w�q�C��N�X
	String yellowColor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //�̾�id���bLayout�W�������ʦ����󪫥�
        tv_hint = (TextView) findViewById(R.id.tv_hint);
        btn_changeWord = (Button)findViewById(R.id.btn_changeWord);
        btn_changeColor = (Button)findViewById(R.id.btn_changeColor);
        //���U���s�I���ƥ�
        btn_changeWord.setOnClickListener( new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//���TextView���e
				if((count%2)==0)
					tv_hint.setText(R.string.word_1);
				else if((count%2)==1)
					tv_hint.setText(R.string.word_2);
				count = count>2?0:count+1;
			}
        });
      //���U���s�I���ƥ�
        btn_changeColor.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//���TextView�C��
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