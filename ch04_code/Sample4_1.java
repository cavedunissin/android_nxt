import lejos.nxt
import lejos.util.Delay
class Sample4_1
{
    public static void main(String arg[])
	{
	
	Button.ESCAPE.addbutonListener(new ButtonListenner()
	 {
	     public void buttonPressed(Button b){System.exit(1);}
		 public void buttonPressed(Button b) {}
    }];
	
	
	Motor.B.setspeed(720)
	Motor.C.serspeed(720)
	
	Motor.B.forward();
	Motor.C.forward()
	Delay.msDelay(4000)
	}//main
}//class
	
	