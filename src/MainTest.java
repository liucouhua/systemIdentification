import java.io.File;  
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import recutil.GridData;
import recutil.GridInfo;
import recutil.MyMath;
import recutil.SHighLowPressure;
import recutil.SJet;
import recutil.SRidge;
import recutil.SShear;
import recutil.SSouthAsiaHigh;
import recutil.SSubtropicalHigh;
import recutil.STrough;
import recutil.STyphoon;
import recutil.SVortex;
import recutil.SimilarWeatherSituation;
import recutil.SystemIdentification;
import recutil.VectorData;
import recutil.VectorMathod;
import recutil.WeatherSituationType;
import recutil.WeatherSystems;
import recutil.STyphoon;
public class MainTest {
	
	static String test_data_root= "D:/develop/java/";
	
	public static void main(String[] args) throws Exception
	
	{
		//creat_typhoon_report_test();
		//weathersituationtype();
		//test09();
		class_test();
	}
	
	
	private static void class_test() {
		
		//
		
		String dir_h1000 = "D:\\develop\\java\\201905-weahter_identification\\gfs0\\2010\\hgt\\1000";
		String dir_h500 = "D:\\develop\\java\\201905-weahter_identification\\gfs0\\2010\\hgt\\500";
		String dir_w850 = "D:\\develop\\java\\201905-weahter_identification\\gfs0\\2010\\wind\\850";
		String dir_w700 = "D:\\develop\\java\\201905-weahter_identification\\gfs0\\2010\\wind\\700";
		String dir_typhoon = "D:\\develop\\java\\201905-weahter_identification\\output\\typhoon_trace";
		String dir_vectors = "D:\\develop\\java\\201905-weahter_identification\\output\\weather_para_vector";
		String dir_classification = "D:\\develop\\java\\201905-weahter_identification\\output\\classification";
		float centx = 114;
		float centy = 30;
		//相似天气型识别的算法功能都放在 SimilarWeatherSituation类里面。
		//使用时分为以下几个步骤：
		//step1： 将存储了历史数据的各要素的数据路径，传输给SimilarWeathSituation的初始化函数，sws的成员属性中会保存这些数据路径
		//dir_h1000, dir_w850, dir_w700, dir_h500, dir_typhoon等参数都是输入数据的路径，文件名命名格式为 YYMMDDHH.000
		//如果数据路径和关注中心位置不变时，step1无需重复运行。
		SimilarWeatherSituation sws = new SimilarWeatherSituation(dir_h1000, dir_w850, dir_w700, dir_h500, dir_typhoon,dir_vectors,dir_classification,centx,centy);
		
		
		//step2：设置历史数据的起止时间，批量的识别历史数据中各类天气系统，同一类天气系统中会保留一个离关注区域最近的一个系统, 计算它的属性，将属性计算结果保存在dir_vecotrs目录里面
		//dir_vectors天气系统属性计算结果保存目录，文件名命名格式为YYMMDDHH.txt。centx，centy为关注区域的中心点位置，例如关注武汉附件区域时，centx = 114;centy = 30;
		//当有新的历史样本增加时，start和end只需要覆盖新资料的日期就行，不需要把所有日期的都重新运行一遍
		Calendar start = Calendar.getInstance();
		start.set(2010,3,20,8,0);	
		Calendar end = Calendar.getInstance();
		end.set(2010,11,20,20,0);
		int dh = 6;
		//sws.creatWeatherSituationVector_bunch(start, end, dh);
		
		
		//step3:从dir_vecotrs目录里读取所有的时刻的属性文件，设文件有N个，读取后的数据数据是N * 39的二维数组。用这些数据训练kmean分类模型，将模型有关的各类结果存储在dir_classification中
		//当有新数据要加入，需要更新分类模型时，需要重复运行step2和step3
		sws.creatClassifiationByKmeans(3);
		
		//step4:设定当前日期，从历史数据中找到和当前日期最相似的nearNum个相似各类，返回相似个例对应的属性的文件名。 
		//如果重新训练模型，只是将已有模型用于对新日期相似结果的搜素，则无需重新允许step1-step3
		
		Calendar time1 = (Calendar) start.clone();
		time1.add(Calendar.HOUR_OF_DAY, 6);
		ArrayList<String>similar_filenames =  sws.getSimilarWeatherDates(time1,5); //nearNum = 5
		
		
		
		//输出识别结果
		System.out.println("最相似的日期对应的文件名为：\n");
		for(int i=0;i<similar_filenames.size();i++) {
			System.out.println(similar_filenames.get(i));
		}
		
		
	}
	
