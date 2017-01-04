package filrmanage;

import javafx.application.Application;  
import javafx.scene.Node;  
import javafx.scene.Scene;  
import javafx.scene.control.TreeItem;  
import javafx.scene.control.TreeView;  
import javafx.scene.layout.StackPane;  
import javafx.stage.Stage;  
   
public class TreeViewSample extends Application {  
   
    public static void main(String[] args) {  
        launch(args);  
    }  
      
    @Override  
    public void start(Stage primaryStage) {  
    	
    	 primaryStage.setTitle("目-录");          
          
		 TreeItem<String> rootItem = new TreeItem<> ("root");//最顶层
	     
	     rootItem.setExpanded(true); //是否可以展开 
	     
	     Disk disk=new Disk();
	     disk.init();
	     String string=new String(disk.diskblocks[4].getBy());
	     byte[] oldby=disk.diskblocks[4].getBy();
		 for(int i=0;i<string.length();i+=8){
			 TreeItem<String> item = new TreeItem<> (string.substring(i,i+3));     			 
			 if(oldby[i+5]!=0){
				 this.showcatalog(item,oldby[i+5],disk);
			 }else{
				 
			 }
			 rootItem.getChildren().add(item);
	     }
	
	    TreeView<String> tree = new TreeView<> (rootItem);         
        StackPane root = new StackPane();  
        root.getChildren().add(tree);  
        primaryStage.setScene(new Scene(root, 300, 250));  
        primaryStage.show();  
        
    }  
    public TreeItem<String> showcatalog(TreeItem<String> items,byte startdisk,Disk disk){
    	String string=new String(disk.diskblocks[startdisk].getBy());
	    byte[] oldby=disk.diskblocks[startdisk].getBy();
	    for(int i=0;i<string.length();i+=8){
	    	TreeItem<String> item=null;
	    	if(oldby[i+3]==2){
	    		item = new TreeItem<> (string.substring(i,i+3)+".txt");
	    	}else{
	    		if(oldby[i+3]==3){
	    			item = new TreeItem<> (string.substring(i,i+3)+".e");
	    		}else{
	    			item = new TreeItem<> (string.substring(i,i+3));
	    		}
	    	}
	    	
			      			 
			 if(oldby[i+5]!=0&&oldby[i+3]!=2&&oldby[i+3]!=3){
				 this.showcatalog(item,oldby[i+5],disk);
			 }else{
				 
			 }
			 items.getChildren().add(item);
	     }
    	return items;
    }
}