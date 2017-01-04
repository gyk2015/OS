package deviceManagement;

import java.util.Timer;
import java.util.TimerTask;

// Ӧ�ó������
public class Process{// extends Thread
	
	private int ID;
	
	private Device usedDevice;
	
	private int runTime;
	
	Timer timer;
	TimerTask task;
	public Process(int ID, Device device, int time ){
		this.ID = ID;
		this.usedDevice = device;
		this.runTime = time;
	}
//	@Override
//	public void run(){
//		useDevice();
//	}
	
	public int getRunTime(){
		return runTime;
	}
	
	public void setRunTime(int time){
		this.runTime = time;
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	@Override
	public String toString() {
		return "Process [Name=" + ID + "]";
	}
	
	// ����ʹ�õ��豸��ʱ�䣬��λ�� �� 
	public void useDevice() {
		
//		usedDevice = device;
//		runTime = time;
		timer = new Timer(); 
		
		task = new TimerTask (){
			
			public void run() { 
				if( runTime==0 ) {
					
					System.out.println("����[" + ID + "]ִ������˳�.");
					
					ReturnDevice(usedDevice);	
					
					cancel();
				}
				runTime--;
			}
		};
		DeviceManagement dm = DeviceManagement.getInstance();
		
		dm.OnProcessRun(this, usedDevice);
		
	}
	
	// �豸ʹ�����֮�󣬹黹�豸
	public void ReturnDevice( Device device ) {
		
		DeviceManagement dm = DeviceManagement.getInstance();
		
		dm.OnProcessStop(this);
		
		System.out.println("�豸[" + device.getName() + "]�ͷ�.");
		
		usedDevice.Detach();
		runTime = 0;
		
	}

}

