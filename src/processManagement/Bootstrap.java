package processManagement;

import java.awt.Color;
import java.util.Timer;

import javax.swing.JTree;

import filrmanage.Disk;
import filrmanage.Tree;
import memoryManage.MemoryManage;
import view.View;

public class Bootstrap {
	public static byte[] Memary;
	public static void main(String[] args) {
		Disk.getDisk().init();
		View view = View.getView();
		refreshTree(Disk.getDisk());
		refreshDisk(Disk.getDisk());
		MemoryManage memory = new MemoryManage(view);
		PCBManager  pcbManager =new PCBManager(view);
		ProcessControl pControler = new ProcessControl(pcbManager,view);
		Timer timer = new Timer();
		TimeCounter timeCounter =new TimeCounter(pControler);
		
		Processor processor = new Processor(pControler,timeCounter,view);
		view.setVisible(true);
		timer.scheduleAtFixedRate(timeCounter, 2000L, 2000L);
		processor.CPU();
		
		
	}

			public static void refreshTree(Disk disk){
					
				Tree tree=new Tree();
				View.getView().getPanel().removeAll();
				JTree jtree=tree.buildtree(disk);
				jtree.setBounds(10, 10, 170, 497);
				View.getView().getPanel().add(jtree);
				View.getView().getPanel().repaint();
			}

			public static void refreshDisk(Disk disk){
				for(int i=0;i<256;i++){
					if(disk.fileAllacationTable[i]!=0){
						View.getView().getHRPanels()[i].setBackground(Color.BLUE);
					}else{
						View.getView().getHRPanels()[i].setBackground(Color.RED);
					}
				}
			}
	
}
