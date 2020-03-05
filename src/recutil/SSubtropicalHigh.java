package recutil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * 浜氱儹甯﹂珮鍘�
 */
public class SSubtropicalHigh{
	public static WeatherSystems getSubtropicalHigh(GridData height, int level, float scale) {
		// 涓�鑸剰涔夌殑鍓珮鍦�588绾夸互鍐�
		GridData ids = SystemIdentification.getCuttedRegion(height.mutiply(height.add(-5840).sign01()),0);//浠ラ珮搴﹀満鍒嗗壊鍑洪珮鍘嬪尯  
		Map<Integer, SystemFeature> features = SystemIdentification.getCentreAreaStrenght(height, ids);
        Iterator<Entry<Integer, SystemFeature>> it = features.entrySet().iterator();  
        float lat;
        int ig,jg,oldId;
        
        
        //while(it.hasNext()){  
         //   Entry<Integer, SystemFeature> entry = it.next();  
        //    lat = entry.getValue().centrePoint.ptLat;
         //   if(lat<5 || lat> 40){ // 鍓珮鐨勪腑蹇冮渶鍦ㄥ壇鐑甫
        //         ig = (int) ((entry.getValue().centrePoint.ptLon-height.gridInfo.startlon)/height.gridInfo.dlon);
        //         jg = (int) ((lat -height.gridInfo.startlat)/height.gridInfo.dlat);
        //         oldId = entry.getKey();
        //         SystemIdentification.resetId(ids, oldId, 0, ig, jg);
        //     }
        //} 
        //ids.writeToFile("D:\\develop\\java\\201905-weahter_identification\\dec\\result\\all_china\\subHigh_500\\ids0.txt");
        //ids.writeToFile("D:\\develop\\java\\201905-weahter_identification\\dec\\result\\all_china\\subHigh_500\\ids1.txt");
        //ids = SystemIdentification.combineStrongConnectingRegion_2d(ids, 0.1f);  //鍚堝苟楂樺帇鍖�
        //ids.writeToFile("D:\\develop\\java\\201905-weahter_identification\\dec\\result\\all_china\\subHigh_500\\ids2.txt");
        combineEastmax(height,ids);
        
        //西太副高选择
        //ids.writeToFile("D:\\develop\\java\\201905-weahter_identification\\dec\\result\\all_china\\subHigh_500\\ids2.txt");
        
		VectorData tranDirection=new VectorData(height.gridInfo);  //骞虫祦椋庤缃鍗楅
		tranDirection.v.setValue(1.0f); 
		
		GridData marker = ids.sign01();
	//	marker.writeToFile("G:/data/systemIdentify/maker.txt");
		GridData smheight = height.copy();
		for(int i=0;i<height.gridInfo.nlon;i++) {
			for(int j= 0;j<height.gridInfo.nlat;j++) {
		//		smheight.dat[i][j] *= ids.dat[i][j];
			}
		}
		smheight.smooth(10);
		//smheight.writeToFile("D:\\develop\\java\\201905-weahter_identification\\dec\\result\\all_china\\subHigh_500\\smheight.txt");
		
	//	smheight.writeToFile("G:/data/systemIdentify/smheight.txt");
		ArrayList<Line> ridge = subHighRidge(tranDirection,smheight,level);  //鑾峰緱鑴婄嚎
		ridge = LineDealing.cutLines(ridge, marker);
		//LineDealing.writeToFile("D:\\develop\\java\\201905-weahter_identification\\dec\\result\\all_china\\subHigh_500\\ridge.txt",ridge);
		LineDealing.smoothLines(ridge, 30);
		ridge = SystemIdentification.getLongLine(scale,ridge);
		
		
		tranDirection = SystemIdentification.getDirectionFromLine(ridge, marker);
		ridge = subHighRidge(tranDirection,smheight,level);
		ridge = LineDealing.cutLines(ridge, marker);
		LineDealing.smoothLines(ridge, 10);
		//ridge = SystemIdentification.getLongLine(scale,ridge);
		float max_lenght = 0;
		int maxI = 0;
		for(int i =0; i< ridge.size();i++) {
			float lenght = LineDealing.lineLenght(ridge.get(i));
			if(lenght > max_lenght) {
				max_lenght = lenght;
				maxI = i;
			}
		}
		
		//连接其它线条
		Line maxLine = ridge.get(maxI);
		for(int i =0; i< ridge.size();i++) {
			
			if(i != maxI) {
				int npoint0 = maxLine.point.size();
				Line line1 = ridge.get(i);
				int npoint1 = line1.point.size();
				if (line1.point.get(0)[0] > maxLine.point.get(npoint0-1)[0]) {
					for(int j =0; j< npoint1;j++) {
						maxLine.point.add(line1.point.get(j));
					}
				}
				else if (line1.point.get(npoint1 - 1)[0] < maxLine.point.get(0)[0]) {
					for(int j =npoint1 - 1; j>=0;j--) {
						maxLine.point.add(0,line1.point.get(j));
					}
				}
				
			}
			
		}
		
		ArrayList<Line> ridge_max = new ArrayList<Line>();
		ridge_max.add(maxLine);
		LineDealing.smoothLines(ridge_max, 10);
		
		
		//LineDealing.writeToFile("D:\\develop\\java\\201905-weahter_identification\\dec\\result\\all_china\\subHigh_500\\ridge1.txt",ridge);
		// 从线条的两端沿着风场方向延伸
		//extend_lines(ridge,height);
		
	//	LineDealing.writeToFile("G:/data/systemIdentify/ridge1.txt",ridge);
		WeatherSystems sh = new  WeatherSystems("SubtropicalHigh",level);//瀹氫箟鍓儹甯﹂珮鍘嬶紝骞惰繘琛岃祴鍊�
		sh.type= "副高";
		sh.setAxes(ridge_max);
		sh.setValue(height);
		sh.setIds(ids);
		sh.reset1();//鍓珮鍘嬭酱绾垮拰鍒嗗尯璁剧疆鐨勬椂鍊欙紝涓�涓珮鍘嬪尯鍙兘瀵瑰簲澶氭潯杞寸嚎

		
		return sh;
	}
	

