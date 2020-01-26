package recutil;
import recutil.WeatherJudgeUtil;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class WeatherSystems {
	public String type = "";
	public int level;
	public Map<Integer, SystemFeature>  features = new HashMap<Integer,SystemFeature>();
	public GridData value;
	public GridData ids;
	
	public WeatherSystems(String type0,int level0){
		type = type0;
		level = level0;
	}
	
	public WeatherSystems copy() {
	
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);
			try {
				return (WeatherSystems) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/*

	public void writeFeatures(String fileName) {
		DecimalFormat datafmt = new DecimalFormat("0.000");
		DecimalFormat datafmt1 = new DecimalFormat("0.0");
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(new File(fileName)),"GBK");
			BufferedWriter br=new BufferedWriter(fos);
			String str="diamond 14 "+fileName;
			br.write(str);
			str="\n2017 08 12 16 0\n";
			br.write(str);
			//杈撳嚭lines
			br.write("LINES: 0\n");
			Set<Integer> keys = this.features.keySet();
			
			//杈撳嚭trough
			
			if(this.type.equals("切变线")) {
				br.write("LINES_SYMBOL: "+0+"\n");
			}
			else {
				int nline0 = this.features.size();
				//keys = this.features.keySet();
				int nline1 = 0;
				for(Integer i : keys){
					if(this.features.get(i).axes.point.size() >0) {
						nline1 +=1;
					}
				}
				
				
				
				br.write("LINES_SYMBOL: "+nline1+"\n");
				//keys = this.features.keySet();
				for(Integer i : keys){
					if(this.features.get(i).axes.point.size() >0) {
						br.write("0 4 "+this.features.get(i).axes.point.size());
						for(int j=0;j<this.features.get(i).axes.point.size();j++){
							if(j%4==0)br.write("\n");
							float [] p1=(float[]) this.features.get(i).axes.point.get(j);
							br.write("   "+datafmt.format(p1[0]));
							br.write("   "+datafmt.format(p1[1]));
							if(j==0){
								br.write("         1");
							}
							else{
								br.write("     0.000");
							}
						}
						br.write("\nNoLabel 0\n");
					}
				}
			}

			//杈撳嚭symbol
			br.write("SYMBOLS: "+this.features.size()*1+"\n");
			for(Integer i : keys){
				if(this.features.get(i).getFeature("strenght")>0) {
					br.write("  160  ");
				}
				else {
					br.write("  161  ");
				}
				br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLon));
				br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLat));
				br.write("  0");
				br.write("   " +datafmt1.format(this.features.get(i).centrePoint.ptVal)+"\n");
			}	
			//for(Integer i : keys){
			//	br.write("  48  ");
			//	br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLon));
			//	br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLat));
			//	br.write("  1");
			//	br.write("   " +datafmt1.format(this.features.get(i).centrePoint.ptVal)+"\n");
			//}
			
			br.write("CLOSED_CONTOURS: 0\n");
			br.write("STATION_SITUATION\n");
			br.write("WEATHER_REGION:  0\n"); 
			br.write("FILLAREA:  0\n");
			br.write("NOTES_SYMBOL: "+0+"\n");
			
			br.write("NOTES_SYMBOL: "+0+"\n");
			//br.write("NOTES_SYMBOL: "+this.features.size()*1+"\n");
			//for(Integer i : keys){
			//	br.write("48  ");
			//	br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLon));
			//	br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLat));
			//	br.write("  0 5");
			//	br.write("   " +datafmt1.format(this.features.get(i).centrePoint.ptVal)+" 0 10 simhei.ttf 16 1 255 255 0 0\n");
			//}	
			br.write("WithProp_LINESYMBOLS: "+this.features.size()+"\n");
			String line_type = "";
			if (this.type.equals("槽线") || this.type.equals("副高脊线")) {
				line_type = "0 4 255 192 0 0 0 0\n";
			}
			else if(this.type.equals("切变线")) {
				line_type = "1102 2 255 255 0 0 0 0\n";
			}
			
			
			for(Integer i : keys){
				br.write(line_type+this.features.get(i).axes.point.size());
				for(int j=0;j<this.features.get(i).axes.point.size();j++){
					if(j%4==0)br.write("\n");
					float [] p1=(float[]) this.features.get(i).axes.point.get(j);
					br.write("   "+datafmt.format(p1[0]));
					br.write("   "+datafmt.format(p1[1]));
					br.write("     0.000");
				}
				br.write("\nNoLabel 0\n");
			}
			br.flush();
			fos.close();
		} catch (Exception e) {
			// TODO 鑷姩鐢熸垚鐨� catch 鍧�
			System.out.println(fileName+"鍐欏叆澶辫触");
			
		}	
	
	}

	*/
	
	public void writeAbstract(String fileName, String time) {
		Set<Integer> keys = this.features.keySet();
		String str = "id" + "\t" + "centre_x" +"\t"+ "centre_y" + "\t" + "axes_dir" + "\t" + "axes_len" + "\t" + "area" + "\t"+"strenght"+"\n";
		for(Integer i : keys) {
			float centre_x = this.features.get(i).centrePoint.ptLon;
			float centre_y = this.features.get(i).centrePoint.ptLat;
			if(this.features.get(i).axes.point.size()==0)continue;
			float[] p0 = this.features.get(i).axes.point.get(0);
			int num = this.features.get(i).axes.point.size();
			float[] p1 = this.features.get(i).axes.point.get(num-1);
			float axes_dir ;
			float dx = p1[0] - p0[0];
			float dy = p1[1] - p0[1];
			float dis = (float) Math.sqrt(dx * dx + dy * dy);
			
			if(dy >=0) {
				axes_dir = (float) (Math.acos(dx/dis)* 180 / Math.PI);
			}
			else{
				axes_dir = -(float)(Math.acos(dx/dis) * 180 / Math.PI);
			}
			
			float axes_lenght = dis;
			float area = 0;
			if(this.features.get(i).features.containsKey("area")) {
				area = this.features.get(i).features.get("area");
			}
			float strenght = 0;
			if(this.features.get(i).features.containsKey("strenght")) {
				strenght = this.features.get(i).features.get("strenght");
			}
			
			str += i + "\t" +  centre_x + "\t" + centre_y +"\t" + (int)axes_dir + "\t" + String.format("%.1f", axes_lenght) 
			+ "\t"+ String.format("%.1f", area)+"\t" + String.format("%.1f", strenght) + "\n";
			
		}
		
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(new File(fileName)),"GBK");
			BufferedWriter br=new BufferedWriter(fos);
			br.write(str);
			br.close();
		}
		catch (Exception e) {
			// TODO 鑷姩鐢熸垚鐨� catch 鍧�
			System.out.println(fileName+"文件输出失败");

		}
		
	}
	
	
	public void writeFeatures(String fileName,String time) {
		DecimalFormat datafmt = new DecimalFormat("0.000");
		DecimalFormat datafmt1 = new DecimalFormat("0.0");
		File file = new File(fileName);
		File dir = file.getParentFile();
		if(!dir.exists()){
			dir.mkdirs();
		}
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(new File(fileName)),"GBK");
			BufferedWriter br=new BufferedWriter(fos);
			int end=file.getName().length();
			int start=Math.max(0, end-16);
			String header = time.substring(0,4) + "年" + time.substring(4,6) +"月" + time.substring(6,8) +"日" + time.substring(8,10) + "时" + level + "hPa" + type+"feature";
			String str="diamond 14 "+header;
			br.write(str);
			
			str="\n"+time.substring(0,4)+" "+time.substring(4,6)+" "
					+time.substring(6,8)+" "+time.substring(8,10)+" 0\n";
			br.write(str);
			//杈撳嚭lines
			br.write("LINES: 0\n");



			//杈撳嚭trough
			Set<Integer> keys = this.features.keySet();
			int nline1 = 0;
			for(Integer i : keys){
				if(this.features.get(i).axes.point.size() >0) {
					nline1 +=1;
				}
			}
			

			if(this.type.equals("切变线") || this.type.equals("急流")) {
				br.write("LINES_SYMBOL: "+0+"\n");
			}
			else {	
				
				//杈撳嚭trough
				br.write("LINES_SYMBOL: "+nline1+"\n");
				keys = this.features.keySet();
				for(Integer i : keys){
					if(this.features.get(i).axes.point.size() >0) {
						br.write("0 4 "+this.features.get(i).axes.point.size());
						for(int j=0;j<this.features.get(i).axes.point.size();j++){
							if(j%4==0)br.write("\n");
							float [] p1=(float[]) this.features.get(i).axes.point.get(j);
		
							br.write("   "+datafmt.format(p1[0]));
							br.write("   "+datafmt.format(p1[1]));
		
							br.write("     0.000");
		//					if(j==0){
		//						br.write("         1");
		//					}
		//					else{
		//						br.write("     0.000");
		//					}
						}
						br.write("\nNoLabel 0\n");
					}
					
				}
			}

			//杈撳嚭symbol
	
			boolean need_output_centrePoint_and_strenth = false;

			if(!this.type.equals("槽线") && !this.type.equals("切变线") && !this.type.equals("急流")) {
				need_output_centrePoint_and_strenth = true;
			}
			
			if(!need_output_centrePoint_and_strenth) {	
				br.write("SYMBOLS: "+0+"\n");
			}
			else {
				if(this.type.contains("高")) {
					br.write("SYMBOLS: "+this.features.size()*1+"\n");
					if(this.level < 1000) {
						//System.out.println("*");
						for(Integer i : keys){
							if(this.features.get(i).getFeature("strenght") >0 ) {
								br.write("  160  ");
							}
							else {
								br.write("  161  ");
	
							}
							br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLon));
							br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLat));
							br.write("  0");
							br.write("   " +datafmt1.format(this.features.get(i).centrePoint.ptVal)+"\n");
						}	
					}
					else {
						for(Integer i : keys){
							if(this.features.get(i).getFeature("strenght") >0 ) {
								br.write("  60  ");
							}
							else {
								br.write("  61  ");
	
							}
							br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLon));
							br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLat));
							br.write("  0");
							br.write("  0\n");
						}	
						
					}
				}
				else {
					br.write("SYMBOLS: "+this.features.size()*1+"\n");
					for(Integer i : keys){
						br.write("  48  ");
						br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLon));
						br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLat));
						br.write("  1");
						br.write("   " +datafmt1.format(this.features.get(i).centrePoint.ptVal)+"\n");
					}
				}
				
			}

			br.write("CLOSED_CONTOURS: 0\n");
			br.write("STATION_SITUATION\n");
			br.write("WEATHER_REGION:  0\n");
			br.write("FILLAREA:  0\n");
			
			if(!need_output_centrePoint_and_strenth) {	
				br.write("NOTES_SYMBOL: "+0+"\n");
			}
			else {
				if(this.type.contains("高")) {
					br.write("NOTES_SYMBOL: "+0+"\n");
				}
				else {
					br.write("NOTES_SYMBOL: "+this.features.size()*1+"\n");
					for(Integer i : keys){
						br.write("48  ");
						br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLon));
						br.write("   "+datafmt.format(this.features.get(i).centrePoint.ptLat));
						br.write("  0 5");
						br.write("   " +datafmt1.format(this.features.get(i).centrePoint.ptVal)+" 0 10 simhei.ttf 16 1 255 255 0 0\n");
					}
				}
			}
			
			br.write("WithProp_LINESYMBOLS: "+nline1+"\n");
			
			String line_type = "";
			if (this.type.equals("槽线") || this.type.equals("副高")) {
				line_type = "0 4 255 192 0 0 0 0\n";
			}
			else if(this.type.equals("切变线")) {
				line_type = "1102 2 255 255 0 0 0 0\n";
			}
			else if(this.type.equals("急流")) {
				if(this.level == 700) {
					line_type = "1115 2 255 151 72 6 0 0\n";
				}
				else if(this.level == 850) {
					line_type = "1115 2 255 255 0 0 0 0\n";
				}
				else {
					line_type = "1115 2 255 0 255 0 0 0\n";
				}
			}
			
			for(Integer i : keys){
				if(this.features.get(i).axes.point.size() >0) {
					br.write(line_type+this.features.get(i).axes.point.size());
					for(int j=0;j<this.features.get(i).axes.point.size();j++){
						if(j%4==0)br.write("\n");
						float [] p1=(float[]) this.features.get(i).axes.point.get(j);
						br.write("   "+datafmt.format(p1[0]));
						br.write("   "+datafmt.format(p1[1]));
						br.write("     0.000");
					}
					br.write("\nNoLabel 0\n");
				}
			}
			br.flush();
			fos.close();
		} catch (Exception e) {
			// TODO 鑷姩鐢熸垚鐨� catch 鍧�
			System.out.println(fileName+"文件输出失败");

		}

	}


	public void writeIds(String string,String time) {
		if(this.ids!=null){
		// TODO Auto-generated method stub
			String header = time.substring(0,4) + "年" + time.substring(4,6) +"月" + time.substring(6,8) +"日" + time.substring(8,10) + "时" + level + "hPa" + type+"id";
			this.ids.writeToFile(string,header);
		}
	}

	public void setAxes( ArrayList<Line> lines) {
		// TODO Auto-generated method stub
		for(int i = 0; i<lines.size();i++){
			this.features.put(i+1,new SystemFeature(lines.get(i)));
		}
	}

	
	public void setCentres( ArrayList<Point> centrePoints) {
		// TODO Auto-generated method stub
		for(int i = 0; i<centrePoints.size();i++){
			this.features.put(i+1,new SystemFeature(centrePoints.get(i)));
		}
	}
	
	public void setValue(GridData value0) {
		// TODO Auto-generated method stub
		this.value = value0;
	}
	

	public void setIds(GridData ids0) {
		// TODO Auto-generated method stub
		this.ids = ids0;
	}
	
	public void reset(){

		//灏嗙綉鏍煎満id锛堝嵆绯荤粺鐨勮寖鍥达級鍜岀郴缁熺殑鐗瑰緛feature 杩涜鍏宠仈
		//
		if(this.value != null && this.ids != null){
			if(this.features.size()==0){
				//濡傛灉娌℃湁宸茬粡璁＄畻鐨勭郴缁熺壒寰佸睘鎬э紝灏辨牴鎹甶ds鐨勫垎甯冭绠楁瘡涓郴缁熺殑鏈�澶у�间腑蹇冧綅缃拰鍙栧��
				this.features = SystemIdentification.getCentreAreaStrenght(value, ids);
			}
			else{
			
				
				Map<Integer, SystemFeature> centreFeatures;
				//鐢� 杞寸嚎axes 灏� ids 鐨勫垎鍖鸿繘琛屼覆鑱�
				Set<Integer> keys = this.features.keySet();	
				Iterator<Integer> iter = keys.iterator();
				Integer onekey = iter.next();
				if(this.features.get(onekey).axes.point.size()!=0){
					int maxAxesId =0;
				
					for(Integer i : keys){
						if(i>maxAxesId)maxAxesId = i;
					}
					//鍏堝皢鎵�鏈夊垎鍖篿d璁剧疆寰楁瘮杞寸嚎id澶�
					for(int i=0;i<ids.gridInfo.nlon;i++){
						for(int j=0;j<ids.gridInfo.nlat;j++){
							if(ids.dat[i][j]>0)ids.dat[i][j]+=maxAxesId;
						}
					}
					centreFeatures = SystemIdentification.getCentreAreaStrenght(value, ids);
					Set <Integer> centKeys = centreFeatures.keySet();
					float xx,yy,dis2,mindis2;
					int minj=0,ig,jg,ig1,jg1;
					Set <Integer> effectiveAxesKeys = new HashSet <Integer>();
					
					for(Integer i:centKeys){
						//瀵逛簬姣忎釜鏍肩偣鍒嗗尯id鐨勪腑蹇冪偣锛屾壘鍒板搴旂殑鏈�杩戠殑杞寸嚎锛屽皢鍒嗗尯id鏀逛负杞寸嚎id
						Point cp = centreFeatures.get(i).centrePoint;
						mindis2=9999;
					
						for(Integer j:keys){
							for(int k=0;k<this.features.get(j).axes.point.size();k++){
								xx = this.features.get(j).axes.point.get(k)[0];
								yy = this.features.get(j).axes.point.get(k)[1];
								dis2 = MyMath.dis2(cp.ptLon, cp.ptLat, xx, yy);
								if(dis2<mindis2){
									mindis2 = dis2;
									minj = j;
									
								}
							}
						}
						//鍒ゆ柇鏈�杩戣酱绾挎槸鍚︾粡杩囧垎鍖篿
						
						boolean cross = false;
						for(int k=0;k<this.features.get(minj).axes.point.size();k++){
							xx = this.features.get(minj).axes.point.get(k)[0];
							yy = this.features.get(minj).axes.point.get(k)[1];
							ig = (int) ((xx-ids.gridInfo.startlon)/ids.gridInfo.dlon);
							jg = (int) ((yy-ids.gridInfo.startlat)/ids.gridInfo.dlat);
							if(ids.dat[ig][jg] == i){
								cross = true;
								break;
							}
						}
						if(cross){
							ig = (int) ((cp.ptLon-ids.gridInfo.startlon)/ids.gridInfo.dlon);
							jg = (int) ((cp.ptLat-ids.gridInfo.startlat)/ids.gridInfo.dlat);
							SystemIdentification.resetId(ids, i, minj, ig, jg);  
							effectiveAxesKeys.add(minj);
							
							
				
						}
					}
					
					//鍒犻櫎鏃犺酱绾跨殑鍒嗗尯
					for(int i=0;i<ids.gridInfo.nlon;i++){
						for(int j=0;j<ids.gridInfo.nlat;j++){
							if(ids.dat[i][j]>maxAxesId)ids.dat[i][j]=0;
						}
					}
					
					//鍒犻櫎鏃犲搴斿垎鍖虹殑杞寸嚎
			       Iterator<Entry<Integer, SystemFeature>> it = this.features.entrySet().iterator();  
			        while(it.hasNext()){  
			            Entry<Integer, SystemFeature> entry = it.next();  
			            if(!effectiveAxesKeys.contains(entry.getKey())){ 
			                it.remove();//浣跨敤杩唬鍣ㄧ殑remove()鏂规硶鍒犻櫎鍏冪礌  
			             }
			        }  
			     // 鍒犻櫎杞寸嚎瓒呭嚭id鑼冨洿鐨勯儴鍒�    
			       it = this.features.entrySet().iterator();  
			        while(it.hasNext()){  
			            Entry<Integer, SystemFeature> entry = it.next();  
						for(int k=entry.getValue().axes.point.size()-1;k>=0;k--){
							xx = entry.getValue().axes.point.get(k)[0];
							yy = entry.getValue().axes.point.get(k)[1];
							ig = (int) ((xx-ids.gridInfo.startlon)/ids.gridInfo.dlon);
							jg = (int) ((yy-ids.gridInfo.startlat)/ids.gridInfo.dlat);
							ig1 = ig+1;
							if(ig1 >= ids.gridInfo.nlon)ig1 = ids.gridInfo.nlon - 1;
							jg1 = jg + 1;
							if(jg1 >= ids.gridInfo.nlat)jg1 = ids.gridInfo.nlat - 1;
							if(ids.dat[ig][jg] != entry.getKey() && ids.dat[ig1][jg] != entry.getKey() &&
									ids.dat[ig][jg1] != entry.getKey() && ids.dat[ig1][jg1] != entry.getKey()){
								entry.getValue().axes.point.remove(k);
							}
						}
						
			        }  
				
			        
				}

			
				
				// 鏍规嵁鏂扮殑id鍒嗗竷璁＄畻涓績鐐瑰睘鎬�
				centreFeatures = SystemIdentification.getCentreAreaStrenght(value, ids);
				//濡傛灉鍘熸潵涓績鐐瑰睘鎬т笉瀛樺湪锛屽垯鐢ㄦ柊鐨勬浛鎹�
				Set<Integer> newkeys = centreFeatures.keySet();
				keys = this.features.keySet();
				for(Integer i:newkeys){
					if(!this.features.containsKey(i)){
						this.features.put(i, centreFeatures.get(i));
					}
					
					else {
						SystemFeature sf = this.features.get(i);
						if(sf.centrePoint.ptVal==0){
							sf.setCentrePoint(centreFeatures.get(i).centrePoint.copy());
						}
						if(!sf.features.containsKey("area")){
							sf.features.put("area", centreFeatures.get(i).getFeature("area"));
						}
						if(!sf.features.containsKey("strenght")){
							sf.features.put("strenght", centreFeatures.get(i).getFeature("strenght"));
						}
					}
				}
			}
		}
		return;
	}

	public void reset1(){

		//灏嗙綉鏍煎満id锛堝嵆绯荤粺鐨勮寖鍥达級鍜岀郴缁熺殑鐗瑰緛feature 杩涜鍏宠仈
		//
		if(this.value != null && this.ids != null){
			if(this.features.size()==0){
				//濡傛灉娌℃湁宸茬粡璁＄畻鐨勭郴缁熺壒寰佸睘鎬э紝灏辨牴鎹甶ds鐨勫垎甯冭绠楁瘡涓郴缁熺殑鏈�澶у�间腑蹇冧綅缃拰鍙栧��
				this.features = SystemIdentification.getCentreAreaStrenght(value, ids);
			}
			else{
			
				
				Map<Integer, SystemFeature> centreFeatures;
				//鐢� 杞寸嚎axes 灏� ids 鐨勫垎鍖鸿繘琛屼覆鑱�
				Set<Integer> keys = this.features.keySet();	
				Iterator<Integer> iter = keys.iterator();
				Integer onekey = iter.next();
				if(this.features.get(onekey).axes.point.size()!=0){
					int maxAxesId =0;
				
					for(Integer i : keys){
						if(i>maxAxesId)maxAxesId = i;
					}
					//鍏堝皢鎵�鏈夊垎鍖篿d璁剧疆寰楁瘮杞寸嚎id澶�
					for(int i=0;i<ids.gridInfo.nlon;i++){
						for(int j=0;j<ids.gridInfo.nlat;j++){
							if(ids.dat[i][j]>0)ids.dat[i][j]+=maxAxesId;
						}
					}
					centreFeatures = SystemIdentification.getCentreAreaStrenght(value, ids);
					Set <Integer> centKeys = centreFeatures.keySet();
					float xx,yy,dis2,mindis2;
					int minj=0,ig,jg;
					Set <Integer> effectiveAxesKeys = new HashSet <Integer>();
					
					for(Integer i:centKeys){
						//瀵逛簬姣忎釜鏍肩偣鍒嗗尯id鐨勪腑蹇冪偣锛屾壘鍒板搴旂殑鏈�杩戠殑杞寸嚎锛屽皢鍒嗗尯id鏀逛负杞寸嚎id
						Point cp = centreFeatures.get(i).centrePoint;
						mindis2=9999;
					
						for(Integer j:keys){
							for(int k=0;k<this.features.get(j).axes.point.size();k++){
								xx = this.features.get(j).axes.point.get(k)[0];
								yy = this.features.get(j).axes.point.get(k)[1];
								dis2 = MyMath.dis2(cp.ptLon, cp.ptLat, xx, yy);
								if(dis2<mindis2){
									mindis2 = dis2;
									minj = j;
									
								}
							}
						}
						//鍒ゆ柇鏈�杩戣酱绾挎槸鍚︾粡杩囧垎鍖篿
						
						boolean cross = false;
						for(int k=0;k<this.features.get(minj).axes.point.size();k++){
							xx = this.features.get(minj).axes.point.get(k)[0];
							yy = this.features.get(minj).axes.point.get(k)[1];
							ig = (int) ((xx-ids.gridInfo.startlon)/ids.gridInfo.dlon);
							jg = (int) ((yy-ids.gridInfo.startlat)/ids.gridInfo.dlat);
							if(ids.dat[ig][jg] == i){
								cross = true;
								break;
							}
						}
						if(cross){
							ig = (int) ((cp.ptLon-ids.gridInfo.startlon)/ids.gridInfo.dlon);
							jg = (int) ((cp.ptLat-ids.gridInfo.startlat)/ids.gridInfo.dlat);
							SystemIdentification.resetId(ids, i, minj, ig, jg);  
							effectiveAxesKeys.add(minj);
							
							
				
						}
					}
					
					//鍒犻櫎鏃犺酱绾跨殑鍒嗗尯
					for(int i=0;i<ids.gridInfo.nlon;i++){
						for(int j=0;j<ids.gridInfo.nlat;j++){
							if(ids.dat[i][j]>maxAxesId)ids.dat[i][j]=0;
						}
					}
					
				
			        
				}

			
				
				// 鏍规嵁鏂扮殑id鍒嗗竷璁＄畻涓績鐐瑰睘鎬�
				centreFeatures = SystemIdentification.getCentreAreaStrenght(value, ids);
				//濡傛灉鍘熸潵涓績鐐瑰睘鎬т笉瀛樺湪锛屽垯鐢ㄦ柊鐨勬浛鎹�
				Set<Integer> newkeys = centreFeatures.keySet();
				keys = this.features.keySet();
				for(Integer i:newkeys){
					if(!this.features.containsKey(i)){
						this.features.put(i, centreFeatures.get(i));
					}
					
					else {
						SystemFeature sf = this.features.get(i);
						if(sf.centrePoint.ptVal==0){
							sf.setCentrePoint(centreFeatures.get(i).centrePoint.copy());
						}
						if(!sf.features.containsKey("area")){
							sf.features.put("area", centreFeatures.get(i).getFeature("area"));
						}
						if(!sf.features.containsKey("strenght")){
							sf.features.put("strenght", centreFeatures.get(i).getFeature("area"));
						}
					}
				}
			}
		}
		return;
	}
	
	
	public void writeValues(String string,String time) {
		// TODO Auto-generated method stub
		if(this.value!=null){
			// TODO Auto-generated method stub
				String header = time.substring(0,4) + "年" + time.substring(4,6) +"月" + time.substring(6,8) +"日" + time.substring(8,10) + "时" + level + "hPa" + type+"value";
				this.value.writeToFile(string,header);
			}
	}

	/**
	 * 鍒ゆ柇绾挎褰撲腑鏄惁鏈夌鍚堟潯浠剁殑澶╂皵绯荤粺
	 * @param type
	 * @param level
	 * @param p1
	 * @return
	 */
	private Set<Integer> getLineJudge(String type,int level,float[] p1)
	{
		Set<Integer> weatherResult=new HashSet<>();
		if("SJET".equals(type))
		{
			weatherResult.addAll(WeatherJudgeUtil.getCodeByWind850SJET(p1));
		}
		if("SHEAR".equals(type))
		{
			weatherResult.addAll(WeatherJudgeUtil.getCodeByWind850SHEAR(p1));
		}
		if("VORTEX".equals(type))
		{
			weatherResult.addAll(WeatherJudgeUtil.getCodeByWind850Vortex(p1));
		}

		//1000 850 500楂樺害鍦轰俊鎭�
		if("RIDGE".equals(type))
		{
			switch (level){
				case 500:
					weatherResult.addAll(WeatherJudgeUtil.getCodeByHGT500_Ridge_Line(p1));
					break;
				case 850:
					weatherResult.addAll(WeatherJudgeUtil.getCodeByHGT850_Ridge_Line(p1));
					break;
			}
		}
		if("TROUGH".equals(type))
		{
			switch (level){
				case 500:
					weatherResult.addAll(WeatherJudgeUtil.getCodeByHGT500_Trough_Line(p1));
					break;
				case 850:
					weatherResult.addAll(WeatherJudgeUtil.getCodeByHGT850_Trough_Line(p1));
					break;
				case 1000:
					weatherResult.addAll(WeatherJudgeUtil.getCodeByHGT1000_Trough_Line(p1));
					break;
			}
		}

		return  weatherResult;
	}
	/**
	 * 鍒ゆ柇鐐瑰綋涓槸鍚︽湁绗﹀悎鏉′欢鐨勫ぉ姘旂郴缁�
	 * @param type
	 * @param level
	 * @param point
	 * @return
	 */
	private Set<Integer> getPointJudge(String type,int level,Point point)
	{
		Set<Integer> weatherResult=new HashSet<>();
		float[]p1={point.ptLon,point.ptLat};
		if("HIGHLOW".equals(type))
		{
			switch (level){
				case 500:
					weatherResult.addAll(WeatherJudgeUtil.getCodeByHGT500_Point(p1));
					break;
				case 850:
					weatherResult.addAll(WeatherJudgeUtil.getCodeByHGT850_Point(p1));
					break;
				case 1000:
					weatherResult.addAll(WeatherJudgeUtil.getCodeByHGT8700_Point(p1));
					break;
			}
		}
		return weatherResult;
	}

}
