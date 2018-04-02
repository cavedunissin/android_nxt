package com.cavedu.android.computerBT;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataOutputStream;
import java.io.IOException;


import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;

public class ComputerBT extends JFrame{
	
	static StreamConnectionNotifier stcNo = null;
	static StreamConnection stconnection = null;
	static int command ;
    public static final String myUUID = "04c6093b00001000800000805f9b34fb";
    public static final int FORWARD = 1;
    public static final int TURN_LEFT = 2;
    public static final int TURN_RIGHT = 3;
    public static final int BACKWARD = 4;
    public static final int TAKE_PICTURE = 5;
    public static final int MOVE_CAMERA = 6;
    public static final int SAY_HELLO = 7;
    static DataOutputStream mOutputStream;
    static DataOutputStream nxtOuputStream;

    JButton btnForward;
    JButton btnBackward;
    JButton btnLeft;
    JButton btnRight;
    JButton btnPicture;
    JButton connectToNXT;
    JButton sayHello;
	/**
	 * @param args
	 */
    public ComputerBT(){
    	
    	this.setSize(320,340);
    	this.setResizable(false);
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.getContentPane().setLayout(null);
    	this.setLocationRelativeTo(null);
    	
    	connectToNXT = new JButton("NXT"); //宣告JButton
    	connectToNXT.setBounds(0,0, 100, 100); //設定大小位置
    	connectToNXT.addMouseListener(new MouseAction(nxtOuputStream,MOVE_CAMERA));//加入滑鼠事件
    	this.getContentPane().add(connectToNXT); //將按鈕加入視窗
    	
    	btnPicture = new JButton("Click"); //宣告JButton
    	btnPicture.setBounds(200, 0, 100, 100); //設定大小位置
    	btnPicture.addMouseListener(new MouseAction(mOutputStream,TAKE_PICTURE ));//加入滑鼠事件
    	this.getContentPane().add(btnPicture); //將按鈕加入視窗
    	
    	btnBackward = new JButton("↑"); //宣告JButton
    	btnBackward.setBounds(100, 0, 100, 100); //設定大小位置
    	btnBackward.addMouseListener(new MouseAction(nxtOuputStream,FORWARD));//加入滑鼠事件
    	this.getContentPane().add(btnBackward); //將按鈕加入視窗
    	
    	btnForward = new JButton("↓"); //宣告JButton
    	btnForward.setBounds(100,100, 100, 100); //設定大小位置
    	btnForward.addMouseListener(new MouseAction(nxtOuputStream,BACKWARD));//加入滑鼠事件
    	this.getContentPane().add(btnForward); //將按鈕加入視窗
    	
    	btnLeft = new JButton("←"); //宣告JButton
    	btnLeft.setBounds(0,100, 100, 100); //設定大小位置
    	btnLeft.addMouseListener(new MouseAction(nxtOuputStream,TURN_LEFT));//加入滑鼠事件
    	this.getContentPane().add(btnLeft); //將按鈕加入視窗
    	
    	btnRight = new JButton("→"); //宣告JButton
    	btnRight.setBounds(200,100, 100, 100); //設定大小位置
    	btnRight.addMouseListener(new MouseAction(nxtOuputStream,TURN_RIGHT));//加入滑鼠事件
    	this.getContentPane().add(btnRight); //將按鈕加入視窗
    	
    	sayHello = new JButton("Say Hello"); //宣告JButton
    	sayHello.setBounds(0,200, 300, 100); //設定大小位置
    	sayHello.addMouseListener(new MouseAction(mOutputStream,SAY_HELLO));//加入滑鼠事件
    	this.getContentPane().add(sayHello); //將按鈕加入視窗
    	
    	this.setVisible(true);
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConnectWitAndroid();
		//用new來執行建構子，但要在與手機連線建立後才會出現
		ComputerBT CBT = new ComputerBT();
	}//main	
	public static void ConnectWitAndroid(){
		//主程式建立與Android手機的藍芽連線
		LocalDevice localDevice ;
		try {
			localDevice = LocalDevice.getLocalDevice();
			//設定 access code 為 General/Unlimited Inquiry Access Code (GIAC).
			localDevice.setDiscoverable(DiscoveryAgent.GIAC);
			UUID uuid = new UUID(myUUID, false);
			String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
			System.out.println("Waiting for Android to connect...");
			stcNo = (StreamConnectionNotifier) Connector.open(url);
			//程式會在此等待手機連線近來
			stconnection = stcNo.acceptAndOpen();
			System.out.println("Connect to Android Success!");
			mOutputStream = stconnection.openDataOutputStream();
			//手機連線建立完成
			System.out.println("Connect to NXT ...");
			NXTConnector conn = new NXTConnector();
			if(!conn.connectTo("kevin", "", NXTCommFactory.BLUETOOTH)){
				JOptionPane.showMessageDialog(null, "Cannot connect to NXT","Error", JOptionPane.ERROR_MESSAGE); //連線失敗訊息
				System.exit(1);
			}//if
			nxtOuputStream = conn.getDataOut();
			System.out.println("Connect to NXT success!!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}//JRFrame
//定義面板觸發事件
class MouseAction implements MouseListener{
	DataOutputStream mmOutputStream = null;
	DataOutputStream mNXTDataOu = null;
	int commandNumber;
	public static final int STOP = 9;
	
	MouseAction(DataOutputStream DATAOu,int cmd){
			commandNumber = cmd;
			if(commandNumber == 5 ||commandNumber == 7){
				mmOutputStream = DATAOu;
			}
			else{
				mNXTDataOu = DATAOu;
			}
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		//點擊按鈕拍照 
		if(commandNumber == 5 || commandNumber == 7){
			try {
				//此為手機的串流
				mmOutputStream.writeInt(commandNumber);
				mmOutputStream.flush();
				if(commandNumber == 5){
					System.out.println("Take the picture");
				}
				else{
					System.out.println("Say Hello");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(commandNumber!=5 && commandNumber!=7){
			try {
				//此為NXT的串流
				mNXTDataOu.writeInt(commandNumber);
				mNXTDataOu.flush();
				System.out.println("Robot move, action number is "+commandNumber);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}//mousePressed
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(commandNumber!=5 && commandNumber!=7){
			try {
				//此為NXT的串流
				mNXTDataOu.writeInt(STOP);
				mNXTDataOu.flush();
				System.out.println("Robot Stop");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
}//MouseListener()