	private static void test09() {
		Calendar endtime = Calendar.getInstance();
		endtime.set(2019,8,8,20,0);
		String output_dir = "E:\\";
		for(int i=0;i < 180; i+=12) {
			Calendar time0 = (Calendar)endtime.clone();
			time0.add(Calendar.HOUR_OF_DAY, -i);
			String fileName = MyMath.getFileNameFromCalendar(time0);
			String path = "E:\\wind\\" +fileName.substring(2,10) +"." + String.format("%03d", i);
			VectorData wind850 = new VectorData(path);
			WeatherSystems shear_850 = SShear.getShear(wind850, 850, 1.0f);
			shear_850.writeIds(output_dir +"shear_850\\ids"+fileName.substring(2,10) +"." + String.format("%03d", i), fileName);
			shear_850.writeFeatures(output_dir +"shear_850\\feature"+fileName.substring(2,10) +"." + String.format("%03d", i), fileName);
			shear_850.writeValues(output_dir + "shear_850\\value"+fileName.substring(2,10) +"." + String.format("%03d", i), fileName);
			shear_850.writeAbstract(output_dir+ "shear_850\\abstract"+fileName.substring(2,10) +"." + String.format("%03d", i), fileName);
		
			WeatherSystems jet_850 = SJet.getJet(wind850, 850, 1.0f);
			jet_850.writeIds(output_dir +"jet_850\\ids"+fileName.substring(2,10) +"." + String.format("%03d", i),fileName);
			jet_850.writeFeatures(output_dir +"jet_850\\feature"+fileName.substring(2,10) +"." + String.format("%03d", i), fileName);
			jet_850.writeValues(output_dir + "jet_850\\value"+fileName.substring(2,10) +"." + String.format("%03d", i), fileName);
			jet_850.writeAbstract(output_dir+ "jet_850\\abstract"+fileName.substring(2,10) +"." + String.format("%03d", i), fileName);
			
		}
		
	}
	
	
	
	
	private static void creat_typhoon_report_test(){
		String wind850_dir = "D:\\develop\\java\\201905-weahter_identification\\gfs0\\2010\\wind\\850";
		String report_dir = "D:\\develop\\java\\201905-weahter_identification\\output\\typhoon_report";
		Calendar start = Calendar.getInstance();
		start.set(2010,6, 13,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2016, 6, 21,3,0);
		Calendar dati= (Calendar) start.clone();
		while(dati.before(end)) {
			dati.add(Calendar.HOUR, 6);
			STyphoon.creat_typhoon_report(wind850_dir,report_dir,dati,6,6, 48);
		}
		
	}
	
	
	
	
	private static void weathersituationtype() {
		String output_dir = "D:\\develop\\java\\201905-weahter_identification\\output\\";
		Calendar start = Calendar.getInstance();
		start.set(2010,5, 18,20,0);
		Calendar end =Calendar.getInstance();
		end.set(2012, 9, 21,3,0);
		Calendar time= (Calendar) start.clone();
		String root_dir = "D:\\develop\\java\\201905-weahter_identification\\output\\";
		String root_typhoon = "D:\\develop\\java\\201905-weahter_identification\\output\\typhoon_trace\\babj";
		while(time.before(end)){
			
			time.add(Calendar.HOUR, 6);
			String fileName =MyMath.getFileNameFromCalendar(time);
			
			String h500_path = test_data_root +  "201905-weahter_identification/gfs0/"+fileName.substring(0,4)+"/hgt/500/"+fileName.substring(2,10)+".000";
			//String h500_path = test_data_root +  "201905-weahter_identification/GH/500/"+fileName.substring(0,10)+".000";
			GridData h500 = new GridData(h500_path);
			if(h500.gridInfo == null) {
				continue;
			}
			for(int i=0;i<h500.gridInfo.nlon;i++) {
				for(int j=0;j<h500.gridInfo.nlat;j++) {
					h500.dat[i][j] *= 10;
				}
			}
	
		
		
			ArrayList<float[]> typhoon_reports = STyphoon.read_typhoon_position(root_typhoon,time);

			//System.out.println(fileName);
			//if(typhoon_reports.size() == 0)continue;
			//System.out.println(typhoon_reports.size());
			
			String h1000_path = test_data_root +  "201905-weahter_identification/gfs0/"+fileName.substring(0,4)+"/hgt/1000/"+fileName.substring(2,10)+".000";
			GridData h1000 = new GridData(h1000_path);
			if(h1000.gridInfo == null) {
				continue;
			}
			String h850_path = test_data_root +  "201905-weahter_identification/gfs0/"+fileName.substring(0,4)+"/hgt/850/"+fileName.substring(2,10)+".000";
			GridData h850 = new GridData(h850_path);
			if(h850.gridInfo == null) {
				continue;
			}
			
			String h700_path = test_data_root +  "201905-weahter_identification/gfs0/"+fileName.substring(0,4)+"/hgt/700/"+fileName.substring(2,10)+".000";
			GridData h700 = new GridData(h700_path);
			if(h850.gridInfo == null) {
				continue;
			}
			
			
			String w850_path = test_data_root +  "201905-weahter_identification/gfs0/"+fileName.substring(0,4)+"/wind/850/"+fileName.substring(2,10)+".000";
			VectorData w850 = new VectorData(w850_path);
			if(w850.gridInfo == null) {
				continue;
			}
			
			String w700_path = test_data_root +  "201905-weahter_identification/gfs0/"+fileName.substring(0,4)+"/wind/700/"+fileName.substring(2,10)+".000";
			VectorData w700 = new VectorData(w700_path);
			if(w700.gridInfo == null) {
				continue;
			}
			String w500_path = test_data_root +  "201905-weahter_identification/gfs0/"+fileName.substring(0,4)+"/wind/500/"+fileName.substring(2,10)+".000";
			VectorData w500 = new VectorData(w500_path);
			if(w500.gridInfo == null) {
				continue;
			}
			
			WeatherSituationType wst = new WeatherSituationType(h1000,h850,h700,h500,w850,w700,w500,typhoon_reports,fileName);
			wst.write_to_file(root_dir,time);
			System.out.println(fileName);
			
		}
		
		
	}
	
