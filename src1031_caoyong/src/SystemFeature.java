import java.util.HashMap;
import java.util.Map;

public class SystemFeature {
	
	public Point centrePoint = new Point(0,0);
	public Line axes = new Line();
	public Map<String,Float> features = new HashMap<String,Float>();
	
	public SystemFeature(Point point){
		centrePoint = point.copy();
	}
	public SystemFeature(Line line){
		axes = line.copy();
	}
	public SystemFeature() {
		// TODO Auto-generated constructor stub
	}
	
	public Point getCentrePoint(){
		return centrePoint;
	}
	public void setCentrePoint(Point point){
		centrePoint = point;
	}
	public void setAxes(Line line){
		axes = line;
	}
	public Line getAxes(){
		return axes;
	}
	
	public Float getFeature(String featureName){
		return features.get(featureName);
	}
	public void setFeature(String featureName, float value){
		features.put(featureName, value);
	}
	public void addValueForFeature(String featureName, float value){
		if(features.containsKey(featureName)){
			features.put(featureName, features.get(featureName)+value);
		}
		else{
			features.put(featureName, value);
		}
	}
}