	public static ArrayList<Line> subHighRidge(VectorData tranWind, GridData gridFeature,int level){
		//鏍规嵁骞虫祦椋庢柟娉曡幏鍙栭珮鍊煎尯鐨勮剨绾�
		VectorData tranDirection = VectorMathod.getDirection(tranWind);          //璁＄畻骞虫祦鏂瑰悜
		GridData adve=VectorMathod.getAdvection(tranDirection, gridFeature);    //璁＄畻骞虫祦鍦�
		adve.writeToFile("D:\\develop\\java\\201905-weahter_identification\\output\\trough_1000\\adve.txt");
		ArrayList<Line> line0 = LineDealing.creatLine(0.0f, adve);  //璁＄畻骞虫祦鍦�0绾匡紝浣滀负鑴婄嚎鎴栨Ы绾�
		VectorData adve_grad_direction = VectorMathod.getDirection(VectorMathod.getGrads(adve));
		GridData adveOfAdve= VectorMathod.dot(tranDirection, adve_grad_direction); //
		GridData marker=gridFeature.sign01().mutiply(adveOfAdve.add(0.5f).mutiply(-1).sign01());
		adveOfAdve.writeToFile("D:\\develop\\java\\201905-weahter_identification\\output\\trough_1000\\adveofadv.txt");
		
		// 杈圭晫瑁佸壀
		int n = 3;
		GridData markerR= new GridData(marker.gridInfo);
		for(int i= n;i<markerR.gridInfo.nlon-n;i++){
			for(int j=n;j<markerR.gridInfo.nlat-n;j++){
				markerR.dat[i][j]=1;
			}
		}
		marker = marker.mutiply(markerR);
		
		ArrayList<Line> lines= LineDealing.cutLines(line0, marker); 
		return lines;
	}
	
