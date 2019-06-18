package recutil;

import java.util.ArrayList;
import java.util.HashMap;

import com.sun.javafx.collections.MappingChange.Map;

public class WeatherSituationType {
	
	public T_Front tFront;
	public T_SWVortex tSWVortex;
	public T_SW_NEVortex tSWNEVotex;
	public T_SummerReversedTrough tSrTrough;
	public T_Tythoon tTythoon;
	public HashMap<String, WeatherSystems> wss = new HashMap<String,WeatherSystems>();
	
	
	public WeatherSituationType(GridData hight1000,GridData hight500, VectorData wind850, VectorData wind700,
			VectorData wind500,ArrayList<TyphoonReport> typhoons) {
		
		// 识别出各层的基本天气
		
		// 地面低压
		
		String output_dir = "D:\\develop\\java\\201905-weahter_identification\\output\\";
		hight1000.smooth(3);
		WeatherSystems low_surface = SHighLowPressure.getHLCentres(hight1000, 1000, 1.0f);
		wss.put("low_surface", low_surface);
		
		//low_surface.writeIds(output_dir +"ids.txt", "2010042008");
		//low_surface.writeFeatures(output_dir +"feature.txt", "2010042008");
		//low_surface.writeValues(output_dir + "value.txt", "2010042008");
		
		//500hpa 高度场低涡
		hight500.smooth(10);
		WeatherSystems low_500 = SHighLowPressure.getHLCentres(hight500, 1000, 1.0f);
		wss.put("low_500", low_500);
		//low_500.writeIds(output_dir +"ids.txt", "2010042008");
		//low_500.writeFeatures(output_dir +"feature.txt", "2010042008");
		//low_500.writeValues(output_dir + "value.txt", "2010042008");
		
		
		//500副高
		WeatherSystems subHigh_500 = SSubtropicalHigh.getSubtropicalHigh(hight500, 500, 1.0f);
		wss.put("subHigh_500", subHigh_500);
		//subHigh_500.writeIds(output_dir +"ids.txt", "2010042008");
		//subHigh_500.writeFeatures(output_dir +"feature.txt", "2010042008");
		//subHigh_500.writeValues(output_dir + "value.txt", "2010042008");
		
		//500高空槽
		WeatherSystems trough_500 = STrough.getTrough(hight500, 500, 1.0f);
		wss.put("trough_500", trough_500);
		//trough_500.writeIds(output_dir +"ids.txt", "2010042008");
		//trough_500.writeFeatures(output_dir +"feature.txt", "2010042008");
		//trough_500.writeValues(output_dir + "value.txt", "2010042008");
		
		
		//500hpa 涡旋 
		WeatherSystems vortex_500 = SVortex.getVortexCentres(wind500, 500, 1.0f);
		wss.put("vortex_500",vortex_500);
		//vortex_500.writeIds(output_dir +"ids.txt", "2010042008");
		//vortex_500.writeFeatures(output_dir +"feature.txt", "2010042008");
		//vortex_500.writeValues(output_dir + "value.txt", "2010042008");
		
		
		//850hpa 涡旋
		WeatherSystems vortex_850 = SVortex.getVortexCentres(wind850, 850, 1.0f);
		wss.put("vortex_850", vortex_850);
		
		vortex_850.writeIds(output_dir +"ids.txt", "2010042008");
		vortex_850.writeFeatures(output_dir +"feature.txt", "2010042008");
		vortex_850.writeValues(output_dir + "value.txt", "2010042008");
		
		//700hpa 涡旋 
		WeatherSystems vortex_700 = SVortex.getVortexCentres(wind700, 700, 1.0f);
		wss.put("vortex_700", vortex_700);
		vortex_700.writeIds(output_dir +"ids.txt", "2010042008");
		vortex_700.writeFeatures(output_dir +"feature.txt", "2010042008");
		vortex_700.writeValues(output_dir + "value.txt", "2010042008");
		
		//850切边线
		WeatherSystems shear_850 = SShear.getShear(wind850, 850, 1.0f);
		wss.put("shear_850", shear_850);
		shear_850.writeIds(output_dir +"ids.txt", "2010042008");
		shear_850.writeFeatures(output_dir +"feature.txt", "2010042008");
		shear_850.writeValues(output_dir + "value.txt", "2010042008");
		//700切边线
		WeatherSystems shear_700 = SShear.getShear(wind850, 850, 1.0f);
		wss.put("shear_700", shear_700);
		shear_700.writeIds(output_dir +"ids.txt", "2010042008");
		shear_700.writeFeatures(output_dir +"feature.txt", "2010042008");
		shear_700.writeValues(output_dir + "value.txt", "2010042008");
		
		//850急流
		WeatherSystems jet_850 = SJet.getJet(wind850, 850, 1.0f);
		wss.put("jet_850", jet_850);
		jet_850.writeIds(output_dir +"ids.txt", "2010042008");
		jet_850.writeFeatures(output_dir +"feature.txt", "2010042008");
		jet_850.writeValues(output_dir + "value.txt", "2010042008");
		
		
		//700急流
		WeatherSystems jet_700 = SJet.getJet(wind700, 700, 1.0f);
		wss.put("jet_700", jet_700);
		jet_700.writeIds(output_dir +"ids.txt", "2010042008");
		jet_700.writeFeatures(output_dir +"feature.txt", "2010042008");
		jet_700.writeValues(output_dir + "value.txt", "2010042008");
		
		
		tFront = new T_Front (wss,typhoons);
		tSWVortex = new T_SWVortex(wss,typhoons);
		tSrTrough = new T_SummerReversedTrough(wss,typhoons);
		tSrTrough = new T_SummerReversedTrough(wss,typhoons);
		tTythoon =  new T_Tythoon(wss,typhoons);
	}
	
	public void write_to_file() {
		
	}
	
}
