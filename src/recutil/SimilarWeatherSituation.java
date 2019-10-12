package recutil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Pattern;

public class SimilarWeatherSituation {
	
	private String dir_h1000;
	private String dir_w850;
	private String dir_w700;
	private String dir_h500;
	private String dir_typhoon;
	private String dir_vectors;
	private String dir_classificationModel;
	float centx,centy;
	
	public SimilarWeatherSituation(String dir_h1000, String dir_w850, String dir_w700, String dir_h500,String dir_typhoon,String dir_vectors,String dir_classificationModel,
		float centx,float centy) {
		this.dir_h1000 = dir_h1000;
		this.dir_w850 = dir_w850;
		this.dir_w700 = dir_w700;
		this.dir_h500 = dir_h500;
		this.dir_typhoon = dir_typhoon;
		this.dir_vectors = dir_vectors;
		this.dir_classificationModel = dir_classificationModel;
		this.centx = centx;
		this.centy = centy;
	}
	
	
	private float[] getNearestSystemPara(WeatherSystems ws, float centx1,float centy1) {
		
		GridInfo gf = ws.ids.gridInfo;
		float mindis2 = 999999;
		float dx,dy,dis;
		float minId = 0;
		for(int i = 0;i<gf.nlon;i++) {
			for(int j= 0;j<gf.nlat;j++) {
				if(ws.ids.dat[i][j] !=0) {
					dx = gf.startlon + i * gf.dlon - centx1;
					dy = gf.startlat + j * gf.dlat - centy1;
					dis = dx * dx + dy * dy;
					if(dis <mindis2) {
						minId = ws.ids.dat[i][j];
						mindis2 =dis;
					}
				}
			}
		}
		float[] para = new float[5];
		int key = (int) minId;
		if( ws.features.containsKey(key)){
			
			para[0] = ws.features.get(key).centrePoint.ptLon;
			para[1] = ws.features.get(key).centrePoint.ptLat;
			
			if(ws.features.get(key).features.containsKey("area")) {
				para[2] = ws.features.get(key).features.get("area");
			}
			if(ws.features.get(key).features.containsKey("strenght")) {
				para[3] = ws.features.get(key).features.get("strenght");
			}
			
			if (ws.features.get(key).axes.point.size()>1) {
				float[] p0 = ws.features.get(key).axes.point.get(0);
				int num = ws.features.get(key).axes.point.size();
				float[] p1 = ws.features.get(key).axes.point.get(num-1);
				float axes_dir ;
				dx = p1[0] - p0[0];
				dy = p1[1] - p0[1];
				dis = (float) Math.sqrt(dx * dx + dy * dy);
				
				if(dy >=0) {
					axes_dir = (float) (Math.acos(dx/dis)* 180 / Math.PI);
				}
				else{
					axes_dir = -(float)(Math.acos(dx/dis) * 180 / Math.PI);
				}
				para[4] = axes_dir;
			}
		}

		return para;
	}
	
