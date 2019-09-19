package recutil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 浜氱儹甯﹂珮鍘�
 */
public class SSubtropicalHigh{
	public static WeatherSystems getSubtropicalHigh(GridData height, int level, float scale) {
		// 涓�鑸剰涔夌殑鍓珮鍦�588绾夸互鍐�
		GridData ids = SystemIdentification.getCuttedRegion(height.mutiply(height.add(-5880).sign01()),0);//浠ラ珮搴﹀満鍒嗗壊鍑洪珮鍘嬪尯  
		Map<Integer, SystemFeature> features = SystemIdentification.getCentreAreaStrenght(height, ids);
        Iterator<Entry<Integer, SystemFeature>> it = features.entrySet().iterator();  
        float lat;
        int ig,jg,oldId;
        
        //ids.writeToFile("G:/data/systemIdentify/ids0.txt");
        while(it.hasNext()){  
            Entry<Integer, SystemFeature> entry = it.next();  
            lat = entry.getValue().centrePoint.ptLat;
            if(lat<10 || lat> 40){ // 鍓珮鐨勪腑蹇冮渶鍦ㄥ壇鐑甫
                 ig = (int) ((entry.getValue().centrePoint.ptLon-height.gridInfo.startlon)/height.gridInfo.dlon);
                 jg = (int) ((lat -height.gridInfo.startlat)/height.gridInfo.dlat);
                 oldId = entry.getKey();
                 SystemIdentification.resetId(ids, oldId, 0, ig, jg);
             }
        } 
      //  ids.writeToFile("G:/data/systemIdentify/ids1.txt");
       ids = SystemIdentification.combineStrongConnectingRegion_2d(ids, 0.1f);  //鍚堝苟楂樺帇鍖�
      //  ids.writeToFile("G:/data/systemIdentify/ids2.txt");
        
		VectorData tranDirection=new VectorData(height.gridInfo);  //骞虫祦椋庤缃鍗楅
		tranDirection.v.setValue(1.0f); 
		
		GridData marker = ids.sign01();
	//	marker.writeToFile("G:/data/systemIdentify/maker.txt");
		GridData smheight = height.copy();
		smheight.smooth(5);
	//	smheight.writeToFile("G:/data/systemIdentify/smheight.txt");
		ArrayList<Line> ridge = SystemIdentification.HighValueAreaRidge(tranDirection,smheight,level);  //鑾峰緱鑴婄嚎
		ridge = LineDealing.cutLines(ridge, marker);
		LineDealing.smoothLines(ridge, 30);
		ridge = SystemIdentification.getLongLine(scale,ridge);
	//	LineDealing.writeToFile("G:/data/systemIdentify/ridge.txt",ridge);
		
		tranDirection = SystemIdentification.getDirectionFromLine(ridge, marker);
		ridge = SystemIdentification.HighValueAreaRidge(tranDirection,smheight,level);
		ridge = LineDealing.cutLines(ridge, marker);
		LineDealing.smoothLines(ridge, 10);
		ridge = SystemIdentification.getLongLine(scale,ridge);
		
		
		// 从线条的两端沿着风场方向延伸
		//extend_lines(ridge,height);
		
	//	LineDealing.writeToFile("G:/data/systemIdentify/ridge1.txt",ridge);
		WeatherSystems sh = new  WeatherSystems("SubtropicalHigh",level);//瀹氫箟鍓儹甯﹂珮鍘嬶紝骞惰繘琛岃祴鍊�
		sh.type= "副高";
		sh.setAxes(ridge);
		sh.setValue(height);
		sh.setIds(ids);
		sh.reset1();//鍓珮鍘嬭酱绾垮拰鍒嗗尯璁剧疆鐨勬椂鍊欙紝涓�涓珮鍘嬪尯鍙兘瀵瑰簲澶氭潯杞寸嚎

		
		return sh;
	}
	

	private static void extend_lines(ArrayList<Line> jetLine, GridData height) {
		// TODO Auto-generated method stub
		
		//VectorData windm = VectorMathod.rotate(windp, 180);
		int nline = jetLine.size();
		float h;
		float [] ps = new float[2],p1 = new float[2],p_1 = new float[2];
		for(int i = 0;i <nline;i++) {
			Line line1 = jetLine.get(i);

			for(int j = 0;j <1000;j++) {
				h = VectorMathod.getValue(height, ps);
				if(h < 5880) break;
				ps = line1.point.get(0);
				p_1 = line1.point.get(1);
				p1[0] = 2 * ps[0] - p_1[0];
				p1[1] = 2 * ps[1] - p_1[1];
				if(p1[0]<=height.gridInfo.startlon+1 || p1[0] >= height.gridInfo.endlon-1) break;
				if(p1[1]<=height.gridInfo.startlat+1 || p1[1] >= height.gridInfo.endlat-1) break;
				line1.point.add(0,p1);
				
			}
			int npoint = line1.point.size();
			float[] pe = new float[2];
			for(int j = 0;j <1000;j++) {
				h = VectorMathod.getValue(height, ps);
				if(h < 5880) break;
				pe = line1.point.get(npoint -1);
				p_1 = line1.point.get(npoint -2);
				p1[0] = 2 * ps[0] - p_1[0];
				p1[1] = 2 * ps[1] - p_1[1];
				if(p1[0]<=height.gridInfo.startlon+1 || p1[0] >= height.gridInfo.endlon-1) break;
				if(p1[1]<=height.gridInfo.startlat+1 || p1[1] >= height.gridInfo.endlat-1) break;
				line1.point.add(0,p1);
			}
			
			
			
		}
		
	}
	
	
}
