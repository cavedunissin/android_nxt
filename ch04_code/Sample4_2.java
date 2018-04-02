import lejos.nxt.*; 
import lejos.util.Delay; 
class Sample4_2
{
	public static void main(String args[])
	{
		//定義取消鍵可以結束程式
		Button.ESCAPE.addButtonListener(new ButtonListener()
		{
			public void buttonPressed(Button b){System.exit(0);}
			public void buttonReleased(Button b){}
		});
		
		//設定速度
		Motor.B.setSpeed(600); //設定B馬達速度為600(度/秒) 
		Motor.C.setSpeed(600); //設定C馬達速度為600(度/秒)
		
		//車子前進3.5秒
		Motor.B.forward(); //B馬達正轉
		Motor.C.forward(); //C馬達正轉
		Delay.msDelay(3500); //等待3.5秒
		
		//車子左轉2.5秒
		Motor.B.stop(); //B馬達停止
		Delay.msDelay(2500); //等待2.5秒
		
		//車子倒退2秒
		Motor.B.backward(); //B馬達倒轉
		Motor.C.backward(); //C馬達倒轉
		Delay.msDelay(2000); //等待2秒
	}//main
}//Sampl4_2
