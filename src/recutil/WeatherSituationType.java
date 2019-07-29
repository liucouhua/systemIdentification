package recutil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.sun.javafx.collections.MappingChange.Map;

public class WeatherSituationType {
	
	public T_Front tFront;
	public T_SWVortexShear tSWVortex;
	public T_SW_NEVortex tSWNEVotex;
	public T_SummerReversedTrough tSrTrough;
	public T_Tythoon tTythoon;
	public HashMap<String, WeatherSystems> wss = new HashMap<String,WeatherSystems>();

	public WeatherSituationType(GridData hight1000,GridData hight850,GridData hight700,GridData hight500, VectorData wind850, VectorData wind700,
			VectorData wind500,ArrayList<TyphoonReport> typhoons) {
		
		// 识别出各层的基本天气
		
		// 地面低压
		
		String output_dir = "D:\\develop\\java\\201905-weahter_identification\\output\\";
		hight1000.smooth(3);
		hight1000.writeToFile(output_dir+"low_surface\\h1000.txt");
		WeatherSystems low_surface = SHighLowPressure.getHLCentres(hight1000, 1000, 1.0f,2.5f);
		wss.put("low_surface", low_surface);
		
		low_surface.writeIds(output_dir +"low_surface\\ids.txt", "2010042008");
		low_surface.writeFeatures(output_dir +"low_surface\\feature.txt", "2010042008");
		low_surface.writeValues(output_dir + "low_surface\\value.txt", "2010042008");
		
		//850hpa 高低压中心
		hight850.smooth(3);
		hight850.writeToFile(output_dir+"low_850\\h850.txt");
		WeatherSystems hl_850 = SHighLowPressure.getHLCentres(hight850, 850, 1.0f,4);
		wss.put("hl_850", hl_850);
		hl_850.writeIds(output_dir +"low_850\\ids.txt", "2010042008");
		hl_850.writeFeatures(output_dir +"low_850\\feature.txt", "2010042008");
		hl_850.writeValues(output_dir + "low_850\\value.txt", "2010042008");
		
		
		//700pa 高低压中心
		hight850.smooth(3);
		hight850.writeToFile(output_dir+"low_850\\h850.txt");
		WeatherSystems hl_700 = SHighLowPressure.getHLCentres(hight700, 700, 1.0f,4);
		wss.put("hl_700", hl_700);
		hl_850.writeIds(output_dir +"hl_700\\ids.txt", "2010042008");
		hl_850.writeFeatures(output_dir +"hl_700\\feature.txt", "2010042008");
		hl_850.writeValues(output_dir + "hl_700\\value.txt", "2010042008");
		
		//500hpa 高度场低涡
		hight500.smooth(3);
		hight500.writeToFile(output_dir+"low_500\\h500.txt");
		WeatherSystems low_500 = SHighLowPressure.getHLCentres(hight500, 1000, 1.0f,4f);
		wss.put("low_500", low_500);
		low_500.writeIds(output_dir +"low_500\\ids.txt", "2010042008");
		low_500.writeFeatures(output_dir +"low_500\\feature.txt", "2010042008");
		low_500.writeValues(output_dir + "low_500\\value.txt", "2010042008");
		
		
		//500副高
		hight500.writeToFile(output_dir+"subHigh_500\\h500.txt");
		WeatherSystems subHigh_500 = SSubtropicalHigh.getSubtropicalHigh(hight500, 500, 1.0f);
		wss.put("subHigh_500", subHigh_500);
		subHigh_500.writeIds(output_dir +"subHigh_500\\ids.txt", "2010042008");
		subHigh_500.writeFeatures(output_dir +"subHigh_500\\feature.txt", "2010042008");
		subHigh_500.writeValues(output_dir + "subHigh_500\\value.txt", "2010042008");
	
		//1000槽线识别（主要关注倒槽）
		hight1000.writeToFile(output_dir+"trough_1000\\h1000.txt");
		WeatherSystems trough_1000 = STrough.getTrough(hight1000, 1000, 1.0f);
		wss.put("trough_1000", trough_1000);
		trough_1000.writeIds(output_dir +"trough_1000\\ids.txt", "2010042008");
		trough_1000.writeFeatures(output_dir +"trough_1000\\feature.txt", "2010042008");
		trough_1000.writeValues(output_dir + "trough_1000\\value.txt", "2010042008");
		
		//500高空槽
		hight500.writeToFile(output_dir+"trough_500\\h500.txt");
		WeatherSystems trough_500 = STrough.getTrough(hight500, 500, 1.0f);
		wss.put("trough_500", trough_500);
		trough_500.writeIds(output_dir +"trough_500\\ids.txt", "2010042008");
		trough_500.writeFeatures(output_dir +"trough_500\\feature.txt", "2010042008");
		trough_500.writeValues(output_dir + "trough_500\\value.txt", "2010042008");
		
		
		//500hpa 涡旋
		wind500.writeToFile(output_dir +"vortex_500\\wind.txt", "2010042008");
		WeatherSystems vortex_500 = SVortex.getVortexCentres(wind500, 500, 1.0f);
		wss.put("vortex_500",vortex_500);
		vortex_500.writeIds(output_dir +"vortex_500\\ids.txt", "2010042008");
		vortex_500.writeFeatures(output_dir +"vortex_500\\feature.txt", "2010042008");
		vortex_500.writeValues(output_dir + "vortex_500\\value.txt", "2010042008");
		
		
		//850hpa 涡旋
		wind850.writeToFile(output_dir +"vortex_850\\wind.txt", "2010042008");
		WeatherSystems vortex_850 = SVortex.getVortexCentres(wind850, 850, 1.0f);
		wss.put("vortex_850", vortex_850);
		
		
		vortex_850.writeIds(output_dir +"vortex_850\\ids.txt", "2010042008");
		vortex_850.writeFeatures(output_dir +"vortex_850\\feature.txt", "2010042008");
		vortex_850.writeValues(output_dir + "vortex_850\\value.txt", "2010042008");
		
		//700hpa 涡旋
		wind700.writeToFile(output_dir +"vortex_700\\wind.txt", "2010042008");
		
		WeatherSystems vortex_700 = SVortex.getVortexCentres(wind700, 700, 1.0f);
		wss.put("vortex_700", vortex_700);
		vortex_700.writeIds(output_dir +"vortex_700\\ids.txt", "2010042008");
		vortex_700.writeFeatures(output_dir +"vortex_700\\feature.txt", "2010042008");
		vortex_700.writeValues(output_dir + "vortex_700\\value.txt", "2010042008");
		
		//850切变线
		
		wind850.writeToFile(output_dir +"shear_850\\wind.txt", "2010042008");
		WeatherSystems shear_850 = SShear.getShear(wind850, 850, 1.0f);
		wss.put("shear_850", shear_850);
		shear_850.writeIds(output_dir +"shear_850\\ids.txt", "2010042008");
		shear_850.writeFeatures(output_dir +"shear_850\\feature.txt", "2010042008");
		shear_850.writeValues(output_dir + "shear_850\\value.txt", "2010042008");
		
		
		//700切变线
		wind700.writeToFile(output_dir +"shear_700\\wind.txt", "2010042008");
		
		WeatherSystems shear_700 = SShear.getShear(wind700, 700, 1.0f);
		wss.put("shear_700", shear_700);
		shear_700.writeIds(output_dir +"shear_700\\ids.txt", "2010042008");
		shear_700.writeFeatures(output_dir +"shear_700\\feature.txt", "2010042008");
		shear_700.writeValues(output_dir + "shear_700\\value.txt", "2010042008");
		
		//850急流
		wind850.writeToFile(output_dir +"jet_850\\wind.txt", "2010042008");
		WeatherSystems jet_850 = SJet.getJet(wind850, 850, 1.0f);
		wss.put("jet_850", jet_850);
		jet_850.writeIds(output_dir +"jet_850\\ids.txt", "2010042008");
		jet_850.writeFeatures(output_dir +"jet_850\\feature.txt", "2010042008");
		jet_850.writeValues(output_dir + "jet_850\\value.txt", "2010042008");
		
		
		//700急流
		wind700.writeToFile(output_dir +"jet_700\\wind.txt", "2010042008");
		WeatherSystems jet_700 = SJet.getJet(wind700, 700, 1.0f);
		wss.put("jet_700", jet_700);
		jet_700.writeIds(output_dir +"jet_700\\ids.txt", "2010042008");
		jet_700.writeFeatures(output_dir +"jet_700\\feature.txt", "2010042008");
		jet_700.writeValues(output_dir + "jet_700\\value.txt", "2010042008");
		
		
		tFront = new T_Front (wss,typhoons);
		tSWVortex = new T_SWVortexShear(wss,typhoons);
		//tSrTrough = new T_SummerReversedTrough(wss,typhoons);
		//tSrTrough = new T_SummerReversedTrough(wss,typhoons);
		//tTythoon =  new T_Tythoon(wss,typhoons);
		//System.out.println();
	}
	
