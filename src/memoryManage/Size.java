/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package memoryManage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import filrmanage.Disk;
/**
 *
 * @author lenovo
 */
//���õ�������
public class Size {
    //start address
    private int start;
    private int space;//space
    private int states = -1;//state (-1��δ���� )
    private ArrayList<String>  ins = new ArrayList<String>();
    
   public Size(int start ,int  space ){
       
       this.start =  start;
       this.space = space;
   }
   public Size(int start, int space, int states){
       this.start = start;
       this.space = space;
       this.states =  states;
       
   }
   public int getStart(){
       return start;
   }
   public void setStart(int start){
       this.start = start;
   }
   public int getSpace(){
       return space;
   }
   public void setSpace(int space){
       this.space = space;
   }
   public int getState(){
       return states;
   }
   public void setState(int states){
       this.states =  states;
       
   }
   public void setIns(int exeFileIndex){
	   byte[] exeFile;
	   exeFile=Disk.getexefile(exeFileIndex);
	   for(int i=0;i<exeFile.length;i++){
		   System.out.println("ָ�"+(0x00ff&exeFile[i]));
		   ins.add(i, exeFile[i]+"");
	   }
   }
   public byte getIns(int i){
       if(i>=space){
           System.out.println("�����ڴ�߽�");
           return -1;
       }
       else if(ins.size() ==0 ){
    	   System.out.println("��������û��ָ��");
           return -1;
       }   
       else
           return new Byte(ins.get(i));
   }
   @Override
   public String toString(){
	   if(states==-1){
		   return "   "+String.format("%-17d", start)+String.format("%-15d",start+space-1)+"����"+"\n";
	   }else{
		   return "   "+String.format("%-17d", start)+String.format("%-15d",start+space-1)+"����"+states+"\n";
	   }
       
   }
}

