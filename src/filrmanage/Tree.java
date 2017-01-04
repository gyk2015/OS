package filrmanage;

import javafx.scene.control.TreeItem;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;


public class Tree {
	public Tree(){
		
	}
	public JTree buildtree(Disk disk){
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("root");
		 String string=new String(disk.diskblocks[4].getBy());
	     byte[] oldby=disk.diskblocks[4].getBy();
	     for(int i=0;i<string.length();i+=8){
			 DefaultMutableTreeNode node = new DefaultMutableTreeNode(string.substring(i,i+3));
			 if(oldby[i+5]!=0){
				 this.showcatalog(node,oldby[i+5],disk);
			 }else{
				 
			 }
			 top.add(node);
	     }

	    JTree tree = new JTree(top);
	    return tree;
	}
	public DefaultMutableTreeNode showcatalog(DefaultMutableTreeNode items,byte startdisk,Disk disk){
    	String string=new String(disk.diskblocks[startdisk].getBy());
	    byte[] oldby=disk.diskblocks[startdisk].getBy();
	    for(int i=0;i<string.length();i+=8){
	    	DefaultMutableTreeNode item=null;
	    	if(oldby[i+3]==2){
	    		item =new DefaultMutableTreeNode(string.substring(i,i+3)+".txt");
	    	}else{
	    		if(oldby[i+3]==3){
	    			item =new DefaultMutableTreeNode(string.substring(i,i+3)+".e");
	    		}else{
	    			item = new DefaultMutableTreeNode(string.substring(i,i+3));
	    		}
	    	}
	    	
			      			 
			 if(oldby[i+5]!=0&&oldby[i+3]!=2&&oldby[i+3]!=3){
				 this.showcatalog(item,oldby[i+5],disk);
			 }else{
				 
			 }
			 items.add(item);
	     }
    	return items;
    }
}


