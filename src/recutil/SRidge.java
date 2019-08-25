package recutil;
import java.util.ArrayList;

/**
 * 鑴�
 */
public class SRidge{

	public static WeatherSystems getRidge(GridData height, int level, float scale) {
		// TODO Auto-generated method stub
		GridData curVor=VectorMathod.getCurvatureVor(height).mutiply(-1);   //璁＄畻鏇茬巼娑″害锛屽彇鍙嶏紝鍥犱负鑴婂尯瀵瑰簲璐熺殑鏇茬巼娑″害
		curVor.smooth(100);
	//	curVor.writeToFile("G:/data/systemIdentify/curVor.txt");
		VectorData wind = VectorMathod.getGeostrophicWind(height);          //璁＄畻鍦拌浆椋�
	//	wind.writeToFile("G:/data/systemIdentify/wind.txt");
		ArrayList<Line> ridge = SystemIdentification.HighValueAreaRidge(wind,curVor,level);   //閫氳繃骞虫祦娉曡幏寰楄剨绾�
		
		GridData marker=(curVor.add(-1.0f).sign01());   //鏇茬巼娑″害闃堝��                             
		ridge=LineDealing.cutLines(ridge, marker); 
	//	LineDealing.writeToFile("G:/data/systemIdentify/markLine1.txt", ridge);
		
		
		GridData u = wind.u;
	//	u.writeToFile("G:/data/systemIdentify/windSpeed.txt");
		marker = marker.mutiply(u.add(-6.0f));    		//璇ョ被鐨勬柟娉曚晶閲嶈瘑鍒タ椋庡甫涓殑鑴婄嚎锛岄渶婊¤冻涓�瀹氱殑瑗块椋庨�熸潯浠�
		ridge=LineDealing.cutLines(ridge, marker); 
	//	LineDealing.writeToFile("G:/data/systemIdentify/markLine2.txt", ridge);
		
		GridData adve=VectorMathod.getAdvection(wind, curVor);    //璁＄畻骞虫祦鍦�
		GridData curOfAdve=VectorMathod.getCurvature(adve);
		marker = marker.mutiply(curOfAdve.add(-0.4f).mutiply(-1).sign01());  // 涓洪伩鍏嶇嚎鏉¤繃搴﹀集鏇�
	//	curOfAdve.writeToFile("G:/data/systemIdentify/curOfAdve.txt");
		ridge=LineDealing.cutLines(ridge, marker); 
	//	LineDealing.writeToFile("G:/data/systemIdentify/markLine3.txt", ridge);
		
		
		ridge = SystemIdentification.getLongLine(scale,ridge);
		
		GridData ids = SystemIdentification.getCuttedRegion(curVor);  //浠ユ洸鐜囨丁搴︾敓鎴愬垎鍖�
		marker=(curVor.add(-1.0f).sign01());
		ids = ids.mutiply(marker);                                    //淇濈暀鍒嗗尯涓殑楂橀槇鍊奸儴鍒�
	//	ids.writeToFile("G:/data/systemIdentify/ids.txt");
		
		WeatherSystems ridgeS = new WeatherSystems("ridge",level);  //瀹氫箟绯荤粺锛屽苟灏嗗垎鍖轰笌杞寸嚎鐩镐簰鍗忚皟
		ridgeS.type = "脊线";
		
		ridgeS.setAxes(ridge);
		ridgeS.setValue(curVor.mutiply(marker));
		ridgeS.setIds(ids);
		
		ridgeS.reset();
		
		return ridgeS;
	}
	
	

}
