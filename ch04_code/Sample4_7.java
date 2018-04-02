import lejos.nxt.*;
import lejos.util.Delay;
import lejos.robotics.Colors;
class Sample4_7
{
	public static void main(String args[]) throws Exception
	{
		Button.ESCAPE.addButtonListener(new ButtonListener()
		{
			public void buttonPressed(Button b){System.exit(1);}
			public void buttonReleased(Button b){}
		});
		
		ColorLightSensor color = new ColorLightSensor(SensorPort.S1, ColorLightSensor.TYPE_COLORFULL); //�إ�ColorLightSensor����A�m�����O
		int red, green, blue, brightness;
		int [] values;
		Colors.Color col;
		
		while(true)
		{
			LCD.clear();
			brightness = color.getLightValue(); //Ū���G��
			col = color.readColor(); //Ū���C������
			values = color.getColor(); //Ū���U���C�⹡�M��
			
			red = values[0]; //���⹡�M��
			green = values[1]; //��⹡�M��
			blue = values[2]; //�Ŧ⹡�M��
			
			LCD.drawString("Brightness:" + brightness, 0, 0);
			LCD.drawString("Color:" + col.toString(), 0, 1);
			LCD.drawString("Red:" + red, 0, 2);
			LCD.drawString("Green:" + green, 0, 3);
			LCD.drawString("Blue:" + blue, 0, 4);
			Delay.msDelay(200);
		}//while
	}//main
}//Sample4_7
