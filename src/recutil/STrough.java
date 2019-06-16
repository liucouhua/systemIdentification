package recutil;
import java.util.ArrayList;

/**
 * 妲�
 */
public class STrough{
	
	public static WeatherSystems getTrough(GridData height, int level, float scale) {
		
		String output_dir = "D:\\develop\\java\\201905-weahter_identification\\output\\";
		
		GridData curVor=VectorMathod.getCurvatureVor(height);  //璁＄畻鏇茬巼娑″害锛屽洜涓鸿剨鍖哄搴旀鐨勬洸鐜囨丁搴�
		curVor.smooth(30);
	//	curVor.writeToFile("G:/data/systemIdentify/curVor.txt");
		VectorData wind = VectorMathod.getGeostrophicWind(height);   //璁＄畻鍦拌浆椋�
	//	wind.writeToFile("G:/data/systemIdentify/wind.txt");
		ArrayList<Line> trough = SystemIdentification.HighValueAreaRidge(wind,curVor,level);  //閫氳繃骞虫祦娉曡幏寰楁Ы绾�
		
		
		GridData marker=(curVor.add(-0.5f).sign01());   //鏇茬巼娑″害闃堝��
		
	
		trough=LineDealing.cutLines(trough, marker); 
		//LineDealing.writeToFile(output_dir + "markLine1.txt", trough);
		
	
		GridData windSpeed = VectorMathod.getMod(wind);
	//	windSpeed.writeToFile("G:/data/systemIdentify/windSpeed.txt");
		marker = marker.mutiply(windSpeed.add(-4.0f));    //椋庨�熼槇鍊�
		trough=LineDealing.cutLines(trough, marker); 
	//	LineDealing.writeToFile("G:/data/systemIdentify/markLine2.txt", trough);
		
		GridData adve=VectorMathod.getAdvection(wind, curVor);    //璁＄畻骞虫祦鍦�
		GridData curOfAdve=VectorMathod.getCurvature(adve);
		marker = marker.mutiply(curOfAdve.add(-0.4f).mutiply(-1).sign01());  // 閫嗘椂閽堝集鏇插害闃堝��
	//	curOfAdve.writeToFile("G:/data/systemIdentify/curOfAdve.txt");
		trough=LineDealing.cutLines(trough, marker); 
	//	LineDealing.writeToFile("G:/data/systemIdentify/markLine3.txt", trough);
		
		
		trough = SystemIdentification.getLongLine(scale,trough);
		
		
		GridData ids = SystemIdentification.getCuttedRegion(curVor);  //浠ユ洸鐜囨丁搴︾敓鎴愬垎鍖�
		marker=(curVor.add(-0.05f).sign01());
		
		
		
		ids = ids.mutiply(marker);  //淇濈暀鍒嗗尯涓殑楂橀槇鍊奸儴鍒�
	//	ids.writeToFile("G:/data/systemIdentify/ids.txt");
		
		
		WeatherSystems troughS = new WeatherSystems("trough",level);  //瀹氫箟绯荤粺锛屽苟灏嗗垎鍖轰笌杞寸嚎鐩镐簰鍗忚皟
	
		troughS.setAxes(trough);
		troughS.setValue(curVor.mutiply(marker));
		troughS.setIds(ids);
		
		troughS.reset();
		
		return troughS;
	}
	
	
}
