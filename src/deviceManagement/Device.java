package deviceManagement;


// 设备对象
public class Device {
	
	private String Name;
	
	public Device(String name){
		Name = name;
	}
	
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
	
	public void Detach() {
		
		Name = "";
		
	}

	@Override
	public String toString() {
		return "Device [Name=" + Name + "]";
	}

}

