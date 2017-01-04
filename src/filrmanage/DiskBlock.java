package filrmanage;


public class DiskBlock implements java.io.Serializable{
	private byte[] by=new byte[64];



	public byte[] getBy() {
		return by;
	}

	public void setBy(byte[] by) {
		this.by = by;
	}
	public DiskBlock(byte[] by){
		this.by=by;
	}
	public DiskBlock(){
		
	}

}
