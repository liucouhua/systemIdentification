package recutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class T_Front {
	int low_surface_id=0;
	int low_850_id = 0;
	int trough_500_id = 0;
	int trough_1000_id = 0;
	int subHigh_500_id = 0;
	int jet_850_id = 0;
	int shear_850_id =0;
	int vortex_850_id = 0;
	ArrayList<Integer> low_surface_id_list = new ArrayList<Integer>();
	ArrayList<Integer> low_850_id_list = new ArrayList<Integer>();
	ArrayList<Integer> trough_500_id_list= new ArrayList<Integer>();
	ArrayList<Integer> trough_1000_id_list= new ArrayList<Integer>();
	ArrayList<Integer> subHigh_500_id_list= new ArrayList<Integer>();
	ArrayList<Integer> jet_850_id_list= new ArrayList<Integer>();
	ArrayList<Integer> shear_850_id_list= new ArrayList<Integer>();
	ArrayList<Integer> vortex_850_id_list = new ArrayList<Integer>();

	int fit_num =0;
	
	public T_Front(HashMap<String, WeatherSystems> wss, ArrayList<float[]> typhoons) {
		// TODO Auto-generated constructor stub
		WeatherSystems low_surface  = wss.get("low_surface");
		WeatherSystems low_850 = wss.get("hl_850");
		WeatherSystems vortex_850 = wss.get("vortex_850");
		WeatherSystems trough_1000 = wss.get("trough_1000");
		WeatherSystems subHigh_500 = wss.get("subHigh_500");
		WeatherSystems jet_850 = wss.get("jet_850");
		WeatherSystems shear_850 = wss.get("shear_850");
		WeatherSystems trough_500 = wss.get("trough_500");
		
		
		
		//江淮气旋
		//判断地面低压中心是否位于110°-120°，30-35°N
		WeatherSystems ws = low_surface;
		Set<Integer> keys = ws.features.keySet();
		int low_surface_num = 0;
	    int max_grid_num =0;
	    int max_num_k=0;
	    float lon,lat;
		for(Integer k : keys){
			if (k<0) {
				float cx = ws.features.get(k).centrePoint.ptLon;
				float cy = ws.features.get(k).centrePoint.ptLat;
				
				if(cx >=110 && cx <= 120 && cy >=30 && cy <= 35) {
					//System.out.println("low_surface:"+i+ "\n");
					//low_surface_id = i;
					low_surface_num += 1;
					
					int num = 0;
					for (int i=0;i< ws.ids.gridInfo.nlon;i++) {
						for(int j=0;j< ws.ids.gridInfo.nlat;j++) {
							if(ws.ids.dat[i][j] == k) {
								lon = ws.ids.gridInfo.startlon + ws.ids.gridInfo.dlon * i;
								lat = ws.ids.gridInfo.startlat + ws.ids.gridInfo.dlat * j;
								if(lon >=110 & lon <=120 & lat >=30 & lat <=35) {
									num ++;
								}
							}
						}
					}
					if(num >0) {
						low_surface_id_list.add(k);
					}
					if(num > max_grid_num) {
						max_grid_num = num;
						max_num_k = k;
					}
				}
			}
		}
		low_surface_id = max_num_k;
		if(max_num_k !=0) fit_num++;
		
		//System.out.println("low_surface_num:"+ low_surface_num+"\n");
		
		//判断850低压中心是否处于110°-120°，30-35°N
		ws = low_850;
	    keys = ws.features.keySet();
	    int low_850_num = 0;
	    max_grid_num =0;
	    max_num_k=0;
	    
		for(Integer k : keys){
			if (k<0) {
				float cx = ws.features.get(k).centrePoint.ptLon;
				float cy = ws.features.get(k).centrePoint.ptLat;
				
				if(cx >=110 && cx <= 120 && cy >=30 && cy <= 35) {
					//System.out.println("low_850:"+i+ "\n");
					//low_850_id = i;
					low_850_num +=1;
					int num = 0;
					for (int i=0;i< ws.ids.gridInfo.nlon;i++) {
						for(int j=0;j< ws.ids.gridInfo.nlat;j++) {
							if(ws.ids.dat[i][j] == k) {
								lon = ws.ids.gridInfo.startlon + ws.ids.gridInfo.dlon * i;
								lat = ws.ids.gridInfo.startlat + ws.ids.gridInfo.dlat * j;
								if(lon >=110 & lon <=120 & lat >=30 & lat <=35) {
									num ++;
								}
							}
						}
					}
					if(num >0) {
						low_850_id_list.add(k);
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
		
		//System.out.println("low_850_num:"+ low_850_num+"\n");
		
		
		//判断850涡旋中心是否处于110°-120°，30-35°N
		ws = vortex_850;
	    keys = ws.features.keySet();
	    int vortex_850_num =0;
	    max_grid_num =0;
	    max_num_k=0;
		for(Integer k : keys){
			
			float cx = ws.features.get(k).centrePoint.ptLon;
			float cy = ws.features.get(k).centrePoint.ptLat;
			
			if(cx >=110 && cx <= 120 && cy >=30 && cy <= 35) {
				//System.out.println("vortex_850:"+i+"\n");
				//low_850_id = i;
				vortex_850_num +=1;
				int num = 0;
				for (int i=0;i< ws.ids.gridInfo.nlon;i++) {
					for(int j=0;j< ws.ids.gridInfo.nlat;j++) {
						if(ws.ids.dat[i][j] == k) {
							lon = ws.ids.gridInfo.startlon + ws.ids.gridInfo.dlon * i;
							lat = ws.ids.gridInfo.startlat + ws.ids.gridInfo.dlat * j;
							if(lon >=110 & lon <=120 & lat >=30 & lat <=35) {
								num ++;
							}
						}
					}
				}
				if(num>0) {
					vortex_850_id_list.add(k);
				}
				if(num > max_grid_num) {
					max_grid_num = num;
					max_num_k = k;
				}
				
			}	
		}
		vortex_850_id = max_num_k;
		if(max_num_k !=0) fit_num++;
		//System.out.println("vortex_850_num:"+ vortex_850_num+"\n");
		
		
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
			if(cy >=20 && cy <= 25 && cx >=70 && cx <=150) {
				System.out.println("subHigh_500:"+k+"\n");
				subHigh_500_id = k;
				fit_num++;
				subHigh_500_id_list.add(k);
				break;
			}
		}
		//System.out.println("subHigh_500_num:"+ vortex_850_num+"\n");
		
		
		
		//判断低空急流是否有部分处于110-120°E，25-33°N,提取急流轴处于这个区域的各急流，选择急流区落在区域面积最大的一个
		ws = jet_850;
	    keys = ws.features.keySet();
	    max_grid_num =0;
	    max_num_k=0;
		for(Integer k : keys){
			
			Line line = ws.features.get(k).getAxes();
			int npoint = line.point.size();
			if(npoint ==0)continue;
			boolean in_region = false;
			for (int p=0;p<npoint;p++) {
				float[] point = line.point.get(p);
				if(point[0]>=110 & point[0] <= 120 & point[1]>=25 & point[1] <=33) {
					in_region = true;
				}
			}
			if(in_region) {
				int num = 0;
				for (int i=0;i< ws.ids.gridInfo.nlon;i++) {
					for(int j=0;j< ws.ids.gridInfo.nlat;j++) {
						if(ws.ids.dat[i][j] == k) {
							lon = ws.ids.gridInfo.startlon + ws.ids.gridInfo.dlon * i;
							lat = ws.ids.gridInfo.startlat + ws.ids.gridInfo.dlat * j;
							if(lon >=110 & lon <=120 & lat >=25 & lat <=33) {
								num ++;
							}
						}
					}
				}
				if(num >0) {
					jet_850_id_list.add(k);
				}
				if(num > max_grid_num) {
					max_grid_num = num;
					max_num_k = k;
				}
				
			}
		}
		jet_850_id = max_num_k;
		if(max_num_k !=0) fit_num++;
		//System.out.println("jet_850:"+ jet_850_id+"\n");
		
		
		// 判断切边线是否位于： 110-120°，E30-35°N,取最长的一个
		
		
		//判断低空急流是否有部分处于110-120°E，25-33°N,提取急流轴处于这个区域的各急流，选择急流区落在区域面积最大的一个
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
				if(point[0]>=110 & point[0] <= 120 & point[1]>=25 & point[1] <=35) {
					in_region = true;
					if (p< p0)p0=p;
					if(p>p1)p1 = p;
				}
			}
			if(in_region) {
				shear_850_id_list.add(k);
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
				if(point[0]>=110 & point[0] <= 120 & point[1]>=25 & point[1] <=33) {
					in_region = true;
					if (p< p0)p0=p;
					if(p>p1)p1 = p;
				}
			}
			if(in_region) {
				trough_1000_id_list.add(k);
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
		
		
		//判断500hPa高考槽线是否位于位置： 105°-115°E，30-40°N
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
				if(point[0]>=105 & point[0] <= 115 & point[1]>=30 & point[1] <=40) {
					in_region = true;
					if (p< p0)p0=p;
					if(p>p1)p1 = p;
				}
			}
			if(in_region) {
				trough_500_id_list.add(k);
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
		System.out.print("low_surface" +"\t"+ "low_850" + "\t\t" + "vortex_850" + "\t" + "shear_850" + "\t" + "jet_850" + "\t\t" + "trough_1000" + "\t" + "trough_500" + "\t" +"subHigh_500" + "\t" +"fit_num"+"\n");
		System.out.print(low_surface_id +"\t\t"+ low_850_id + "\t\t" + vortex_850_id + "\t\t" + shear_850_id + "\t\t" + jet_850_id + "\t\t" + trough_1000_id + "\t\t" + trough_500_id + "\t\t" +subHigh_500_id +"\t\t" + fit_num+ "\n");
		
	}

}
