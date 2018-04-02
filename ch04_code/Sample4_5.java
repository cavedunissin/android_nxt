import lejos.nxt.*;
import lejos.util.Delay;
class Sample4_5
{
	public static void main(String args[])
	{
		Button.ESCAPE.addButtonListener(new ButtonListener()
		{
			public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
		});
		LightSensor light = new LightSensor(SensorPort.S3); 
          //建立LightSensor物件，將光感應器接在輸入端3
		while(true)
		{
			LCD.clear(); //清除螢幕
			LCD.drawInt(light.readValue(), 0, 0); //顯示感應器讀值
			Delay.msDelay(200);
		}//while
	}//main
}//Sample4_5
