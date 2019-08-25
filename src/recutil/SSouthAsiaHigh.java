package recutil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 鍗椾簹楂樺帇鍖�
 */
public class SSouthAsiaHigh {

	public static WeatherSystems getSouthAsiaHigh(GridData height, int level, float scale) {
		//鍗椾簹楂樺帇鍖轰竴鑸湪16800绛変綅鍔跨嚎浠ュ唴
		GridData ids = SystemIdentification.getCuttedRegion(height.mutiply(height.add(-16800).sign01()));  //浠ラ珮搴﹀満鍒嗗壊鍑洪珮鍘嬪尯  
		Map<Integer, SystemFeature> features = SystemIdentification.getCentreAreaStrenght(height, ids); 
        Iterator<Entry<Integer, SystemFeature>> it = features.entrySet().iterator();  
        float lat,lon;
        int ig,jg,oldId;
       // ids.writeToFile("G:/data/systemIdentify/ids0.txt");
        while(it.hasNext()){  
            Entry<Integer, SystemFeature> entry = it.next();  
            lat = entry.getValue().centrePoint.ptLat;
            lon = entry.getValue().centrePoint.ptLon;
            if(lat<20 || lat> 40 || lon>110){  //鍗椾簹楂樺帇鐨勪腑蹇冨湪20-40N锛�110E浠ヨタ锛屽垹闄や箣澶栫殑楂樺帇鍒嗗尯
                 ig = (int) ((entry.getValue().centrePoint.ptLon-height.gridInfo.startlon)/height.gridInfo.dlon);
                 jg = (int) ((lat -height.gridInfo.startlat)/height.gridInfo.dlat);
                 oldId = entry.getKey();
                 SystemIdentification.resetId(ids, oldId, 0, ig, jg);
             }
        } 
     //   ids.writeToFile("G:/data/systemIdentify/ids1.txt");
        ids = SystemIdentification.combineStrongConnectingRegion_2d(ids, 0.1f);  //鍚堝苟楂樺帇鍖�
     //   ids.writeToFile("G:/data/systemIdentify/ids2.txt");
        
		VectorData tranDirection=new VectorData(height.gridInfo);                //骞虫祦椋庡畾涔変负姝ｅ崡椋�
		tranDirection.v.setValue(1.0f); 
		
		GridData marker = ids.sign01();
	//	marker.writeToFile("G:/data/systemIdentify/maker.txt");
		GridData smheight = height.copy();
		smheight.smooth(50);
	//	smheight.writeToFile("G:/data/systemIdentify/smheight.txt");
		ArrayList<Line> ridge = SystemIdentification.HighValueAreaRidge(tranDirection,smheight,level);
		ridge = LineDealing.cutLines(ridge, marker);
		LineDealing.smoothLines(ridge, 30);
		ridge = SystemIdentification.getLongLine(scale,ridge);
	//	LineDealing.writeToFile("G:/data/systemIdentify/ridge.txt",ridge);
		
		WeatherSystems sh = new  WeatherSystems("SubtropicalHigh",level);  //瀹氫箟鍗椾簹楂樺帇锛屽苟杩涜璧嬪��
		sh.type = "南亚高压";
		sh.setAxes(ridge);
		sh.setValue(height);
		sh.setIds(ids);
		sh.reset1(); //鍗椾簹楂樺帇杞寸嚎鍜屽垎鍖鸿缃殑鏃跺�欙紝涓�涓珮鍘嬪尯鍙兘瀵瑰簲澶氭潯杞寸嚎

		
		return sh;
	}

}
