package recutil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 亚热带高压
 */
public class SSubtropicalHigh{
	public static WeatherSystems getSubtropicalHigh(GridData height, int level, float scale) {
		// 一般意义的副高在588线以内
		GridData ids = SystemIdentification.getCuttedRegion(height.mutiply(height.add(-5880).sign01()));//以高度场分割出高压区  
		Map<Integer, SystemFeature> features = SystemIdentification.getCentreAreaStrenght(height, ids);
        Iterator<Entry<Integer, SystemFeature>> it = features.entrySet().iterator();  
        float lat;
        int ig,jg,oldId;
      //  ids.writeToFile("G:/data/systemIdentify/ids0.txt");
        while(it.hasNext()){  
            Entry<Integer, SystemFeature> entry = it.next();  
            lat = entry.getValue().centrePoint.ptLat;
            if(lat<10 || lat> 40){ // 副高的中心需在副热带
                 ig = (int) ((entry.getValue().centrePoint.ptLon-height.gridInfo.startlon)/height.gridInfo.dlon);
                 jg = (int) ((lat -height.gridInfo.startlat)/height.gridInfo.dlat);
                 oldId = entry.getKey();
                 SystemIdentification.resetId(ids, oldId, 0, ig, jg);
             }
        } 
      //  ids.writeToFile("G:/data/systemIdentify/ids1.txt");
        ids = SystemIdentification.combineStrongConnectingRegion_2d(ids, 0.1f);  //合并高压区
      //  ids.writeToFile("G:/data/systemIdentify/ids2.txt");
        
		VectorData tranDirection=new VectorData(height.gridInfo);  //平流风设置正南风
		tranDirection.v.setValue(1.0f); 
		
		GridData marker = ids.sign01();
	//	marker.writeToFile("G:/data/systemIdentify/maker.txt");
		GridData smheight = height.copy();
		smheight.smooth(100);
	//	smheight.writeToFile("G:/data/systemIdentify/smheight.txt");
		ArrayList<Line> ridge = SystemIdentification.HighValueAreaRidge(tranDirection,smheight,level);  //获得脊线
		ridge = LineDealing.cutLines(ridge, marker);
		LineDealing.smoothLines(ridge, 30);
		ridge = SystemIdentification.getLongLine(scale,ridge);
	//	LineDealing.writeToFile("G:/data/systemIdentify/ridge.txt",ridge);
		
		tranDirection = SystemIdentification.getDirectionFromLine(ridge, marker);
		ridge = SystemIdentification.HighValueAreaRidge(tranDirection,smheight,level);
		ridge = LineDealing.cutLines(ridge, marker);
		LineDealing.smoothLines(ridge, 30);
		ridge = SystemIdentification.getLongLine(scale,ridge);
	//	LineDealing.writeToFile("G:/data/systemIdentify/ridge1.txt",ridge);
		WeatherSystems sh = new  WeatherSystems("SubtropicalHigh",level);//定义副热带高压，并进行赋值
		sh.setAxes(ridge);
		sh.setValue(height);
		sh.setIds(ids);
		sh.reset1();//副高压轴线和分区设置的时候，一个高压区可能对应多条轴线

		
		return sh;
	}
}
