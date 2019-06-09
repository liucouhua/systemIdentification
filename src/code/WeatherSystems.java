package code;
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
			//输出lines
			br.write("LINES: 0\n");
			
			
			//输出trough
			int nline = this.features.size();
			br.write("LINES_SYMBOL: "+nline+"\n");
			Set<Integer> keys = this.features.keySet();
			for(Integer i : keys){
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


			//输出symbol
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
			for(Integer i : keys){
				br.write("0 4 255 255 255 0 0 0\n"+this.features.get(i).axes.point.size());
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
			// TODO 自动生成的 catch 块
			System.out.println(fileName+"写入失败");
			
		}
	
	}

	public void writeIds(String string) {
		if(this.ids!=null){
		// TODO Auto-generated method stub
			this.ids.writeToFile(string);
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

		//将网格场id（即系统的范围）和系统的特征feature 进行关联
		//
		if(this.value != null && this.ids != null){
			if(this.features.size()==0){
				//如果没有已经计算的系统特征属性，就根据ids的分布计算每个系统的最大值中心位置和取值
				this.features = SystemIdentification.getCentreAreaStrenght(value, ids);
			}
			else{
			
				
				Map<Integer, SystemFeature> centreFeatures;
				//用 轴线axes 将 ids 的分区进行串联
				Set<Integer> keys = this.features.keySet();	
				Iterator<Integer> iter = keys.iterator();
				Integer onekey = iter.next();
				if(this.features.get(onekey).axes.point.size()!=0){
					int maxAxesId =0;
				
					for(Integer i : keys){
						if(i>maxAxesId)maxAxesId = i;
					}
					//先将所有分区id设置得比轴线id大
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
						//对于每个格点分区id的中心点，找到对应的最近的轴线，将分区id改为轴线id
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
						//判断最近轴线是否经过分区i
						
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
					
					//删除无轴线的分区
					for(int i=0;i<ids.gridInfo.nlon;i++){
						for(int j=0;j<ids.gridInfo.nlat;j++){
							if(ids.dat[i][j]>maxAxesId)ids.dat[i][j]=0;
						}
					}
					
					//删除无对应分区的轴线
			       Iterator<Entry<Integer, SystemFeature>> it = this.features.entrySet().iterator();  
			        while(it.hasNext()){  
			            Entry<Integer, SystemFeature> entry = it.next();  
			            if(!effectiveAxesKeys.contains(entry.getKey())){ 
			                it.remove();//使用迭代器的remove()方法删除元素  
			             }
			        }  
			     // 删除轴线超出id范围的部分    
			       it = this.features.entrySet().iterator();  
			        while(it.hasNext()){  
			            Entry<Integer, SystemFeature> entry = it.next();  
						for(int k=entry.getValue().axes.point.size()-1;k>=0;k--){
							xx = entry.getValue().axes.point.get(k)[0];
							yy = entry.getValue().axes.point.get(k)[1];
							ig = (int) ((xx-ids.gridInfo.startlon)/ids.gridInfo.dlon);
							jg = (int) ((yy-ids.gridInfo.startlat)/ids.gridInfo.dlat);
							if(ids.dat[ig][jg] != entry.getKey()){
								entry.getValue().axes.point.remove(k);
							}
						}
						
			        }  
				
			        
				}

			
				
				// 根据新的id分布计算中心点属性
				centreFeatures = SystemIdentification.getCentreAreaStrenght(value, ids);
				//如果原来中心点属性不存在，则用新的替换
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

	public void reset1(){

		//将网格场id（即系统的范围）和系统的特征feature 进行关联
		//
		if(this.value != null && this.ids != null){
			if(this.features.size()==0){
				//如果没有已经计算的系统特征属性，就根据ids的分布计算每个系统的最大值中心位置和取值
				this.features = SystemIdentification.getCentreAreaStrenght(value, ids);
			}
			else{
			
				
				Map<Integer, SystemFeature> centreFeatures;
				//用 轴线axes 将 ids 的分区进行串联
				Set<Integer> keys = this.features.keySet();	
				Iterator<Integer> iter = keys.iterator();
				Integer onekey = iter.next();
				if(this.features.get(onekey).axes.point.size()!=0){
					int maxAxesId =0;
				
					for(Integer i : keys){
						if(i>maxAxesId)maxAxesId = i;
					}
					//先将所有分区id设置得比轴线id大
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
						//对于每个格点分区id的中心点，找到对应的最近的轴线，将分区id改为轴线id
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
						//判断最近轴线是否经过分区i
						
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
					
					//删除无轴线的分区
					for(int i=0;i<ids.gridInfo.nlon;i++){
						for(int j=0;j<ids.gridInfo.nlat;j++){
							if(ids.dat[i][j]>maxAxesId)ids.dat[i][j]=0;
						}
					}
					
				
			        
				}

			
				
				// 根据新的id分布计算中心点属性
				centreFeatures = SystemIdentification.getCentreAreaStrenght(value, ids);
				//如果原来中心点属性不存在，则用新的替换
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
	
	
	public void writeValues(String string) {
		// TODO Auto-generated method stub
		if(this.value!=null){
			// TODO Auto-generated method stub
				this.value.writeToFile(string);
			}
	}
}
