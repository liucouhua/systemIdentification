package code;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GridInfo implements Serializable{
	public String name;
	public float startlon,endlon,dlon,startlat,endlat,dlat;
	public int nlon,nlat;
	
	public GridInfo(){
		startlon=0;
		endlon=0;
		dlon=0;
		startlat=0;
		endlat=0;
		dlat=0;
		nlon=0;
		nlat=0;
	}
	
	
	//格点信息类的构造函数
	public GridInfo(int nlon,int nlat,float startlon,float startlat,float dlon,float dlat){
		this.startlon=startlon;
		this.dlon=dlon;
		this.startlat=startlat;
		this.dlat=dlat;
		this.nlon=nlon;
		this.nlat=nlat;
		this.endlon=startlon+(nlon-1)*dlon;
		this.endlat=startlat+(nlat-1)*dlat;
	}
	
	public int getnlon(){
		if(dlon!=0){
			return (int) ((endlon-startlon)/dlon);
		}
		else return 0;
	}
	public int getnlat(){
		if(dlat!=0){
			return (int) ((endlat-startlat)/dlat);
		}
		else return 0;
	}
	public float getendlon(){
		return startlon+(nlon-1)*dlon;
	}
	
	public float getendlat(){
		return startlat+(nlat-1)*dlat;
	}
	
	
	public GridInfo copy(){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);
			try {
				return (GridInfo) ois.readObject();
			} catch (ClassNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return null;

	  }
	
}
