package recutil;
import java.util.ArrayList;

/**
 * 脊
 */
public class SRidge{

	public static WeatherSystems getRidge(GridData height, int level, float scale) {
		// TODO Auto-generated method stub
		GridData curVor=VectorMathod.getCurvatureVor(height).mutiply(-1);   //计算曲率涡度，取反，因为脊区对应负的曲率涡度
		curVor.smooth(100);
	//	curVor.writeToFile("G:/data/systemIdentify/curVor.txt");
		VectorData wind = VectorMathod.getGeostrophicWind(height);          //计算地转风
	//	wind.writeToFile("G:/data/systemIdentify/wind.txt");
		ArrayList<Line> ridge = SystemIdentification.HighValueAreaRidge(wind,curVor,level);   //通过平流法获得脊线
		
		GridData marker=(curVor.add(-1.0f).sign01());   //曲率涡度阈值                             
		ridge=LineDealing.cutLines(ridge, marker); 
	//	LineDealing.writeToFile("G:/data/systemIdentify/markLine1.txt", ridge);
		
		
		GridData u = wind.u;
	//	u.writeToFile("G:/data/systemIdentify/windSpeed.txt");
		marker = marker.mutiply(u.add(-6.0f));    		//该类的方法侧重识别西风带中的脊线，需满足一定的西风风速条件
		ridge=LineDealing.cutLines(ridge, marker); 
	//	LineDealing.writeToFile("G:/data/systemIdentify/markLine2.txt", ridge);
		
		GridData adve=VectorMathod.getAdvection(wind, curVor);    //计算平流场
		GridData curOfAdve=VectorMathod.getCurvature(adve);
		marker = marker.mutiply(curOfAdve.add(-0.4f).mutiply(-1).sign01());  // 为避免线条过度弯曲
	//	curOfAdve.writeToFile("G:/data/systemIdentify/curOfAdve.txt");
		ridge=LineDealing.cutLines(ridge, marker); 
	//	LineDealing.writeToFile("G:/data/systemIdentify/markLine3.txt", ridge);
		
		
		ridge = SystemIdentification.getLongLine(scale,ridge);
		
		GridData ids = SystemIdentification.getCuttedRegion(curVor);  //以曲率涡度生成分区
		marker=(curVor.add(-1.0f).sign01());
		ids = ids.mutiply(marker);                                    //保留分区中的高阈值部分
	//	ids.writeToFile("G:/data/systemIdentify/ids.txt");
		
		WeatherSystems ridgeS = new WeatherSystems("ridge",level);  //定义系统，并将分区与轴线相互协调
	
		ridgeS.setAxes(ridge);
		ridgeS.setValue(curVor.mutiply(marker));
		ridgeS.setIds(ids);
		
		ridgeS.reset();
		
		return ridgeS;
	}
	
	

}
