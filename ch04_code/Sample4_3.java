import lejos.nxt.*;
import lejos.util.Delay;
class Sample4_3
{
	public static void main(String args[]) throws Exception
	{
		Button.ESCAPE.addButtonListener(new ButtonListener()
		{
			public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
		});
		
		TouchSensor touch = new TouchSensor(SensorPort.S1); 
//建立TouchSensor物件，將觸碰感應器接在輸入端1
		
		Motor.B.setSpeed(360);
		Motor.C.setSpeed(360);
		
		while(true)
		{
			if(touch.isPressed()) //撞到障礙物
			{
				//煞車
				Motor.B.stop();
				Motor.C.stop();
				
				//倒退0.8秒
				Motor.B.backward();
				Motor.C.backward();
				Delay.msDelay(800);
				
				//轉彎0.5秒
				Motor.B.forward();
				Motor.C.backward();
				Delay.msDelay(500);
			}//if
			else //沒撞到物件
			{
				//繼續行走
				Motor.B.forward();
				Motor.C.forward();
			}//else
		}//while
	}//main
}//Sample4_3
