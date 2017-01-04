package processManagement;

import deviceManagement.DeviceManagement;
import filrmanage.Disk;
import memoryManage.MemoryManage;
import memoryManage.total_Size;
import view.View;
import memoryManage.Size;

public class ProcessControl {
	private PCBManager pcbManager;
	private View view;
	public ProcessControl(PCBManager pcbManager,View view){
		this.pcbManager = pcbManager;
		this.view = view;
	}
	public String getPCBsDetails(){
		return pcbManager.getPCBsDetails();
	}
	public boolean create(){
		byte[] newPCB;
		int pAddr=0,size = 0;
		if((newPCB = pcbManager.createBlankPCB()) == null){
			System.out.println("申请空白进程  失败");
			return false;
		}
		byte PID = (byte)((Math.random()*255)+1);
		int exeFileIndex = (int)(Math.random()*6)+1;
	//	int exeFileIndex = 2;
		System.out.println("当前可执行文件随机数"+exeFileIndex+"返回的文件字节大小"+Disk.getexesize(exeFileIndex));
		if((size = Disk.getexesize(exeFileIndex)) <=0){
			System.out.println("可执行文件大小异常");
			pcbManager.recyclePCB(newPCB);
			return false;
		}
		if((pAddr = MemoryManage.reqMemory(0x00ff&PID, size, exeFileIndex))==-1){//(pAddr=applyMemery())<0){
			System.out.println("申请主存空间  失败");
			pcbManager.recyclePCB(newPCB);
			return false;
		}
//		if(false){
//			return false;//无法获取主存空间返回false
//		}
//		PCB[0]---AX寄存器
		//PCB[1]---PC程序计数器
//			PCB[2]---进程首地址1
//			PCB[3]---进程首地址2
		//PCB[4]---PID
		//PCB[5]---state
		newPCB[0] = 0;
		newPCB[1] = 0;
		newPCB[3] = (byte)pAddr;
		newPCB[2] = (byte)(pAddr>>>8);
		newPCB[4] = PID;
		newPCB[5] = 1;
		return pcbManager.addReadyPCB(newPCB);
	}
	public boolean destroy(){
//		showResult();
		MemoryManage.relMemory(Processor.pAddress);
		if(!pcbManager.recyclePCB(null)){
			return false;
		}
		return true;
	}
	public boolean block(){
		byte[] runningPCB = pcbManager.getRunningPCB();
		if(runningPCB==null) return false;
		runningPCB[0] = Processor.AX;
		runningPCB[1] = Processor.PC;
		runningPCB[5] = 0;//0--阻塞态；1---就绪态；2---运行态
		return pcbManager.addBlockedPCB();
	}
	public boolean awake(){
		byte[] PCB;
		int PID = DeviceManagement.getInstance().deleteFinishedProcess();
		if((PCB = pcbManager.removeBlockedPCB(PID)) != null){
			PCB[5] = 1;
			pcbManager.addReadyPCB(PCB);
			Processor.setPSW((0x00ff&Processor.PSW)-0x08);
			
			return true;
		}else{
			System.out.println("ProcessControl--awake() error");
			return false;
		}
	}
	public boolean readyToRunning(){//用于
		byte[] runningPCB;
		if((runningPCB = pcbManager.removeReadyPCB()) != null){
			Processor.AX = runningPCB[0];
			Processor.PC = runningPCB[1];
			Processor.PID = runningPCB[4];
			runningPCB[5] = 2;
			int pAddr = 0;
			Processor.pAddress = ((pAddr|runningPCB[2])<<8)|runningPCB[3];
			total_Size total =MemoryManage.getTotal_Size();
		    for(Size aSize : total.getList()){
		    	if(aSize.getStart()==Processor.pAddress){
		    		Processor.currentSize = aSize;
		            break;
		        }
		    }
			return true;
		}else{
			return false;
		}
	}
	public boolean runningToReady(){//用于
		byte[] runningPCB = pcbManager.getRunningPCB();
		if(runningPCB==null) return false;
		runningPCB[0] = Processor.AX;
		runningPCB[1] = Processor.PC;
		runningPCB[5] = 1;//设为就绪态
		if(pcbManager.addReadyPCB(runningPCB)){
			runningPCB=null;
			return true;
		}else{
			return false;
		}
	}
}
