import lejos.nxt.*;
class Sample4_6
{
	public static void main(String args[]) throws Exception
	{ 
		Button.ESCAPE.addButtonListener(new ButtonListener()
		{
			public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
		});
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4); 
//建立UltrasonicSensor物件，將超音波感應器接在輸入端4
		int distance, limit = 15; //設定保持的距離為15公分
		
		Motor.B.setSpeed(200);
		Motor.C.setSpeed(200);
		
		while(true)
		{
			distance = us.getDistance();
			if(distance==255 || distance==limit) //沒有物件或距離剛好
			{
				Motor.B.stop();
				Motor.C.stop();
			}//if
			else if(distance>limit) //物體太遠，機器人向前走
			{
				Motor.B.forward();
				Motor.C.forward();
			}//else if
			else if(distance<limit) //物體太近，機器人向後退
			{
				Motor.B.backward();
				Motor.C.backward();
			}//else if
		}//while
	}//main
}//Sample4_6