	public void creatWeatherSituationVector_bunch(Calendar start,Calendar end, int dh) {
		//批量读取气压、风场和高度场数据，识别出各类天气系统，每一类天气系统只保留和关注点（centx,centy) 最近的一个，将其位置、面积、强度、轴向的特征输出到文件中
		//dir_h1000,1000hPa的高度场的路径
		//dir_w850,850hPa的风场的路径
		//dir_w700， 700hPa的风场路径
		//dir_h500 500hPa的高度场路径
		//start批量计算的起始日期
		//end 批量计算的结束日期
		//dh 批量计算的间隔（单位 小时）
		//centx，centy关注区域的中心点的位置
		//dir_vector 输出属性的路径
		
		Calendar time= (Calendar) start.clone();
		while(!time.after(end)){
			 
			String fileName =MyMath.getFileNameFromCalendar(time);
			Calendar time0 = (Calendar) time.clone();
			time.add(Calendar.HOUR, dh);
			String h1000_path =dir_h1000  +"\\" + fileName.substring(2,10)+".000";
			GridData h1000 = new GridData(h1000_path);
			String context = "system\t\t"+"centx\t"+"centy\t"+"area\t"+"strenght\t" + "direction\n";
			float[] para = new float[5];
			if(h1000.gridInfo == null) {
				continue;
			}
			else {
				h1000.smooth(3);
				WeatherSystems low_surface = SHighLowPressure.getHLCentres(h1000, 1000, 1.0f,2.5f);
				para = getNearestSystemPara(low_surface,centx,centy);
				context += "low_surface\t" + String.format("%8.2f", para[0]) + "\t" + String.format("%8.2f", para[1]) + "\t" +String.format("%8.2f", para[2]) + "\t"
						+String.format("%8.2f", para[3]) + "\t" +String.format("%8.2f", para[4]) + "\n";
				
			}
			
			String w850_path = dir_w850 +"\\" +fileName.substring(2,10)+".000";
			VectorData w850 = new VectorData(w850_path);
			w850.u.smooth(1);
			w850.v.smooth(1);
			if(w850.gridInfo == null) {
				continue;
			}
			else {
				WeatherSystems vortex_850 = SVortex.getVortexCentres(w850, 850, 1.0f);
				para = getNearestSystemPara(vortex_850,centx,centy);
				context += "vortex_850\t" + String.format("%8.2f", para[0]) + "\t" + String.format("%8.2f", para[1]) + "\t" +String.format("%8.2f", para[2]) + "\t"
						+String.format("%8.2f", para[3]) + "\t" +String.format("%8.2f", para[4]) + "\n";
				
				WeatherSystems jet_850 = SJet.getJet(w850, 850, 1.0f);
				para = getNearestSystemPara(jet_850,centx,centy);
				context += "jet_850\t\t" + String.format("%8.2f", para[0]) + "\t" + String.format("%8.2f", para[1]) + "\t" +String.format("%8.2f", para[2]) + "\t"
						+String.format("%8.2f", para[3]) + "\t" +String.format("%8.2f", para[4]) + "\n";			
				
				WeatherSystems shear_850 = SShear.getShear(w850, 850, 1.0f);
				
				para = getNearestSystemPara(shear_850,centx,centy);
				context += "shear_850\t\t" + String.format("%8.2f", para[0]) + "\t" + String.format("%8.2f", para[1]) + "\t" +String.format("%8.2f", para[2]) + "\t"
						+String.format("%8.2f", para[3]) + "\t" +String.format("%8.2f", para[4]) + "\n";
				
			}
			String w700_path = dir_w700  +"\\" + fileName.substring(2,10)+".000";
			VectorData w700 = new VectorData(w700_path);
			if(w700.gridInfo == null) {
				continue;
			}
			else {
				WeatherSystems jet_700 = SJet.getJet(w700, 700, 1.0f);
				para = getNearestSystemPara(jet_700,centx,centy);
				context += "jet_700\t\t" + String.format("%8.2f", para[0]) + "\t" + String.format("%8.2f", para[1]) + "\t" +String.format("%8.2f", para[2]) + "\t"
						+String.format("%8.2f", para[3]) + "\t" +String.format("%8.2f", para[4]) + "\n";
				WeatherSystems shear_700 = SShear.getShear(w700, 700, 1.0f);
				para = getNearestSystemPara(shear_700,centx,centy);
				context += "shear_700\t\t" + String.format("%8.2f", para[0]) + "\t" + String.format("%8.2f", para[1]) + "\t" +String.format("%8.2f", para[2]) + "\t"
						+String.format("%8.2f", para[3]) + "\t" +String.format("%8.2f", para[4]) + "\n";
			}
			String h500_path = dir_h500 +"\\"  +fileName.substring(2,10)+".000";
			GridData h500 = new GridData(h500_path);
			if(h500.gridInfo == null) {
				continue;
			}
			else {
				WeatherSystems subHigh_500 = SSubtropicalHigh.getSubtropicalHigh(h500, 500, 1.0f);
				para = getNearestSystemPara(subHigh_500,centx,centy);
				context += "subHigh_500\t" + String.format("%8.2f", para[0]) + "\t" + String.format("%8.2f", para[1]) + "\t" +String.format("%8.2f", para[2]) + "\t"
						+String.format("%8.2f", para[3]) + "\t" +String.format("%8.2f", para[4]) + "\n";
				
				WeatherSystems trough_500 = STrough.getTrough(h500, 500, 1.0f);
				para = getNearestSystemPara(trough_500,centx,centy);
				context += "trough_500\t" + String.format("%8.2f", para[0]) + "\t" + String.format("%8.2f", para[1]) + "\t" +String.format("%8.2f", para[2]) + "\t"
						+String.format("%8.2f", para[3]) + "\t" +String.format("%8.2f", para[4]) + "\n";
			}
			
			 ArrayList<float[]> typhoons = STyphoon.read_typhoon_position(dir_typhoon,time0);
			 para = new float[5];
			 float mindis2 = 999999;
			 float dx,dy,dis;
			 int minIndex =-1;
			 for(int i=0;i<typhoons.size();i++) {
				 dx = centx - typhoons.get(i)[1];
				 dy = centy - typhoons.get(i)[2];
				 dis = dx * dx + dy * dy;
				 if(dis < mindis2) {
					 mindis2 = dis;
					 minIndex = i;
				 }
			 }
			 if(minIndex>=0) {
					para[0] = typhoons.get(minIndex)[1];
					para[1] = typhoons.get(minIndex)[2];
			 }
			 context += "typhoon\t\t" + String.format("%8.2f",para[0] ) + "\t" + String.format("%8.2f",para[1]) + "\t" +String.format("%8.2f", 0.0f) + "\t"
						+String.format("%8.2f", 0.0f) + "\t" +String.format("%8.2f", 0.0f) + "\n";
			 String output_path = dir_vectors +"\\" +fileName.substring(2,10)+".txt";
			 File file = new File(output_path);
				try {
					OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(file),"GBK");			
					BufferedWriter br=new BufferedWriter(fos);
					br.write(context);
					br.flush();
					fos.close();
				}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
		

	}
	
