package code;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SJet{
	
	//设置急流的速度阈值
	public static float getJetSpeed(int level){
		if(level >= 850){
			return 12.0f;  //850hpa以下设置为12.0m/s
		}
		else if(level == 700){
			return 16.0f;  //700hpa设置为16m/s
		}
		else{
			return 30.0f;  //700hpa以上设置为30m/s
		}
	}
	
	
	public static WeatherSystems getJet(VectorData wind, int level, float scale) {
		// TODO Auto-generated method stub
		WeatherSystems jet = new WeatherSystems("jet",level);  
		float jetSpeed = getJetSpeed(level);
		GridData speed = VectorMathod.getMod(wind);  //风速- 急流风速阈值
		speed.smooth(3);
		//speed.writeToFile("G:/data/systemIdentify/speed.txt");
		GridData v = wind.v.copy();
		GridData marker = speed.add(-jetSpeed).sign01();  // 风速大于阈值的部分将被保留
		
		//700hpa 以下只保留偏南风急流，侧重水汽输送和动力辐合
		//500hpa及以上急流讨论的是急流出口和入口区对涡度平流的差异导致的动力强迫，因此不区分南北风
		if(level>=700) marker =marker.mutiply(v.sign01());  
		
		//marker.writeToFile("G:/data/systemIdentify/marker.txt");
		speed = speed.mutiply(marker);   //保留急流区的风速
		
		//以垂直急流的方向对风速进行平流
		VectorData rw = VectorMathod.rotate(wind,-90); 
		ArrayList<Line> jetLine = SystemIdentification.HighValueAreaRidge(rw,speed,level);
		
		jetLine = LineDealing.cutLines(jetLine, marker);   //用风速和风向条件对急流轴线进行裁剪
		jetLine = SystemIdentification.getLongLine(scale,jetLine);  //保留大雨scale的急流
		LineDealing.smoothLines(jetLine, 10);   // 对线条进行平滑
		//LineDealing.writeToFile("G:/data/systemIdentify/jetLine.txt",jetLine);
		GridData ids = SystemIdentification.getCuttedRegion(speed);              //根据速度进行初步分区
		ids = SystemIdentification.combineStrongConnectingRegion_2d(ids,2.0f);   //将紧邻的分区进行合并
		
		//ids.writeToFile("G:/data/systemIdentify/ids.txt");
		
		jet.setAxes(jetLine);  // 将天气系统的轴线属性设置为jetLine
		jet.setValue(speed);  //将天气系统的value 属性设置为speed
		jet.setIds(ids);      // 将天气学系统的分区设置为ids
		jet.reset();         //将分区设置与轴线设置进行匹配
		return jet;
	}


}
