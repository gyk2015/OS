package deviceManagement;

import java.util.Timer;
import java.util.TimerTask;

// 应用程序对象
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
	
	// 进程使用的设备和时间，单位是 秒 
	public void useDevice() {
		
//		usedDevice = device;
//		runTime = time;
		timer = new Timer(); 
		
		task = new TimerTask (){
			
			public void run() { 
				if( runTime==0 ) {
					
					System.out.println("进程[" + ID + "]执行完毕退出.");
					
					ReturnDevice(usedDevice);	
					
					cancel();
				}
				runTime--;
			}
		};
		DeviceManagement dm = DeviceManagement.getInstance();
		
		dm.OnProcessRun(this, usedDevice);
		
	}
	
	// 设备使用完毕之后，归还设备
	public void ReturnDevice( Device device ) {
		
		DeviceManagement dm = DeviceManagement.getInstance();
		
		dm.OnProcessStop(this);
		
		System.out.println("设备[" + device.getName() + "]释放.");
		
		usedDevice.Detach();
		runTime = 0;
		
	}

}