	private static Map<Integer, Map<String, Float>> getCentreAndEdgeMax(GridData grid, GridData ids) {
		// ��һ���ߣ��ͣ�ѹ���ľ�����һ���ߣ��ͣ�ѹ���������<scale ʱ���轫�䲢�������ߣ��ͣ�ѹ����Ϊ�������øߣ��ͣ�ѹ���ľ���߽���������
		Map<Integer,Map<String,Float>> idPros = new HashMap<Integer,Map<String,Float>>();
		int id,i1,j1,id1;
		int nlon = ids.gridInfo.nlon;
		int nlat = ids.gridInfo.nlat;
		float slat = grid.gridInfo.startlat;
		float slon = grid.gridInfo.startlon;
		float dlat = grid.gridInfo.dlat;
		float dlon = grid.gridInfo.dlon;
		float dis = 0,cx,cy,ex,ey;
		Map<String,Float> idPro =null;
		for(int i=0;i<nlon;i++){
			for(int j=0;j<nlat;j++){
				id = (int) ids.dat[i][j];
				if(id!=0){
					idPro =null;				
					if(idPros.containsKey(id)){
						idPro = idPros.get(id);
					}
					else{
						idPro=new HashMap<String,Float>();
						idPro.put("centreMax", 0.0f);
						idPro.put("centreI", 0.0f);
						idPro.put("centreJ", 0.0f);
						idPro.put("edgeMax",0.0f);
						idPro.put("edgeId", 0.0f);
						idPro.put("edgeI", 0.0f);
						idPro.put("edgeJ", 0.0f);
						idPro.put("scale", 9999.0f);
						idPros.put(id, idPro);
					}
					
					if(grid.dat[i][j]>idPro.get("centreMax")){
						idPro.put("centreMax", grid.dat[i][j]);
						idPro.put("centreI", (float)i);
						idPro.put("centreJ", (float)j);
					}
					//map_line.put(0,area+grid0.dat[i][j]);
					
				    for(int p=-1;p<2;p++){
			        	for(int q=-1;q<2;q++){
			        		if(Math.abs(p)+Math.abs(q)!=1) continue;
			        		i1= MyMath.cycleIndex(false, nlon, i, p);
			        		j1= MyMath.cycleIndex(false, nlat, j, q);
			        		id1 = (int) ids.dat[i1][j1];
			        		if(id1 !=0 && id1 != id){
			        			if(grid.dat[i1][j1]>idPro.get("edgeMax")){
			        				idPro.put("edgeMax",grid.dat[i1][j1]);
									idPro.put("edgeId", (float)id1);
									idPro.put("edgeI", (float)i1);
									idPro.put("edgeJ", (float)j1);
			        			}
			        		}
			        	}
				    }
				}
			}
		}	
		Set<Integer> keys = idPros.keySet();
		for ( Integer key : keys){
			idPro = idPros.get(key);
			cx = slon + dlon * idPro.get("centreI");
			cy = slat + dlat * idPro.get("centreJ");
			ex = slon + dlon * idPro.get("edgeI");
			ey = slat + dlat * idPro.get("edgeJ");
			dis = MyMath.dis(cx, cy, ex, ey);
			idPro.put("scale", dis);	
		}
		 
		
		return idPros;
	}

	
	
