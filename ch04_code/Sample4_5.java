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
          //�إ�LightSensor����A�N���P�������b��J��3
		while(true)
		{
			LCD.clear(); //�M���ù�
			LCD.drawInt(light.readValue(), 0, 0); //��ܷP����Ū��
			Delay.msDelay(200);
		}//while
	}//main
}//Sample4_5