	// 濞村鐦崡妞剧肮妤傛ê甯囩拠鍡楀焼閻ㄥ嫮鈻兼惔锟�
	private static void southAsiaHighTest(){
		int[] level = new int[]{100};  //閸楁ぞ绨规妯哄竾娑撴槒顩﹂崗铏暈100hpa
		Calendar start = Calendar.getInstance();
		start.set(2015, 6, 15,20,0);
		Calendar end =Calendar.getInstance();
		end.set(2015, 6, 20,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				String dir = "M:/newecmwf_grib/newecmwf_grib_"+fileName.substring(0,6)+"/height/"+level[i]+"/"+fileName.substring(2,10)+".000";
				GridData grid=new GridData(dir);
				grid = grid.mutiply(10.0f);
				grid.smooth(50);
				grid.writeToFile("H:/task/link/xiangji/201905-weahter_identification/output/height/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				WeatherSystems ridges = SSouthAsiaHigh.getSouthAsiaHigh(grid,level[i],10.0f);
				ridges.writeIds("H:/task/link/xiangji/201905-weahter_identification/output/ids/southasiahigh/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				ridges.writeFeatures("H:/task/link/xiangji/201905-weahter_identification/output/features/southasiahigh/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				System.out.println(fileName);
			}
		}
	}
	
	// 濞村鐦崜顖滃劰鐢箓鐝崢瀣槕閸掝偆娈戠粙瀣碍
	private static void SubtropicalHighTest() {
		// TODO Auto-generated method stub
		int[] level = new int[]{500};  // 閸擃垳鍎圭敮锕傜彯閸樺瀵岀憰浣稿彠濞夛拷500hpa
		Calendar start = Calendar.getInstance();
		start.set(2017, 9, 20,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2017, 9, 20,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				GridData grid=new GridData("Z:/data/newecmwf_grib/height/"+level[i]+"/"+fileName.substring(2,10)+".000");
				grid = grid.mutiply(10.0f);
				grid.smooth(50);
				grid.writeToFile("H:/task/link/xiangji/201905-weahter_identification/output/height/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				WeatherSystems ridges = SSubtropicalHigh.getSubtropicalHigh(grid,level[i],10.0f);
				ridges.writeIds("H:/task/link/xiangji/201905-weahter_identification/output/ids/subtropicalhigh/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				ridges.writeFeatures("H:/task/link/xiangji/201905-weahter_identification/output/features/subtropicalhigh/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				System.out.println(fileName);
			}
		}
	}
	
	// 濞村鐦稉顓濈秵鐏炲倿鐝担搴″竾閸栭缚鐦戦崚顐ゆ畱缁嬪绨�
	private static void hLCentreTest() {
		int[] level = new int[]{500};  // 娑擄拷閼割剛娈戞妯圭秵閸樺灏稉鏄忣洣閸忚櫕鏁炴稉顓濈秵鐏烇拷
		Calendar start = Calendar.getInstance();
		start.set(2018, 3, 16,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2018, 4, 16,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				GridData grid=new GridData("H:\\task\\link\\xiangji\\201706-systemIdentification\\2018062320.003");
				//grid = grid.mutiply(10);
				grid.smooth(200);
			//	grid.writeToFile("H:\\task\\link\\xiangji\\201706-systemIdentification\\output\\smooth.003");
				WeatherSystems hlCentres = SHighLowPressure.getHLCentres(grid,level[i],2.0f,4.0f);
			//	WeatherSystems hlCentres = SHighLowPressure.getHLCentres_quickly(grid,level[i],5.0f);
				hlCentres.writeIds("H:\\task\\link\\xiangji\\201706-systemIdentification\\output\\ids.003",fileName);
				hlCentres.writeFeatures("H:\\task\\link\\xiangji\\201706-systemIdentification\\output\\feature.003",fileName);
				System.out.println(fileName);
			}
		}
	}

	//濞村鐦☉鈩冩鐠囧棗鍩嗛惃鍕柤鎼达拷  
	private static void vortexCentreTest() {
		// TODO Auto-generated method stub
		int[] level = new int[]{850};   // 濞戔剝妫嗛崗铏暈娑擃厼鐪伴崪灞肩秵鐏烇拷
		Calendar start = Calendar.getInstance();
		start.set(2010, 1, 3,2,0);
		
		Calendar end =Calendar.getInstance();
		end.set(2012, 12, 5,2,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 6);
				VectorData wind=new VectorData(test_data_root+"201905-weahter_identification/gfs0/"+fileName.substring(0,4)+"/wind/"+level[i]+"/"+fileName.substring(2,10)+".000");
				if (wind.gridInfo!= null) {
					
				
					int nlon = wind.gridInfo.nlon * 2 - 1;
					int nlat = wind.gridInfo.nlat * 2 - 1;
					GridInfo grid025 = new GridInfo(nlon,nlat,wind.gridInfo.startlon,wind.gridInfo.startlat,wind.gridInfo.dlon/2,wind.gridInfo.dlat/2);
					VectorData wind025 = new VectorData(grid025);
					wind025.u.linearIntepolatedFrom(wind.u);
					wind025.v.linearIntepolatedFrom(wind.v);
					//wind = wind025;
					wind.u.smooth(1); wind.v.smooth(1);
					
					GridData cor = VectorMathod.getCurvatureVor(wind);
					wind.writeToFile(test_data_root+"201905-weahter_identification/output/wind.txt", "2019060108");
					cor.smooth(5);
					cor.writeToFile(test_data_root+"201905-weahter_identification/output/cor.txt", "2019060108");
					
					wind.writeToFile(test_data_root+"201905-weahter_identification/output/wind/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
					WeatherSystems vorCentres = SVortex.getVortexCentres(wind,level[i],1.0f);
					vorCentres.writeIds(test_data_root+"201905-weahter_identification/output/ids/vortexCentre/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
					vorCentres.writeFeatures(test_data_root+"201905-weahter_identification/output/features/vortexCentre/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
					vorCentres.writeValues(test_data_root+"201905-weahter_identification/output/values/vortexCentre/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
					System.out.println(fileName);
				}
			}
		}
	}

	//濞村鐦稉顓炵湴鐟楀潡顥撻悳顖涚ウ娑擃叀鍓ㄧ痪鑳槕閸掝偆娈戠粙瀣碍
	private static void ridgeTest() { 
		// TODO Auto-generated method stub
		int[] level = new int[]{500};                     // 閼村﹦鍤庢稉鏄忣洣閸忚櫕鏁�500hpa
		Calendar start = Calendar.getInstance();
		start.set(2017, 9, 20,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2017, 9, 20,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				GridData grid=new GridData("Z:/data/newecmwf_grib/height/"+level[i]+"/"+fileName.substring(2,10)+".000");
				grid = grid.mutiply(10.0f);
				grid.smooth(20);
				grid.writeToFile("H:/task/link/xiangji/201905-weahter_identification/output/height/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				WeatherSystems ridges = SRidge.getRidge(grid,level[i],2.0f);
				ridges.writeIds("H:/task/link/xiangji/201905-weahter_identification/output/ids/ridge/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				ridges.writeFeatures("H:/task/link/xiangji/201905-weahter_identification/output/features/ridge/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				System.out.println(fileName);
			}
		}
	}
	
	//濞村鐦稉顓炵湴濡茬晫鍤庣拠鍡楀焼閻ㄥ嫮鈻兼惔锟�
	private static void troughTest() {
		// TODO Auto-generated method stub
		int[] level = new int[]{500};                  // 濡茬晫鍤庢稊鐔跺瘜鐟曚礁鍙у▔锟�500hpa
		Calendar start = Calendar.getInstance();
		start.set(2017, 9, 20,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2017, 9, 20,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				GridData grid=new GridData("Z:/data/newecmwf_grib/height/"+level[i]+"/"+fileName.substring(2,10)+".000");
				grid = grid.mutiply(10);
				grid.smooth(20);
				grid.writeToFile("H:/task/link/xiangji/201905-weahter_identification/output/height/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				WeatherSystems troughs = STrough.getTrough(grid,level[i],2.0f);
				troughs.writeIds("H:/task/link/xiangji/201905-weahter_identification/output/ids/trough/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				troughs.writeFeatures("H:/task/link/xiangji/201905-weahter_identification/output/features/trough/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				System.out.println(fileName);
			}
		}
	}
	
	//濞村鐦幀銉︾ウ鐠囧棗鍩嗛惃鍕柤鎼达拷
	private static void jetLineTest() {
		// TODO Auto-generated method stub
		int[] level = new int[]{850,700,500,200,100};       // 閹儲绁︽潪瀵稿殠閸氬嫬鐪伴柈鑺ユ箒閸忚櫕鏁為敍灞肩瑝閸氬苯鐪板▎鈩冩箒娑撳秴鎮撻惃鍕棑闁喖妲囬崐鍏肩垼閸戯拷
		Calendar start = Calendar.getInstance();
		start.set(2017, 9, 20,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2017, 9, 20,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				VectorData wind=new VectorData("Z:/data/newecmwf_grib/stream/"+level[i]+"/"+fileName.substring(2,10)+".000");
				wind.u.smooth(5); wind.v.smooth(5);
				wind.writeToFile("H:/task/link/xiangji/201905-weahter_identification/output/wind/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				WeatherSystems jetLines = SJet.getJet(wind,level[i],2.0f);
				jetLines.writeIds("H:/task/link/xiangji/201905-weahter_identification/output/ids/jetLine/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				jetLines.writeFeatures("H:/task/link/xiangji/201905-weahter_identification/output/features/jetLine/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				System.out.println(fileName);
			//	break;
			}
		}
	}
	
	
	//濞村鐦稉顓濈秵鐏炲倸鍨忛崣妯煎殠鐠囧棗鍩嗛惃鍕柤鎼达拷
	private static void shearLineTest() throws Exception {
		// TODO Auto-generated method stub
		int[] level = new int[]{850,700};                          // 閸掑洤褰夌痪澶稿瘜鐟曚礁鍙у▔锟�850hpa 閸滐拷700hpa
		Calendar start = Calendar.getInstance();
		start.set(2017, 9,20,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2017, 9, 20,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				VectorData wind=new VectorData("Z:/data/newecmwf_grib/stream/"+level[i]+"/"+fileName.substring(2,10)+".000");
			    wind=new VectorData("H:/task/link/xiangji/201905-weahter_identification/output/wind/"+level[i]+"/"+fileName.substring(2,10)+".000");
				wind.u.smooth(5); wind.v.smooth(5);
			//	wind.writeToFile("H:/task/link/xiangji/201905-weahter_identification/output/wind/"+level[i]+"/"+fileName.substring(2,10)+".000");
				WeatherSystems shearLines = SShear.getShear(wind,level[i],2.0f);
				shearLines.writeIds("H:/task/link/xiangji/201905-weahter_identification/output/ids/shearLine/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				shearLines.writeFeatures("H:/task/link/xiangji/201905-weahter_identification/output/features/shearLine/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				shearLines.writeValues("H:/task/link/xiangji/201905-weahter_identification/output/values/shearLine/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				System.out.println(fileName);
			//	break;
			}
		}
	}

	
}
