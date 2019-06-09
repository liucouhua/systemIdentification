package code;
import java.util.ArrayList;

public class STrough{
	
	public static WeatherSystems getTrough(GridData height, int level, float scale) {
		
		GridData curVor=VectorMathod.getCurvatureVor(height);  //计算曲率涡度，因为槽区对应正的曲率涡度
		curVor.smooth(100);
	//	curVor.writeToFile("G:/data/systemIdentify/curVor.txt");
		VectorData wind = VectorMathod.getGeostrophicWind(height);   //计算地转风
	//	wind.writeToFile("G:/data/systemIdentify/wind.txt");
		ArrayList<Line> trough = SystemIdentification.HighValueAreaRidge(wind,curVor,level);  //通过平流法获得槽线
		
		GridData marker=(curVor.add(-0.5f).sign01());   //曲率涡度阈值
		trough=LineDealing.cutLines(trough, marker); 
	//	LineDealing.writeToFile("G:/data/systemIdentify/markLine1.txt", trough);
		
		
		GridData windSpeed = VectorMathod.getMod(wind);
	//	windSpeed.writeToFile("G:/data/systemIdentify/windSpeed.txt");
		marker = marker.mutiply(windSpeed.add(-4.0f));    //风速阈值
		trough=LineDealing.cutLines(trough, marker); 
	//	LineDealing.writeToFile("G:/data/systemIdentify/markLine2.txt", trough);
		
		GridData adve=VectorMathod.getAdvection(wind, curVor);    //计算平流场
		GridData curOfAdve=VectorMathod.getCurvature(adve);
		marker = marker.mutiply(curOfAdve.add(-0.4f).mutiply(-1).sign01());  // 逆时针弯曲度阈值
	//	curOfAdve.writeToFile("G:/data/systemIdentify/curOfAdve.txt");
		trough=LineDealing.cutLines(trough, marker); 
	//	LineDealing.writeToFile("G:/data/systemIdentify/markLine3.txt", trough);
		
		
		trough = SystemIdentification.getLongLine(scale,trough);
		
		GridData ids = SystemIdentification.getCuttedRegion(curVor);  //以曲率涡度生成分区
		marker=(curVor.add(-0.05f).sign01());
		ids = ids.mutiply(marker);  //保留分区中的高阈值部分
	//	ids.writeToFile("G:/data/systemIdentify/ids.txt");
		
		WeatherSystems troughS = new WeatherSystems("trough",level);  //定义系统，并将分区与轴线相互协调
	
		troughS.setAxes(trough);         //将前面识别的槽线设置为天气系统的轴线
		troughS.setValue(curVor.mutiply(marker));  //将曲率涡度设置为天气系统的特征变量，其中保留曲率涡度较高的部分
		troughS.setIds(ids); //以分区Ids代表每个天气系统的覆盖范围
		
		troughS.reset();   //将系统轴线同系统的分区id进行匹配
		
		return troughS;
	}
	
	
}
