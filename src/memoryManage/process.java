/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package memoryManage;

/**
 *
 * @author lenovo
 */
public class process {
   private int size;
   private int index;
   private int exeFileIndex;
   
   public process (int index,int size, int exeFileIndex){
       this.index = index;
       this.size = size;
       this.exeFileIndex = exeFileIndex;
   }
   public int getIndex(){
       return index;
   }
   public void setIndex(int index){
       this.index = index;
   }
   public int getSize(){
       return size;
   }
   public void setSize(int size){
       this.size = size;
   }
   public int getExeFileIndex(){
       return exeFileIndex;
   }
   public void setExeFileIndex(int exeFileIndex){
       this.exeFileIndex = exeFileIndex;
   }
   @Override
   public String toString(){
       return "JOB(" +"index="+index+"size="+size+",filename="+index+")";
   }
   
}