	public void write_to_file(String root_dir,Calendar time) {
		write_to_file_tFront(root_dir,time);
		
		write_to_file_tSEVortexShear(root_dir,time);
		
	}
	
	public void write_to_file_tFront(String root_dir,Calendar time) {
		//输出锋面气旋类天气系统
		String dir_tFront = root_dir + "tFront\\";
		File file = new File(dir_tFront);
		file.mkdir();
		file = new File(dir_tFront +"\\low_1000\\");
		file.mkdir(); 
		file = new File(dir_tFront +"\\vortex_850\\");
		file.mkdir(); 
		file = new File(dir_tFront +"\\shear_850\\");
		file.mkdir(); 
		file = new File(dir_tFront +"\\trough_500\\");
		file.mkdir(); 
		file = new File(dir_tFront +"\\trough_1000\\");
		file.mkdir(); 
		file = new File(dir_tFront +"\\jet_850\\");
		file.mkdir(); 
		file = new File(dir_tFront +"\\subHigh_500\\");
		file.mkdir(); 
		
		file = new File(dir_tFront+"abstract.txt");
		
		String[] strs = null;
		if(file.exists()) {
			FileInputStream in;
			try {
				in = new FileInputStream(file);
				byte[] readBytes = new byte[in.available()];
				String zz="\\n";
				Pattern pat=Pattern.compile(zz);
				in.read(readBytes);
				in.close();
				String str = new String(readBytes);
				strs = pat.split(str.trim());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String filename =MyMath.getFileNameFromCalendar(time);
		String ids_strs = tFront.low_surface_id +"\t\t"+
				tFront.low_850_id + "\t\t" + tFront.vortex_850_id + "\t\t" + 
				tFront.shear_850_id + "\t\t" + tFront.jet_850_id + "\t\t" + 
				tFront.trough_1000_id + "\t\t" + tFront.trough_500_id + "\t\t" +
				tFront.subHigh_500_id +"\t\t" + tFront.fit_num;
		
		HashMap<String, String> time_ids_map = new HashMap<String,String>();
		if(strs !=null) {
			if(strs.length >1) {
				String zz="\\s+";
				Pattern pat=Pattern.compile(zz);
				for(int i=1;i<strs.length;i++) {
					String[] time_ids = pat.split(strs[i],2);
					time_ids_map.put(time_ids[0],time_ids[1]);
				}
			}			
		}
		time_ids_map.put(filename.substring(2,10),ids_strs);
		
		Collection<String> keyset=time_ids_map.keySet();		 
		List<String> list = new ArrayList<String>(keyset);	
		Collections.sort(list);
		
		file = new File(dir_tFront+"abstract.txt");
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(file),"GBK");
			
			BufferedWriter br=new BufferedWriter(fos);
			String str = "datetime" + "\t" + "low_surface" +"\t"+ "low_850" + "\t\t" + "vortex_850" + "\t" + "shear_850" + "\t\t" + "jet_850" + "\t\t" + "trough_1000" + "\t" + "trough_500" + "\t" +"subHigh_500" + "\t" +"fit_num"+"\n";
			br.write(str);
			for (int i = 0; i < list.size(); i++) {
				str= list.get(i) + "\t" + time_ids_map.get(list.get(i)) +"\n";
				br.write(str);
			}
			br.flush();
			fos.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//输出地面低压和低涡
		WeatherSystems ws = null;		
		ws = wss.get("low_surface");
		WeatherSystems ws1 = get_relative_weatherSystems(ws,tFront.low_surface_id);
		ws1.writeIds(dir_tFront + "\\low_1000\\ids"+ filename, filename);
		ws1.writeValues(dir_tFront + "\\low_1000\\values"+ filename, filename);
		ws1.writeFeatures(dir_tFront + "\\low_1000\\features"+ filename, filename);
		
		ws = wss.get("hl_850");
		ws1 = get_relative_weatherSystems(ws,tFront.low_850_id);
		ws1.writeIds(dir_tFront + "\\low_850\\ids"+ filename, filename);
		ws1.writeValues(dir_tFront + "\\low_850\\values"+ filename, filename);
		ws1.writeFeatures(dir_tFront + "\\low_850\\features"+ filename, filename);

		ws = wss.get("vortex_850");
		ws1 = get_relative_weatherSystems(ws,tFront.vortex_850_id);
		ws1.writeIds(dir_tFront + "\\vortex_850\\ids"+ filename, filename);
		ws1.writeValues(dir_tFront + "\\vortex_850\\values"+ filename, filename);
		ws1.writeFeatures(dir_tFront + "\\vortex_850\\features"+ filename, filename);
		
		ws = wss.get("shear_850");
		ws1 = get_relative_weatherSystems(ws,tFront.shear_850_id);
		ws1.writeIds(dir_tFront + "\\shear_850\\ids"+ filename, filename);
		ws1.writeValues(dir_tFront + "\\shear_850\\values"+ filename, filename);
		ws1.writeFeatures(dir_tFront + "\\shear_850\\features"+ filename, filename);

		ws = wss.get("trough_500");
		ws1 = get_relative_weatherSystems(ws,tFront.trough_500_id);
		ws1.writeIds(dir_tFront + "\\trough_500\\ids"+ filename, filename);
		ws1.writeValues(dir_tFront + "\\trough_500\\values"+ filename, filename);
		ws1.writeFeatures(dir_tFront + "\\trough_500\\features"+ filename, filename);

		ws = wss.get("trough_1000");
		ws1 = get_relative_weatherSystems(ws,tFront.trough_1000_id);
		ws1.writeIds(dir_tFront + "\\trough_1000\\ids"+ filename, filename);
		ws1.writeValues(dir_tFront + "\\trough_1000\\values"+ filename, filename);
		ws1.writeFeatures(dir_tFront + "\\trough_1000\\features"+ filename, filename);

		ws = wss.get("subHigh_500");
		ws1 = get_relative_weatherSystems(ws,tFront.subHigh_500_id);
		ws1.writeIds(dir_tFront + "\\subHigh_500\\ids"+ filename, filename);
		ws1.writeValues(dir_tFront + "\\subHigh_500\\values"+ filename, filename);
		ws1.writeFeatures(dir_tFront + "\\subHigh_500\\features"+ filename, filename);
		
		
		ws = wss.get("jet_850");
		ws1 = get_relative_weatherSystems(ws,tFront.jet_850_id);
		ws1.writeIds(dir_tFront + "\\jet_850\\ids"+ filename, filename);
		ws1.writeValues(dir_tFront + "\\jet_850\\values"+ filename, filename);
		ws1.writeFeatures(dir_tFront + "\\jet_850\\features"+ filename, filename);
	}
	

	public void write_to_file_tSEVortexShear(String root_dir,Calendar time) {
		//输出锋面气旋类天气系统
		String dir_tSEVortexShear = root_dir + "tSEVortexShear\\";
		File file = new File(dir_tSEVortexShear);
		file.mkdir();
		file = new File(dir_tSEVortexShear +"\\low_850\\");
		file.mkdir(); 
		file = new File(dir_tSEVortexShear+"\\low_700\\");
		file.mkdir(); 
		file = new File(dir_tSEVortexShear +"\\shear_850\\");
		file.mkdir(); 
		file = new File(dir_tSEVortexShear +"\\high_850\\");
		file.mkdir(); 
		file = new File(dir_tSEVortexShear +"\\trough_500\\");
		file.mkdir(); 
		file = new File(dir_tSEVortexShear +"\\trough_1000\\");
		file.mkdir(); 
		file = new File(dir_tSEVortexShear +"\\jet_850\\");
		file.mkdir(); 
		file = new File(dir_tSEVortexShear +"\\subHigh_500\\");
		file.mkdir(); 
		
		file = new File(dir_tSEVortexShear+"abstract.txt");
		
		String[] strs = null;
		if(file.exists()) {
			FileInputStream in;
			try {
				in = new FileInputStream(file);
				byte[] readBytes = new byte[in.available()];
				String zz="\\n";
				Pattern pat=Pattern.compile(zz);
				in.read(readBytes);
				in.close();
				String str = new String(readBytes);
				strs = pat.split(str.trim());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String filename =MyMath.getFileNameFromCalendar(time);
		String ids_strs =tSWVortex.high_850_id +"\t\t"+
				tSWVortex.low_700_id +"\t\t"+ 
				tSWVortex.low_850_id + "\t\t"  + 
				tSWVortex.shear_850_id + "\t\t" + 
				tSWVortex.jet_850_id + "\t\t" + 
				tSWVortex.trough_1000_id + "\t\t" + 
				tSWVortex.trough_500_id + "\t\t" +
				tSWVortex.subHigh_500_id +"\t\t" + 
				tSWVortex.fit_num;
		
		HashMap<String, String> time_ids_map = new HashMap<String,String>();
		if(strs !=null) {
			if(strs.length >1) {
				String zz="\\s+";
				Pattern pat=Pattern.compile(zz);
				for(int i=1;i<strs.length;i++) {
					String[] time_ids = pat.split(strs[i],2);
					time_ids_map.put(time_ids[0],time_ids[1]);
				}
			}			
		}
		time_ids_map.put(filename.substring(2,10),ids_strs);
		
		Collection<String> keyset=time_ids_map.keySet();		 
		List<String> list = new ArrayList<String>(keyset);	
		Collections.sort(list);
		
		file = new File(dir_tSEVortexShear+"abstract.txt");
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(file),"GBK");
			
			BufferedWriter br=new BufferedWriter(fos);
			String str = "datetime" + "\t" + "high_850"+"\t\t"+"low_700" +"\t\t"+ "low_850" + "\t\t" + "shear_850" + "\t\t" + "jet_850" + "\t\t" + "trough_1000" + "\t" + "trough_500" + "\t" +"subHigh_500" + "\t" +"fit_num"+"\n";
			br.write(str);
			for (int i = 0; i < list.size(); i++) {
				str= list.get(i) + "\t" + time_ids_map.get(list.get(i)) +"\n";
				br.write(str);
			}
			br.flush();
			fos.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//输出地面低压和低涡
		WeatherSystems ws = null;		
		ws = wss.get("hl_700");
		WeatherSystems ws1 = get_relative_weatherSystems(ws,tSWVortex.low_700_id);
		ws1.writeIds(dir_tSEVortexShear + "\\low_700\\ids"+ filename, filename);
		ws1.writeValues(dir_tSEVortexShear + "\\low_700\\values"+ filename, filename);
		ws1.writeFeatures(dir_tSEVortexShear + "\\low_700\\features"+ filename, filename);
		
		
		ws = wss.get("hl_850");
		ws1 = get_relative_weatherSystems(ws,tSWVortex.low_850_id);
		ws1.writeIds(dir_tSEVortexShear + "\\low_850\\ids"+ filename, filename);
		ws1.writeValues(dir_tSEVortexShear + "\\low_850\\values"+ filename, filename);
		ws1.writeFeatures(dir_tSEVortexShear + "\\low_850\\features"+ filename, filename);

		
		ws = wss.get("hl_850");
		ws1 = get_relative_weatherSystems(ws,tSWVortex.high_850_id);
		ws1.writeIds(dir_tSEVortexShear + "\\high_850\\ids"+ filename, filename);
		ws1.writeValues(dir_tSEVortexShear + "\\high_850\\values"+ filename, filename);
		ws1.writeFeatures(dir_tSEVortexShear + "\\high_850\\features"+ filename, filename);
		
		
		ws = wss.get("shear_850");
		ws1 = get_relative_weatherSystems(ws,tSWVortex.shear_850_id);
		ws1.writeIds(dir_tSEVortexShear + "\\shear_850\\ids"+ filename, filename);
		ws1.writeValues(dir_tSEVortexShear + "\\shear_850\\values"+ filename, filename);
		ws1.writeFeatures(dir_tSEVortexShear + "\\shear_850\\features"+ filename, filename);

		ws = wss.get("trough_500");
		ws1 = get_relative_weatherSystems(ws,tSWVortex.trough_500_id);
		ws1.writeIds(dir_tSEVortexShear + "\\trough_500\\ids"+ filename, filename);
		ws1.writeValues(dir_tSEVortexShear + "\\trough_500\\values"+ filename, filename);
		ws1.writeFeatures(dir_tSEVortexShear + "\\trough_500\\features"+ filename, filename);

		ws = wss.get("trough_1000");
		ws1 = get_relative_weatherSystems(ws,tSWVortex.trough_1000_id);
		ws1.writeIds(dir_tSEVortexShear + "\\trough_1000\\ids"+ filename, filename);
		ws1.writeValues(dir_tSEVortexShear + "\\trough_1000\\values"+ filename, filename);
		ws1.writeFeatures(dir_tSEVortexShear + "\\trough_1000\\features"+ filename, filename);

		ws = wss.get("subHigh_500");
		ws1 = get_relative_weatherSystems(ws,tSWVortex.subHigh_500_id);
		ws1.writeIds(dir_tSEVortexShear + "\\subHigh_500\\ids"+ filename, filename);
		ws1.writeValues(dir_tSEVortexShear + "\\subHigh_500\\values"+ filename, filename);
		ws1.writeFeatures(dir_tSEVortexShear + "\\subHigh_500\\features"+ filename, filename);
		
		
		ws = wss.get("jet_850");
		ws1 = get_relative_weatherSystems(ws,tSWVortex.jet_850_id);
		ws1.writeIds(dir_tSEVortexShear + "\\jet_850\\ids"+ filename, filename);
		ws1.writeValues(dir_tSEVortexShear + "\\jet_850\\values"+ filename, filename);
		ws1.writeFeatures(dir_tSEVortexShear + "\\jet_850\\features"+ filename, filename);
	}
	
	
	public WeatherSystems get_relative_weatherSystems(WeatherSystems ws, int id) {
		WeatherSystems ws1 = new WeatherSystems(ws.type,ws.level);
		GridInfo gridInfo = ws.ids.gridInfo;
		GridData value = new GridData(gridInfo);
		GridData ids = new GridData(gridInfo);
		for (int i=0;i<gridInfo.nlon;i++) {
			for(int j=0;j<gridInfo.nlat;j++) {
				if(ws.ids.dat[i][j] == id) {
					ids.dat[i][j] = id;
					value.dat[i][j] = ws.value.dat[i][j];
				}
				
			}
		}
		if(ws.features.containsKey(id)){
			SystemFeature sf = ws.features.get(id);
			ws1.features.put(id,sf);
		}
		ws1.ids = ids;
		ws1.value = value;
		return ws1;
		
	}
	
}
