package code;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


public class Line implements Serializable{
	public float value;
	public ArrayList<float[]> point;
	public boolean iscycle;
	public float lenght;
	public Line(){
		point=new ArrayList<float[]>();
	}
	
	public float getLenght(){
		return lenght;
	}
	
	public void setLenght(){
		ArrayList point =this.point;
		lenght=0;
		for(int j=0;j<point.size()-1;j++){
			float[]x=(float[]) point.get(j);
			float[]y=(float[]) point.get(j+1);
			lenght+=MyMath.dis(x[0], x[1],y[0], y[1]);
		}
	}
	
	public Line copy() {
		// TODO �Զ����ɵķ������
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);
			try {
				return (Line) ois.readObject();
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
