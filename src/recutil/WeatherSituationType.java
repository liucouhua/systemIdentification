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
		
		// ʶ�������Ļ�������
		
		// �����ѹ
		WeatherSystems low_surface = SHighLowPressure.getHLCentres(hight1000, 1000, 1.0f);
		wss.put("low_surface", low_surface);
		
		//500hpa �߶ȳ�����
		WeatherSystems low_500 = SHighLowPressure.getHLCentres(hight500, 1000, 1.0f);
		wss.put("low_500", low_500);
		
		//500����
		WeatherSystems subHigh_500 = SSubtropicalHigh.getSubtropicalHigh(hight500, 500, 1.0f);
		wss.put("subHigh_500", subHigh_500);
		
		//500�߿ղ�
		WeatherSystems trough_500 = STrough.getTrough(hight500, 500, 1.0f);
		wss.put("trough_500", trough_500);
		
		//500hpa ���� 
		WeatherSystems vortex_500 = SVortex.getVortexCentres(wind500, 500, 1.0f);
		wss.put("vortex_500",vortex_500);
		
		//850hpa ����
		WeatherSystems vortex_850 = SVortex.getVortexCentres(wind850, 850, 1.0f);
		wss.put("vortex_850", vortex_850);
		
		//700hpa ���� 
		WeatherSystems vortex_700 = SVortex.getVortexCentres(wind700, 700, 1.0f);
		wss.put("vortex_700", vortex_700);
		
		//850�б���
		WeatherSystems shear_850 = SVortex.getVortexCentres(wind850, 850, 1.0f);
		wss.put("shear_850", shear_850);
		
		//850����
		WeatherSystems jet_850 = SVortex.getVortexCentres(wind850, 850, 1.0f);
		wss.put("jet_850", jet_850);
		
		//700����
		WeatherSystems jet_700 = SVortex.getVortexCentres(wind700, 700, 1.0f);
		wss.put("jet_700", jet_700);
		
		tFront = new T_Front (wss,typhoons);
		tSWVortex = new T_SWVortex(wss,typhoons);
		tSrTrough = new T_SummerReversedTrough(wss,typhoons);
		tSrTrough = new T_SummerReversedTrough(wss,typhoons);
		tTythoon =  new T_Tythoon(wss,typhoons);
	}
	
	public void write_to_file() {
		
	}
	
}
