/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
	 * 
	 * @param index 进程控制块标识符
	 * @param size  内存大小
	 * @param fileName 文件所在路径
	 * @return
	 * @throws IOException
	 */
package memoryManage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import view.View;
/**
 *
 * @author lenovo
 */
public class MemoryManage {

    private static total_Size total = new total_Size();
    private static byte[][] PCBsArea = new byte[10][6];
    private static List<process> jobs = new ArrayList<process>();
    private static View view;
    public MemoryManage(View view){
        this.view = view;
    }
    public static total_Size getTotal_Size(){
    	return total;
    }
    public static byte[][] getPCBsArea(){
        return PCBsArea;
    }
    //申请内存
    public static int reqMemory(int index,int size,int exeFileIndex){
        process job = new process(index,size,exeFileIndex);
        List<Size> lsize = total.getList();
        sort(lsize);
        int i;
        for(i=0;i< lsize.size();i++){
            Size asize  = lsize.get(i);
            if(asize.getState()<=-1){
                if(asize.getSpace()>=job.getSize()){
                    System.out.println("内存申请成功");
                    jobs.add(job);
                    total.alloc(i, job);
                    view.getHR_mainMemoryDetail().setText(memoryList());
                    break;
                }
            }
        }
        if(i>=lsize.size()){
            System.out.println("内存申请失败");
            return -1;
        }
        return lsize.get(i).getStart();
    }
    //释放内存
    public static void relMemory(int start){
    	memoryList();
        List<Size> lsize = total.getList();
        int i ;
        int index = -1;
        for(i=0;i<lsize.size();i++){
            if(lsize.get(i).getStart()== start){
                int j ;
                for(j=0;j<jobs.size();j++){
                    if(jobs.get(j).getIndex()==lsize.get(i).getState()){
                        index = jobs.get(j).getIndex();
                        total.search(jobs.get(j));
                        jobs.remove(j);
                        System.out.println(index+"内存释放成功");
                        view.getHR_mainMemoryDetail().setText(memoryList());
                        return;
                    }
                }
            }
        }
        System.out.println(index+"内存释放失败");
    }
   
    public static void sort(List<Size> lsize){
        Collections.sort(lsize, new Comparator<Size>(){
	        @Override 
	        public int compare(Size t, Size t1){
	            return t.getStart()-t1.getStart();
	        }
	    });
    }
    
    public static String memoryList(){
    	String title = "【内存分区信息】\n起始地址---终止地址---使用情况---\n";
        System.out.println("内存分区信息：");
        System.out.println("起始地址---终止地址---状态---");
        if(total.getList().size()!=0)
        {
            sort(total.getList());
            return title+total.print();
        }
        else{
        	System.out.println("当前用户区没有进程");
        	return title+"\n";
        }
            
        
    }
  
    
}

