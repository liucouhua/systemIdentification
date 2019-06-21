package recutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class T_Front {
	
	int low_surface_id = 0;
	
	
	public T_Front(HashMap<String, WeatherSystems> wss, ArrayList<TyphoonReport> typhoons) {
		// TODO Auto-generated constructor stub
		WeatherSystems low_surface  = wss.get("low_surface");
		WeatherSystems low_850 = wss.get("low_850");
		WeatherSystems vortex_850 = wss.get("votex_850");
		WeatherSystems trough_1000 = wss.get("trough_1000");
		WeatherSystems subHigh_500 = wss.get("subHigh_500");
		WeatherSystems jet_850 = wss.get("jet_850");
		WeatherSystems shear_850 = wss.get("shear_850");
		WeatherSystems trough_500 = wss.get("trough_500");
		
		
		
		//江淮气旋
		//识别地面低压中心，判断是否处于110°-120°，30-35°N
		WeatherSystems ws = low_surface;
		Set<Integer> keys = ws.features.keySet();
		for(Integer i : keys){
			if (i<0) {
				float cx = ws.features.get(i).centrePoint.ptLon;
				float cy = ws.features.get(i).centrePoint.ptLat;
				
				if(cx >=110 && cx <= 120 && cy >=30 && cy <= 35) {
					System.out.println("low_surface:1\n");
					low_surface_id = i;
				}
			}
		}
		
		//识别850h低压中心，判断是否处于110°-120°，30-35°N
		ws = low_850;
	    keys = ws.features.keySet();
		for(Integer i : keys){
			if (i<0) {
				float cx = ws.features.get(i).centrePoint.ptLon;
				float cy = ws.features.get(i).centrePoint.ptLat;
				
				if(cx >=110 && cx <= 120 && cy >=30 && cy <= 35) {
					System.out.println("low_850:1\n");
					low_surface_id = i;
				}
			}
		}
		
		
		//副高脊线
		ws = subHigh_500;
	    keys = ws.features.keySet();
		for(Integer i : keys){
			
			Line line = ws.features.get(i).getAxes();
			int npoint = line.point.size();
			float cx = 0,cy = 0;
			if(line.point.get(0)[0]>line.point.get(npoint - 1)[0]) {
				cx = line.point.get(npoint - 1)[0];
				cy = line.point.get(npoint - 1)[1];
			}
			else {
				cx = line.point.get(0)[0];
				cy = line.point.get(0)[1];
			}
			System.out.println(cy);
			if(cy >=20 && cy <= 25) {
				System.out.println("subHigh_500:1\n");
				low_surface_id = i;
			}
			
			
		}
		
		
		
		
		
		
	}

}
