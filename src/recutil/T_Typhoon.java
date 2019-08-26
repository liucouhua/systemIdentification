package recutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class T_Typhoon {


	int high_850_id = 0;
	int subHigh_500_id = 0;
	int typhoon_id = 0;

	ArrayList<Integer> high_850_id_list = new ArrayList<Integer>(); 
	ArrayList<Integer> subHigh_500_id_list = new ArrayList<Integer>(); 
	
	int fit_num =0;
	
	public T_Typhoon(HashMap<String, WeatherSystems> wss, ArrayList<float[]> typhoons) {
		// TODO Auto-generated constructor stub

		//判断台风是否位于 110°-120°，20-25
		for (int i = 0;i<typhoons.size();i++) {
			float[] ty1 = typhoons.get(i);
			if(ty1[1] >=110 && ty1[1] <=120 && ty1[2] >= 20 && ty1[2] <= 25) {
				typhoon_id = (int)ty1[0];
			}
		}
		if (typhoon_id == 0) return;
		fit_num ++;
		
		WeatherSystems hl_850 = wss.get("hl_850");
		WeatherSystems subHigh_500 = wss.get("subHigh_500");
		
		
		WeatherSystems ws = null;
		Set<Integer> keys = null;
	    int max_grid_num =0;
	    int max_num_k=0;
	    float lon,lat;
	    

		//大陆高压
		//判断850高压中心是否处于105°-115°，35-40°N
		ws = hl_850;
	    keys = ws.features.keySet();
	    max_grid_num =0;
	    max_num_k=0;
	    
		for(Integer k : keys){
			if (k>0) {
				float cx = ws.features.get(k).centrePoint.ptLon;
				float cy = ws.features.get(k).centrePoint.ptLat;
				
				if(cx >=105 && cx <= 115 && cy >=35 && cy <= 40) {	
					high_850_id_list.add(k);
					int num = 0;
					for (int i=0;i< ws.ids.gridInfo.nlon;i++) {
						for(int j=0;j< ws.ids.gridInfo.nlat;j++) {
							if(ws.ids.dat[i][j] == k) {
								lon = ws.ids.gridInfo.startlon + ws.ids.gridInfo.dlon * i;
								lat = ws.ids.gridInfo.startlat + ws.ids.gridInfo.dlat * j;
								if(lon >=110 & lon <=120 & lat >=35 & lat <=40) {
									num ++;
								}
							}
						}
					}
					if(num > max_grid_num) {
						max_grid_num = num;
						max_num_k = k;
					}
					
				}
			}
		}
		high_850_id = max_num_k;
		if(max_num_k !=0) fit_num++;

		
		//判断副高脊线是否位于35-40°N
		ws = subHigh_500;
	    keys = ws.features.keySet();
	    //System.out.print(keys.size());
	    int subHigh_500_num = 0;
		for(Integer k : keys){
			
			Line line = ws.features.get(k).getAxes();
			int npoint = line.point.size();
			if(npoint ==0)continue;
			float cx = 0,cy = 0;
			if(line.point.get(0)[0]>line.point.get(npoint - 1)[0]) {
				cx = line.point.get(npoint - 1)[0];
				cy = line.point.get(npoint - 1)[1];
			}
			else {
				cx = line.point.get(0)[0];
				cy = line.point.get(0)[1];
			}
			//System.out.println(cx + " "+ cy);
			if(cy >=35 && cy <= 40 && cx >=70 && cx <=150) {
				subHigh_500_id_list.add(k);
				System.out.println("subHigh_500:"+k+"\n");
				subHigh_500_id = k;
				fit_num++;
				break;
			}
		}
		
		
		

		//System.out.println("trough_500:"+ trough_500_id+"\n");
		//System.out.println();
		System.out.print("typhoon"+"\t"+"high_850"+"\t" +"subHigh_500" + "\t" +"fit_num"+"\n");
		System.out.print(typhoon_id+"\t\t"+high_850_id +"\t\t" +subHigh_500_id +"\t\t" + fit_num+ "\n");
		
	}

}