	public  void creatWeatherSituationVector(Calendar time0) {
		//读取某一个时刻的读取气压、风场和高度场数据，识别出各类天气系统，每一类天气系统只保留和关注点（centx,centy) 最近的一个，将其位置、面积、强度、轴向的特征输出到文件中
		//dir_h1000,1000hPa的高度场的路径
		//dir_w850,850hPa的风场的路径
		//dir_w700， 700hPa的风场路径
		//dir_h500 500hPa的高度场路径
		//time0 需要计算的时刻
		//centx，centy关注区域的中心点的位置
		//dir_vector 输出属性的路径
		creatWeatherSituationVector_bunch(time0,time0, 1);

	}
	
	public float[] read_vector(File file) {
		String zz="\\n";
		Pattern pat=Pattern.compile(zz);
		float[] vector = new float[40];
		FileInputStream in;
		try {
			in = new FileInputStream(file);
			byte[] readBytes = new byte[in.available()];
			in.read(readBytes);
			in.close();
			String str = new String(readBytes);
			String[] strs = pat.split(str.trim());
		
			int n=0;
			for(int j=1;j<strs.length;j++) {
				String[] strss = strs[j].split("\\s+");				
				for(int k=0;k<5;k++) {
					if(k<3|| (j>2 &&j<9)) {
						n++;
						float para = Float.parseFloat(strss[k+1]);
						vector[n] = para;
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vector;
	}
	
	public void creatClassifiationByKmeans (int classNumber) {
		//读取历史的天气形势矢量，根据knn方法进行分类，并将分类结果输出到模型文件中
		//dir_vector 函数将从dir_vector目录中遍历所有文件，读取其中的vector数据
		//classNumber knn方法聚类分析时预设的分类类别数
		//file_classificationModel 分类将分类结果输出到文件中。
		File file = new File(dir_vectors);		//获取其file对象
		File[] fs = file.listFiles();
		int fs_num = fs.length;
		ArrayList<float[]> vector_list = new ArrayList<float[]>();
		for(int i=0;i<fs_num;i++) {
			float[] vector = read_vector(fs[i]);
			vector[0] = i;	
			vector_list.add(vector);	
		}
		
		//进行数据的标准化
		//计算平均值
		float[] meanvalue = new float[40];
		int vn = vector_list.size();
		for(int i =0;i<vn;i++) {
			for(int j=1;j<40;j++) {
				meanvalue[j] += vector_list.get(i)[j]/vn;
			}
		}
		
		//计算方差
		float[] var = new float[40];
		for(int i =0;i<vn;i++) {
			for(int j=1;j<40;j++) {
				var[j] +=(float) Math.pow(vector_list.get(i)[j] - meanvalue[j],2);
			}
		}
		for(int j=1;j<40;j++) {
			var[j] /= vn;
			var[j] = (float) Math.sqrt(var[j]);
			var[j] += 1e-30;
		}
		
		//将矢量标准化
		ArrayList<float[]> vector_list1 = new ArrayList<float[]> ();
		for(int i =0;i<vn;i++) {
			float[] vector1 = new float[40];
			vector1[0] = i;
			for(int j=1;j<40;j++) {
				vector1[j] = (vector_list.get(i)[j] -meanvalue[j])/(var[j]);
			}
			vector_list1.add(vector1);
		}
		KMeansCluster kmc = new KMeansCluster(classNumber,100,vector_list1);
		kmc.runKmeans();
		
		//将聚类结果输出
		//输出数据集的平均和方差
		 String output_path = dir_classificationModel +"\\mean_var.txt";
		 File file1 = new File(output_path);
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(file1),"GBK");			
			BufferedWriter br=new BufferedWriter(fos);
			for(int j=1;j<40;j++) {
				br.write(meanvalue[j] + " " + var[j] + "\n");
			}
			br.flush();
			fos.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//输出模型聚类的中心点
		output_path = dir_classificationModel +"\\centers.txt";
		 File file2 = new File(output_path);
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(file2),"GBK");			
			BufferedWriter br=new BufferedWriter(fos);
			for(int i=0;i<kmc.k;i++) {
				float[] center = kmc.centers.get(i);
				for(int j=0;j<center.length;j++) {
					br.write(center[j] + " ");
				}
				br.write("\n");
			}
			br.flush();
			fos.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//输出数据样本对应的路径目录
		output_path = dir_classificationModel +"\\files.txt";
		 File file3 = new File(output_path);
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(file3),"GBK");			
			BufferedWriter br=new BufferedWriter(fos);
			for(int i=0;i<fs.length;i++) {
				br.write(fs[i] + "\n");
			}
			br.flush();
			fos.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//输出每个样本的所属的分类、编号、和标准化矢量
		
		output_path = dir_classificationModel +"\\vectors.txt";
		 File file4 = new File(output_path);
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(file4),"GBK");			
			BufferedWriter br=new BufferedWriter(fos);
			for(int i=0;i<kmc.points.size();i++) {
				float[] point = kmc.points.get(i);
				for(int j=0;j<point.length;j++) {
					br.write(point[j] + " ");
				}
				br.write("\n");
			}
			br.flush();
			fos.close();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	

	
	public ArrayList<String> getSimilarWeatherDates(Calendar time0,int nearNum) {
		//dir_veoctor 天气形势矢量的存储路径
		//time0 待识别的日期
		//分类模型文件
		//读取数据集的平均和方差
		float[] meanvalue = new float[40];
		float[] var = new float[40];
		
		 String output_path = dir_classificationModel +"\\mean_var.txt";
		 File file1 = new File(output_path);
		 String zz="\\n";
		 Pattern pat=Pattern.compile(zz);
		 
		 FileInputStream in;
		try {
			in = new FileInputStream(file1);
			byte[] readBytes = new byte[in.available()];
			in.read(readBytes);
			in.close();
			String str = new String(readBytes);
			String[] strs = pat.split(str.trim());
			for(int j = 0;j< strs.length;j++) {
				String[] strss = strs[j].split("\\s+");
				meanvalue[j+1] = Float.parseFloat(strss[0]);
				var[j+1] = Float.parseFloat(strss[1]);
			}
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//读取分类中心
		ArrayList<float[]> centers = new ArrayList<float[]>();
		output_path = dir_classificationModel +"\\centers.txt";
		 File file2 = new File(output_path);
		try {
			in = new FileInputStream(file2);
			byte[] readBytes = new byte[in.available()];
			in.read(readBytes);
			in.close();
			String str = new String(readBytes);
			String[] strs = pat.split(str.trim());
			for(int i = 0;i< strs.length;i++) {
				String[] strss = strs[i].split("\\s+");
				float[] center = new float[strss.length];
				for(int j = 0;j< strss.length;j++) {
					center[j] = Float.parseFloat(strss[j]);
				}
				centers.add(center);
			}
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//读取标准化矢量
		ArrayList<float[]> points = new ArrayList<float[]>();
		output_path = dir_classificationModel +"\\vectors.txt";
		File file3 = new File(output_path);
		try {
			in = new FileInputStream(file3);
			byte[] readBytes = new byte[in.available()];
			in.read(readBytes);
			in.close();
			String str = new String(readBytes);
			String[] strs = pat.split(str.trim());
			for(int i = 0;i< strs.length;i++) {
				String[] strss = strs[i].split("\\s+");
				float[] point = new float[strss.length];
				for(int j = 0;j< strss.length;j++) {
					point[j] = Float.parseFloat(strss[j]);
				}
				points.add(point);
			}
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//读取filename_list
		output_path = dir_classificationModel +"\\files.txt";
		File file4 = new File(output_path);
		String[] filenames = null;
		try {
			in = new FileInputStream(file4);
			byte[] readBytes = new byte[in.available()];
			in.read(readBytes);
			in.close();
			String str = new String(readBytes);
			filenames = pat.split(str.trim());
			
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		 
		this.creatWeatherSituationVector(time0); // 创建某一天的特征矢量
		
		//将特征矢量读取到数组中
		String fileName =MyMath.getFileNameFromCalendar(time0);
		output_path = dir_vectors +"\\" +fileName.substring(2,10)+".txt";
		File file = new File(output_path);
		float[] vector0 = read_vector(file);
		
		//对特征矢量进行标准化
		float[] vector1 = new float[vector0.length];
		for(int j= 1;j<vector0.length;j++) {
			vector1[j] = (vector0[j] - meanvalue[j])/var[j];
		}
		
		//判断特征矢量和各聚类中心点的距离,判断当日形势属于哪一种分类
        double minDistance = 999999;
        float clusterID = -1;
        for (float[] center : centers)
        {
        	float dis2 = 0;
        	for(int j= 1;j<vector1.length;j++) {
        		dis2 += (vector1[j] - center[j+1]) * (vector1[j] - center[j+1]);
        	}
        	
            if (dis2< minDistance)
            {
                minDistance = dis2;
                clusterID = center[0];
            }
        }	    
		
		// 判断当日形势离 第clusterID类的样本集合中各日特征量的距离。
        ArrayList<float[]> id_dis = new ArrayList<>();
        for(float[]point :points) {
        	if(point[0] == clusterID) {
        		float dis2 = 0;
            	for(int j= 1;j<vector1.length;j++) {
            		dis2 += (vector1[j] - point[j+1]) * (vector1[j] - point[j+1]);
            	}
            	float[] id_dis1 = new float[2];
            	id_dis1[0] = point[1];
            	id_dis1[1] = dis2; 
            	id_dis.add(id_dis1);
        	}
        }

        float[][] id_dis2 = new float[id_dis.size()][];
        for(int i=0;i<id_dis.size();i++) {
        	id_dis2[i] = id_dis.get(i);
        }
        
        sort(id_dis2,new int[] {1});
        
        ArrayList<String> similar_filenames = new ArrayList<String>();
        for(int i=0;i<nearNum;i++) {
        	if(i<id_dis2.length) {
        		int id = (int)id_dis2[i][0];
        		similar_filenames.add(filenames[id]);
        	}
        }
        
		
		
		return similar_filenames;
	}
	
	
	
	public static void sort(float[][] ob, final int[] order) {    
        Arrays.sort(ob, new Comparator<Object>() {    
            public int compare(Object o1, Object o2) {    
                float[] one = (float[]) o1;    
                float[] two = (float[]) o2;    
                for (int i = 0; i < order.length; i++) {    
                    int k = order[i];    
                    if (one[k] > two[k]) {    
                        return 1;    
                    } else if (one[k] < two[k]) {    
                        return -1;    
                    } else {    
                        continue;  //如果按一条件比较结果相等，就使用第二个条件进行比较。  
                    }    
                }    
                return 0;    
            }    
        });   
    }  
	
}