	 private static  void combineEastmax(GridData height,GridData ids){			
		//濡傛灉涓や釜鐩搁偦鐨勭郴缁熻竟鐣岀嚎寰堥暱锛岃�岄潰绉笉澶э紝鍒欏畠浠簲璇ュ睘浜庡悓涓�涓郴缁�
		//GridData grid =ids.copy();
		//閫愪釜鍚堝苟閾炬帴搴﹁秴杩囬槇鍊肩殑鐩爣
		int nlon = ids.gridInfo.nlon;
		int nlat = ids.gridInfo.nlat;
	

		while(true){
			//鑾峰彇姣忎釜鐩爣鐨勯潰绉紝浠ュ強瀹冨拰鍏跺畠鐩爣鐨勮竟鐣岀嚎闀垮害
			Map<Integer, Map<String, Float>> map = getCentreAndEdgeMax(height,ids);			
			float maxEdge = 0;
			int id0=0,id1=0;
			for (Map.Entry<Integer, Map<String, Float>> entry : map.entrySet()){
				Map<String,Float> map1 = entry.getValue();
				float i1 = map1.get("centreI");
				int id1_1 = entry.getKey();
				
				float edgeId = map1.get("edgeId");
				int id2 = (int) edgeId;
				
				if(id2 == 0)continue;
				Map<String,Float> map2 = map.get(id2);
				float edgeId1 = map2.get("edgeId");
				int id2_1 = (int)edgeId1;
				float i2 = map2.get("centreI");		
				float lon1 = height.gridInfo.startlon + i1 * height.gridInfo.dlon;
				float lon2 = height.gridInfo.startlon + i2 * height.gridInfo.dlon;
				float minlon = Math.min(lon1, lon2);
				if(id1_1 != id2_1)continue;
									
				if(minlon > 105 || Math.abs(lon1 - lon2) <= 18) {
					float edge = map1.get("edgeMax");
					if(maxEdge < edge) {
						maxEdge = edge;
						id0 = id1_1;
						id1 = id2;
						
					}
				}
			}

			
			if(maxEdge < 5840){
				break;
			}
			else{
				//鍚堝苟鐩爣id
				//System.out.println(id0);
				//System.out.println(id1);
				for(int i=0;i<nlon;i++){
					for(int j=0;j<nlat;j++){
						if(ids.dat[i][j]==id1) ids.dat[i][j]=id0;
					}
				}
			}
		}
		ids.writeToFile("D:\\develop\\java\\201905-weahter_identification\\dec\\result\\all_china\\subHigh_500\\ids3.txt");

		//对105度以东的，如果中心值更高的在西侧，就可以合并
		Map<Integer, Map<String, Float>> map = getCentreAndEdgeMax(height,ids);			
		float maxcentre = 0;
		int max_id = 0;
		float max_lon = 0;
		for (Map.Entry<Integer, Map<String, Float>> entry : map.entrySet()){
			Map<String,Float> map1 = entry.getValue();
			float i1 = map1.get("centreI");
			float lon1 = height.gridInfo.startlon + i1 * height.gridInfo.dlon;
			
			if (map1.get("centreMax") > maxcentre && lon1 >105) {
				max_lon = lon1;
				max_id = entry.getKey();
				maxcentre = map1.get("centreMax");
			}
			
	 	}
		
		for (Map.Entry<Integer, Map<String, Float>> entry : map.entrySet()){
			Map<String,Float> map1 = entry.getValue();
			float i2 = map1.get("centreI");
			float lon2 = height.gridInfo.startlon + i2 * height.gridInfo.dlon;
			
			if (lon2 >max_lon) {
				int id2 = entry.getKey();
				for(int i=0;i<nlon;i++){
					for(int j=0;j<nlat;j++){
						if(ids.dat[i][j]==id2) ids.dat[i][j]=max_id;
					}
				}
			}
			
	 	}
		
		/*
		while(true){
			//鑾峰彇姣忎釜鐩爣鐨勯潰绉紝浠ュ強瀹冨拰鍏跺畠鐩爣鐨勮竟鐣岀嚎闀垮害
			Map<Integer, Map<String, Float>> map = getCentreAndEdgeMax(height,ids);			
			float maxEdge = 0;
			int id0=0,id1=0;
			for (Map.Entry<Integer, Map<String, Float>> entry : map.entrySet()){
				Map<String,Float> map1 = entry.getValue();
				float i1 = map1.get("centreI");
				int id1_1 = entry.getKey();
				float centreMax1 = map1.get("centreMax");
				float edgeId = map1.get("edgeId");
				int id2 = (int) edgeId;
				
				if(id2 == 0)continue;
				Map<String,Float> map2 = map.get(id2);
				float edgeId1 = map2.get("edgeId");
				float i2 = map2.get("centreI");		
				float centreMax2 = map2.get("centreMax");
				float lon1 = height.gridInfo.startlon + i1 * height.gridInfo.dlon;
				float lon2 = height.gridInfo.startlon + i2 * height.gridInfo.dlon;
				float minlon = Math.min(lon1, lon2);
				
									
				if(minlon > 105 && (centreMax1 - centreMax2) * (lon1 - lon2) <=0) {
					float edge = map1.get("edgeMax");
					if(maxEdge < edge) {
						maxEdge = edge;
						id0 = id1_1;
						id1 = id2;
						
					}
				}
			}

			
			if(maxEdge < 5840){
				break;
			}
			else{
				//鍚堝苟鐩爣id
				//System.out.println(id0);
				//System.out.println(id1);
				for(int i=0;i<nlon;i++){
					for(int j=0;j<nlat;j++){
						if(ids.dat[i][j]==id1) ids.dat[i][j]=id0;
					}
				}
			}
		}
		*/
		
		
		ids.writeToFile("D:\\develop\\java\\201905-weahter_identification\\dec\\result\\all_china\\subHigh_500\\ids4.txt");
		 map = getCentreAndEdgeMax(height,ids);	
		float maxlon = 0;
		float edgemax = 0;
		int id_east = 0;
		float maxvalue =0;
		for (Map.Entry<Integer, Map<String, Float>> entry : map.entrySet()){
			Map<String,Float> map1 = entry.getValue();
			float i1 = map1.get("centreI");
			float lon1 = height.gridInfo.startlon + i1 * height.gridInfo.dlon;
			if(maxlon < lon1) {
				maxlon = lon1;
				edgemax =map1.get("edgeMax");
				id_east= entry.getKey();
				maxvalue = map1.get("centreMax");
			}
			else if (maxlon == lon1) {
				float maxvalue1 = map1.get("centreMax");
				if(maxvalue < maxvalue1) {
					maxlon = lon1;
					edgemax =map1.get("edgeMax");
					id_east= entry.getKey();
					maxvalue = map1.get("centreMax");
				}
			}
		}
		
		for(int i=0;i<nlon;i++) {
			for(int j= 0;j<nlat;j++) {
				
				if(height.dat[i][j] < edgemax)ids.dat[i][j] = 0;
				if(ids.dat[i][j] != id_east) {
					ids.dat[i][j] =0;
				}
				else {
					ids.dat[i][j] =1;
				}
			}
		}
		
		
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
