package code;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class SSouthAsiaHigh {

	public static WeatherSystems getSouthAsiaHigh(GridData height, int level, float scale) {
		//南亚高压区一般在16800等位势线以内
		GridData ids = SystemIdentification.getCuttedRegion(height.mutiply(height.add(-16800).sign01()));  //以高度场分割出高压区  
		Map<Integer, SystemFeature> features = SystemIdentification.getCentreAreaStrenght(height, ids); 
        Iterator<Entry<Integer, SystemFeature>> it = features.entrySet().iterator();  
        float lat,lon;
        int ig,jg,oldId;
       // ids.writeToFile("G:/data/systemIdentify/ids0.txt");
        while(it.hasNext()){  
            Entry<Integer, SystemFeature> entry = it.next();  
            lat = entry.getValue().centrePoint.ptLat;
            lon = entry.getValue().centrePoint.ptLon;
            if(lat<20 || lat> 40 || lon>110){  //南亚高压的中心在20-40N，110E以西，删除之外的高压分区
                 ig = (int) ((entry.getValue().centrePoint.ptLon-height.gridInfo.startlon)/height.gridInfo.dlon);
                 jg = (int) ((lat -height.gridInfo.startlat)/height.gridInfo.dlat);
                 oldId = entry.getKey();
                 SystemIdentification.resetId(ids, oldId, 0, ig, jg);
             }
        } 
     //   ids.writeToFile("G:/data/systemIdentify/ids1.txt");
        ids = SystemIdentification.combineStrongConnectingRegion_2d(ids, 0.1f);  //合并高压区
     //   ids.writeToFile("G:/data/systemIdentify/ids2.txt");
        
		VectorData tranDirection=new VectorData(height.gridInfo);                //平流风定义为正南风
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
		
		WeatherSystems sh = new  WeatherSystems("SubtropicalHigh",level);  //定义南亚高压，并进行赋值
		sh.setAxes(ridge);
		sh.setValue(height);
		sh.setIds(ids);
		sh.reset1(); //南亚高压轴线和分区设置的时候，一个高压区可能对应多条轴线

		
		return sh;
	}

}
