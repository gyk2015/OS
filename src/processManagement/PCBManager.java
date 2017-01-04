package processManagement;

import java.util.LinkedList;

import memoryManage.MemoryManage;
import view.View;

public class PCBManager {
	private View view;
	private LinkedList<byte[]> blankQueue;
	private byte[] runningPCB = null,freePCB = null;
	private LinkedList<byte[]> readyQueue;
	private LinkedList<byte[]> blockedQueue;
	public PCBManager(View view){
		this.view = view;
		byte[][] PCBsArea = MemoryManage.getPCBsArea();
		blankQueue = new LinkedList<byte[]>();
		for(int i=0;i<PCBsArea.length-1;i++){
			blankQueue.add(PCBsArea[i]);
		}
		readyQueue = new LinkedList<byte[]>();
		blockedQueue = new LinkedList<byte[]>();
		freePCB = PCBsArea[PCBsArea.length-1];
		freePCB[0]=0;
		freePCB[1]=0;
		freePCB[2]=(byte) 0xff;
		freePCB[3]=(byte) 0xff;
		freePCB[4]=0;//空闲进程ID为0
		freePCB[5] = 1;
		runningPCB = freePCB;
	}
	
	public String getPCBsDetails(){
		StringBuilder details = new StringBuilder();
		details.append("【PCB状态】\n就绪队列：\n");
		if(readyQueue.size()==0){
			details.append("空\n");
		}else{
			for(byte[] PCB : readyQueue){
				details.append((0x00ff&PCB[4])+"  -  ");
			}
			details.append("\n");
		}
		details.append("\n\n阻塞队列：\n");
		if(blockedQueue.size()==0){
			details.append("空\n");
		}else{
			for(byte[] PCB : blockedQueue){
				details.append((0x00ff&PCB[4])+"  -  ");
			}
			details.append("\n");
		}
		return details.toString();
	}
	
	public byte[] getRunningPCB(){
		return runningPCB;
	}
	public byte[] createBlankPCB(){
		if(blankQueue.isEmpty()){
			return null;
		}
		return blankQueue.removeLast();
	}
	public boolean recyclePCB(byte[] PCB){
		if(blankQueue.size()<9){
			if(PCB==null){
				blankQueue.add(runningPCB);
				runningPCB=null;
			}else{
				blankQueue.add(PCB);
			}
			return true;
		}else{
			return false;
		}
	}
	
	public byte[] removeReadyPCB(){
		if(!readyQueue.isEmpty()){
			if((runningPCB = readyQueue.removeFirst())!=null){
//				refreshScreen();
			}
			return runningPCB;
		}
		else{
			return runningPCB = freePCB;
		}
	}
	public boolean addReadyPCB(byte[] PCB){
		if((0x00ff&PCB[4])==0){
			freePCB = PCB;
			return true;
		}else{
			if(readyQueue.add(PCB)){
				if(runningPCB!=null&&(0x00ff&runningPCB[4])==0){
					Processor.setPSW(Processor.PSW|0x04);
				}
	//			refreshScreen();
				return true;
			}
		}
		return false;
	}
	
	public byte[] removeBlockedPCB(int PID){
		
		if(!blockedQueue.isEmpty()){
			int index = 0;
			for(byte[] PCB : blockedQueue){
				if((0x00ff&PCB[4])==PID){
					blockedQueue.remove(index);
					//				refreshScreen();
					return PCB;
				}
				index++;
			}
		}
		System.out.println("PCBManager--removeBlockedPCB() error");
		return null;
	}
	public boolean addBlockedPCB(){
		if(blockedQueue.add(runningPCB)){
			runningPCB = null;
//			refreshScreen();
			return true;
		}else{
			return false;
		}
	}
}
