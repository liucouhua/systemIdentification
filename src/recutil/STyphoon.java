package recutil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.regex.Pattern;

public class STyphoon {
	
	public static ArrayList<float[]> read_typhoon_position(String cyc_root_dir,Calendar dati){
		
		ArrayList<float[]> current_typhons = new ArrayList<float[]>();
		String zz="\\s+";
		Pattern pat=Pattern.compile(zz);
		int year = dati.get(Calendar.YEAR);
		int month = dati.get(Calendar.MONTH);
		int day = dati.get(Calendar.DAY_OF_MONTH);
		int hour = dati.get(Calendar.HOUR_OF_DAY);
		int dati0_int = year * 1000000 + month * 10000 + day * 100 + hour;
		int current_year = year;
		
		for(int year_f = current_year-1; year_f <= current_year;year_f++) {
			for(int i = 1; i < 100; i ++) {
				String path = cyc_root_dir + String.format("%02d", year_f%100) +"" + String.format("%02d", i) + ".dat";
				File file = new File(path);
				int k = 0;
				if (file.exists()){
					FileInputStream in;
					try {
						in = new FileInputStream(file);
						byte[] readBytes = new byte[in.available()];
						in.read(readBytes);
						String text = new String(readBytes);
						String[] strs = pat.split(text.trim());
						String typhoon_discription = strs[2];
						String typhoon_name = strs[3];
						String typhoon_id = strs[4];
						String typhoon_centre_id  = strs[5];
						int line_num = Integer.parseInt(strs[6]);
						float[] typhoon1 = new float[5];
						for(int j = 0; j < line_num; j++) {
							k = 6 + j * 13;
							year = Integer.parseInt(strs[k + 1]);
							month = Integer.parseInt(strs[k + 2]) - 1;
							day = Integer.parseInt(strs[k + 3]);
							hour = Integer.parseInt(strs[k + 4]);
							int dhour = Integer.parseInt(strs[k + 5]);
							Calendar dati1 = Calendar.getInstance();
							dati1.set(year, month, day, hour, 0);
							dati1.add(Calendar.HOUR_OF_DAY, dhour);
							
							year = dati1.get(Calendar.YEAR);
							month = dati1.get(Calendar.MONTH);
							day = dati1.get(Calendar.DAY_OF_MONTH);
							hour = dati1.get(Calendar.HOUR_OF_DAY);
							int dati1_int = year * 1000000 + month * 10000 + day * 100 + hour;
							if(dati1_int == dati0_int) {
								
								float lon = Float.parseFloat(strs[k+6]);
								float lat = Float.parseFloat(strs[k+7]);
								float p_centre = Float.parseFloat(strs[k+8]);
								float max_wind_speed = Float.parseFloat(strs[k+9]);
								float r_7 = Float.parseFloat(strs[k+10]);
								float r_10 = Float.parseFloat(strs[k+11]);
							
								float move_direction = Float.parseFloat(strs[k+12]);
								//System.out.println(strs[k+13]);
								float move_speed = Float.parseFloat(strs[k+13]);
								typhoon1[0] = year_f *100 + i;
								typhoon1[1] = lon;
								typhoon1[2] = lat;
								typhoon1[3] = p_centre;
								typhoon1[4] = max_wind_speed;
							}	
						}
						if(typhoon1[0] != 0) {
							current_typhons.add(typhoon1);
						}
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					break;
				}
			}
		}
		
		return current_typhons;
	}


	public static boolean in_typhoon_area(Point point) {
	
		if(point.ptLon > 100 && point.ptLat < 21) {
			return true;
		}
		else if(point.ptLon > 122 && point.ptLat>=21 && point.ptLat < 31) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static boolean in_typhoon_area(float[] point) {
		
		if(point[0] > 100 && point[1] < 21) {
			return true;
		}
		else if(point[0] > 122 && point[1]>=21 && point[1] < 31) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static void creat_typhoon_report(String wind_850_root,String cyc_root_dir,Calendar dati,int dh_step_0,int dh_step_1, int dh_max) {
		
		//首先从文件中读取所有的台风
		Calendar dati_1 = (Calendar)dati.clone();
		dati_1.add(Calendar.HOUR_OF_DAY, -dh_step_0);
		ArrayList<float[]> current_typhons = read_typhoon_position(cyc_root_dir+"/",dati_1);
		
		ArrayList<VectorData> windlist = new ArrayList<VectorData>();
		for(int dh = 0;dh<= dh_max;dh += dh_step_1) {
			Calendar time = (Calendar) dati.clone();	
			time.add(Calendar.HOUR_OF_DAY, dh);
			boolean has = false;
			for(int ddh = 0; ddh< 240;ddh += dh_step_1){
				Calendar time1 = (Calendar) time.clone();
				time1.add(Calendar.HOUR_OF_DAY, -ddh);
				String fileName =MyMath.getFileNameFromCalendar(time1);
				String w850_path = wind_850_root +"/"+ fileName.substring(2,10) + "." + String.format("%03d", ddh);
				File file = new File(w850_path);
				if(file.exists()) {
					VectorData w850 = new VectorData(w850_path);
					windlist.add(w850);
					has = true;
					break;
				}
			}
			if(!has) {
				break;
			}
		}
		
		
		ArrayList<ArrayList<float[]>> trace_list= new ArrayList<ArrayList<float[]>> ();
		for(float[] p_1: current_typhons) {
			ArrayList<float[]> trace = new ArrayList<float[]>();
			trace.add(p_1);
			trace_list.add(trace);
		}
		
		int now_tyid = get_new_tyid(cyc_root_dir,dati);
		int now_year = (int)(now_tyid/100);
		int now_id = now_tyid % 100;
		
		//从逐时次的风场中识别出涡旋,判断这些涡旋中心是否和现有的台风位置一致，如果一致则将其加入台风的列表中		
		String output_dir = "D:\\develop\\java\\201905-weahter_identification\\output\\";
		String fileName =MyMath.getFileNameFromCalendar(dati);
		
		ArrayList<ArrayList<float[]>> new_trace_list= new ArrayList<ArrayList<float[]>> ();
		
		int num = windlist.size();
		for(int i=0;i<num;i++) {
			VectorData wind = windlist.get(i);
			WeatherSystems vo1 = SVortex.getVortexCentres(wind,850, 500);
			
			vo1.writeIds(output_dir +"vortex_850\\ids"+fileName+".txt", fileName);
			vo1.writeFeatures(output_dir +"vortex_850\\feature"+fileName+".txt", fileName);
			vo1.writeValues(output_dir + "vortex_850\\value"+fileName+".txt", fileName);
			
			Set<Integer> keys = vo1.features.keySet();
			for(Integer key :keys) {
				SystemFeature feature = vo1.features.get(key);
				Point point = feature.centrePoint;
				float[] pc = new float[2];
				pc[0] = point.ptLon;
				pc[1] = point.ptLat;
				float max_value = VectorMathod.getValue(vo1.value, pc);
				if(max_value < 600)continue;
				
				//如果是其他时刻，就要判断该点是否和前面已经被列入trace的涡旋中心位置临近。		
				boolean has = false;
				for(ArrayList<float[]> trace :trace_list) {
					if(trace.size() >i) {
						float[] cent = trace.get(i);
						float dis2 = (cent[1] - point.ptLon) * (cent[1] - point.ptLon) + ((cent[2] - point.ptLat)) * ((cent[2] - point.ptLat));
						float dis = (float)Math.sqrt(dis2);
						if(dis <3) {
							float[] cent1 = new float[5];
							cent1[0] = cent[0];
							cent1[1] = point.ptLon;
							cent1[2] = point.ptLat;
							trace.add(cent1);
							has = true;
							break;
						}
					}
					if(has){
						break;
					}
				}
				

				if(!has) {	
					if(i==0) {
						boolean in_area = in_typhoon_area(point);
						if(in_area) {
							System.out.println(max_value);
							ArrayList<float[]> trace = new ArrayList<float[]>();
							float[] cent0 = new float[5]; //为前一个时刻添加一个空的内容
							trace.add(cent0);
							float[] cent1 = new float[5];
							//cent1[0] = now_year *100 + now_id;
							cent1[1] = point.ptLon;
							cent1[2] = point.ptLat;
							trace.add(cent1);
							new_trace_list.add(trace);
							
						}
					}
					else {
						for(ArrayList<float[]> trace :new_trace_list) {
							if(trace.size() >i) {
								float[] cent = trace.get(i);
								float dis2 = (cent[1] - point.ptLon) * (cent[1] - point.ptLon) + ((cent[2] - point.ptLat)) * ((cent[2] -point.ptLat));
								float dis = (float)Math.sqrt(dis2);
								if(dis <3) {
									float[] cent1 = new float[5];
									cent1[0] = cent[0];
									cent1[1] = point.ptLon;
									cent1[2] = point.ptLat;
									trace.add(cent1);
									has = true;
									break;
								}
							}
							if(has){
								break;
							}
						}
					}
				}
				
			}	
		}
		
		//保留能持续的新trace
		for(ArrayList<float[]> trace :new_trace_list) {
			if(trace.size()>3) {
				for(float[] cent1 :trace) {
					cent1[0] = now_year *100 + now_id;
				}
				trace_list.add(trace);
				now_id += 1;
			}
		}
		
		
		//输出识别结果
		String zz="\\s+";
		Pattern pat=Pattern.compile(zz);
		for(ArrayList<float[]> trace : trace_list) {
			if(trace.size()<2)continue;
			float[] cent1 = trace.get(1);
			int year_id = (int) cent1[0];
			int id = year_id %100;
			int year = (year_id - id)/100;
			String path = cyc_root_dir +"/"+ String.format("%02d", year%100)  + String.format("%02d", id) + ".dat";
			File file = new File(path);
			String[] strs = null;
			int line_num1 = trace.size() - 1;
			int line_num = 0;
			if(file.exists()) {
				try {
					FileInputStream in = new FileInputStream(file);
					byte[] readBytes = new byte[in.available()];
					in.read(readBytes);
					in.close();
					String text = new String(readBytes);
					strs = pat.split(text.trim());
					line_num = Integer.parseInt(strs[6]);
					strs[6] =(line_num +  line_num1) +"";
					strs[2] = year + "_"+id+"th_typhon_trace"+fileName;
				}
				catch(Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				strs = new String[7];
				strs[0] = "diamond";
				strs[1] = "7";
				strs[2] = year + "_"+id+"th_typhon_trace"+fileName;
				strs[3] = "Model";
				strs[4] = String.format("%02d", year%100) +"" + String.format("%02d", id);
				strs[5] = "0";
				strs[6] = line_num1 +"";
			}
			String text  = "";
			for(int i = 0;i<7;i++) {
				text += strs[i];
				if(i==2||i==6) {
					text += "\n";
				}
				else {
					text += " ";
				}
			}
			for(int i =0;i<line_num;i++) {
				for(int j = 0;j<13;j++) {
					text += strs[7 + i * 13 + j]+" ";
				}
				text += "\n";
			}
			
			for(int i = 0;i< line_num1; i++) {
				text += dati.get(Calendar.YEAR) + " ";
				text += dati.get(Calendar.MONTH)+1 + " ";
				text += dati.get(Calendar.DAY_OF_MONTH) + " ";
				text += dati.get(Calendar.HOUR_OF_DAY)+" ";
				text += i * dh_step_1 +" ";
				text += trace.get(i+1)[1] + " ";
				text += trace.get(i+1)[2] + " ";
				text += "0 0 0 0 0 0\n";
				
			}
			
			file = new File(path);
			try {
				OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(file),"GBK");			
				BufferedWriter br=new BufferedWriter(fos);
				br.write(text);	
				br.flush();
				fos.close();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}


	private static int get_new_tyid(String cyc_root_dir,Calendar dati) {
		// TODO Auto-generated method stub

		int year = dati.get(Calendar.YEAR);

		for(int i = 1; i < 100; i ++) {
			String path = cyc_root_dir +"/" + String.format("%02d", year%100)  + String.format("%02d", i) + ".dat";
			File file = new File(path);
			int k = 0;
			if (!file.exists()){
				return year * 100 + i;
			}
		}
		
		return year * 100 + 1;
	}
}
