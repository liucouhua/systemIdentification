package recutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
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
								float r_12 = Float.parseFloat(strs[k + 12]);
								float move_direction = Float.parseFloat(strs[k+13]);
								float move_speed = Float.parseFloat(strs[k+14]);
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


	
}
