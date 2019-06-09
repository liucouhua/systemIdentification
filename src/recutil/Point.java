package recutil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Point implements Serializable{
    public	float ptLon,ptLat;
    public  float ptVal;
    
    public  Point(float ptLon,float ptLat)
    {
    	this.ptLon=ptLon;
    	this.ptLat=ptLat;
    	ptVal=0.0f;
    }
    
    public Point(float ptLon,float ptLat,float ptVal)
    {
    	this.ptLon=ptLon;
    	this.ptLat=ptLat;
    	this.ptVal=ptVal;
    }
    public void setValue(float value){
    	this.ptVal = value;
    }
    
	public Point copy() {
		// TODO �Զ����ɵķ������
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);
			try {
				return (Point) ois.readObject();
			} catch (ClassNotFoundException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		return null;
	}
}
