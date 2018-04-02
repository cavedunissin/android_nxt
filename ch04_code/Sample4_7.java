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
		
		ColorLightSensor color = new ColorLightSensor(SensorPort.S1, ColorLightSensor.TYPE_COLORFULL); //建立ColorLightSensor物件，彩色類別
		int red, green, blue, brightness;
		int [] values;
		Colors.Color col;
		
		while(true)
		{
			LCD.clear();
			brightness = color.getLightValue(); //讀取亮度
			col = color.readColor(); //讀取顏色類型
			values = color.getColor(); //讀取各個顏色飽和度
			
			red = values[0]; //紅色飽和度
			green = values[1]; //綠色飽和度
			blue = values[2]; //藍色飽和度
			
			LCD.drawString("Brightness:" + brightness, 0, 0);
			LCD.drawString("Color:" + col.toString(), 0, 1);
			LCD.drawString("Red:" + red, 0, 2);
			LCD.drawString("Green:" + green, 0, 3);
			LCD.drawString("Blue:" + blue, 0, 4);
			Delay.msDelay(200);
		}//while
	}//main
}//Sample4_7
