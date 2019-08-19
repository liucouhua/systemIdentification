package recutil;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class T_SW_NEVortex {


	int trough_500_id = 0;
	int subHigh_500_id = 0;
	int shear_850_id = 0;
	int trough_1000_id = 0;
	int low_850_id = 0;

	int fit_num =0;
	
	public T_SW_NEVortex(HashMap<String, WeatherSystems> wss, ArrayList<float[]> typhoons) {
		// TODO Auto-generated constructor stub

		WeatherSystems hl_850 = wss.get("hl_850");
		WeatherSystems vortex_850 = wss.get("vortex_850");
		WeatherSystems trough_1000 = wss.get("trough_1000");
		WeatherSystems subHigh_500 = wss.get("subHigh_500");
		WeatherSystems jet_850 = wss.get("jet_850");
		WeatherSystems shear_850 = wss.get("shear_850");
		WeatherSystems trough_500 = wss.get("trough_500");
		
		
		
		//西南涡
	    //判断700hPa低压是否处于105-115,30-32
		WeatherSystems ws = null;
		Set<Integer> keys = null;
	    int max_grid_num =0;
	    int max_num_k=0;
	    float lon,lat;
	   
	    
		//东北气旋
		//判断850低压中心是否处于115°-120°，40-50°N
		ws = hl_850;
	    keys = ws.features.keySet();
	    max_grid_num =0;
	    max_num_k=0;
	    
		for(Integer k : keys){
			if (k<0) {
				float cx = ws.features.get(k).centrePoint.ptLon;
				float cy = ws.features.get(k).centrePoint.ptLat;
				
				if(cx >=115 && cx <= 120 && cy >=40 && cy <= 50) {
					//System.out.println("low_850:"+i+ "\n");
					//low_850_id = i;
					int num = 0;
					for (int i=0;i< ws.ids.gridInfo.nlon;i++) {
						for(int j=0;j< ws.ids.gridInfo.nlat;j++) {
							if(ws.ids.dat[i][j] == k) {
								lon = ws.ids.gridInfo.startlon + ws.ids.gridInfo.dlon * i;
								lat = ws.ids.gridInfo.startlat + ws.ids.gridInfo.dlat * j;
								if(lon >=115 & lon <=120 & lat >=40 & lat <=50) {
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
		low_850_id = max_num_k;
		if(max_num_k !=0) fit_num++;

		
		
		
		//判断副高脊线是否位于20-25°N
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
			if(cy >=20 && cy <= 25 && cx >=100 && cx <=130) {
				System.out.println("subHigh_500:"+k+"\n");
				subHigh_500_id = k;
				fit_num++;
				break;
			}
		}
		
		
		
		// 判断切边线是否位于： 110-120°，E30-40°N,取最长的一个
		//
		ws = shear_850;
	    keys = ws.features.keySet();
	    float max_lenght =0;
	    int max_lenght_k=0;
		for(Integer k : keys){
			
			Line line = ws.features.get(k).getAxes();
			int npoint = line.point.size();
			if(npoint ==0)continue;
			
			boolean in_region = false;
			int p0=10000,p1=-1;
			for (int p=0;p<npoint;p++) {
				float[] point = line.point.get(p);
				if(point[0]>=110 & point[0] <= 120 & point[1]>=30 & point[1] <=40) {
					in_region = true;
					if (p< p0)p0=p;
					if(p>p1)p1 = p;
				}
			}
			if(in_region) {
				float dis = MyMath.dis(line.point.get(p0)[0], line.point.get(p0)[1], line.point.get(p1)[0], line.point.get(p1)[1]);
				if(dis > max_lenght) {
					max_lenght = dis;
					max_lenght_k = k;
				}
			}
		}
		shear_850_id = max_lenght_k;
		if(max_lenght_k !=0) fit_num++;
		//System.out.println("shear_850:"+ shear_850_id+"\n");
		
		
		// 判断槽线是否为倒槽，且位于： 110°-120°，25-35°N
		ws = trough_1000;
		keys = ws.features.keySet();
	    max_lenght =0;
	    max_lenght_k=0;
		for(Integer k : keys){
			Line line = ws.features.get(k).getAxes();
			int npoint = line.point.size();
			if(npoint ==0)continue;
			if(line.point.get(0)[1]>line.point.get(npoint - 1)[1]) continue;
			boolean in_region = false;
			int p0=10000,p1=-1;
			for (int p=0;p<npoint;p++) {
				float[] point = line.point.get(p);
				if(point[0]>=110 & point[0] <= 120 & point[1]>=25 & point[1] <=35) {
					in_region = true;
					if (p< p0)p0=p;
					if(p>p1)p1 = p;
				}
			}
			if(in_region) {
				float dis = MyMath.dis(line.point.get(p0)[0], line.point.get(p0)[1], line.point.get(p1)[0], line.point.get(p1)[1]);
				if(dis > max_lenght) {
					max_lenght = dis;
					max_lenght_k = k;
				}
			}
		}
		trough_1000_id = max_lenght_k;
		if(max_lenght_k !=0) fit_num++;
		//System.out.println("trough_1000:"+ trough_1000_id+"\n");
		
		
		//判断500hPa高考槽线是否位于位置： 105°-115°E，35-45°N
		ws = trough_500;
		keys = ws.features.keySet();
	    max_lenght =0;
	    max_lenght_k=0;
		for(Integer k : keys){
			Line line = ws.features.get(k).getAxes();
			int npoint = line.point.size();
			if(npoint ==0)continue;
			if(line.point.get(0)[1] < line.point.get(npoint - 1)[1]) continue;
			boolean in_region = false;
			int p0=10000,p1=-1;
			for (int p=0;p<npoint;p++) {
				float[] point = line.point.get(p);
				if(point[0]>=105 & point[0] <= 115 & point[1]>=35 & point[1] <=45) {
					in_region = true;
					if (p< p0)p0=p;
					if(p>p1)p1 = p;
				}
			}
			if(in_region) {
				float dis = MyMath.dis(line.point.get(p0)[0], line.point.get(p0)[1], line.point.get(p1)[0], line.point.get(p1)[1]);
				if(dis > max_lenght) {
					max_lenght = dis;
					max_lenght_k = k;
				}
			}
		}
		trough_500_id = max_lenght_k;
		if(max_lenght_k !=0) fit_num++;
		//System.out.println("trough_500:"+ trough_500_id+"\n");
		//System.out.println();
		System.out.print("low_850" + "\t\t" + "shear_850" + "\t"  + "trough_1000" + "\t" + "trough_500" + "\t" +"subHigh_500" + "\t" +"fit_num"+"\n");
		System.out.print(low_850_id + "\t\t"  + shear_850_id + "\t\t" + trough_1000_id + "\t\t" + trough_500_id + "\t\t" +subHigh_500_id +"\t\t" + fit_num+ "\n");
		
	}

}
