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
import recutil.SVortex;
import recutil.SystemIdentification;
import recutil.TyphoonReport;
import recutil.VectorData;
import recutil.VectorMathod;
import recutil.WeatherSituationType;
import recutil.WeatherSystems;
public class MainTest {
	
	static String test_data_root= "D:/develop/java/";
	
	public static void main(String[] args) throws Exception
	{
		
		
		//GridData grid = new GridData("G:\\data\\dt24grid\\output\\15010620.000");
		//grid = grid.mutiply(-1).add(-4);
		//GridData id = SystemIdentification.getCuttedRegion(grid); 
		//id.writeToFile("E:/java/coldair/newId.txt");
		
		
		//閹碉拷閺堝琚崹瀣兇缂佺喕鐦戦崚顐ゆ畱濞村鐦粙瀣碍閸忋儱褰�
		//southAsiaHighTest();
		//hLCentreTest();
		//vortexCentreTest();
		//troughTest();
		//ridgeTest();
		//shearLineTest();
		//jetLineTest();
		//SubtropicalHighTest();

		
		weathersituationtype();
		
		
	}
	
	private static void weathersituationtype() {
		Calendar start = Calendar.getInstance();
		start.set(2010, 5, 18,20,0);
		Calendar end =Calendar.getInstance();
		end.set(2010, 5, 20,20,0);
		Calendar time= (Calendar) start.clone();
		while(time.before(end)){
			String fileName =MyMath.getFileNameFromCalendar(time);
			time.add(Calendar.HOUR, 6);
			String h1000_path = test_data_root +  "201905-weahter_identification/gfs0/"+fileName.substring(0,4)+"/hgt/1000/"+fileName.substring(2,10)+".000";
			GridData h1000 = new GridData(h1000_path);
			if(h1000.gridInfo == null) {
				continue;
			}
			String h500_path = test_data_root +  "201905-weahter_identification/gfs0/"+fileName.substring(0,4)+"/hgt/500/"+fileName.substring(2,10)+".000";
			GridData h500 = new GridData(h500_path);
			if(h500.gridInfo == null) {
				continue;
			}
			for(int i=0;i<h500.gridInfo.nlon;i++) {
				for(int j=0;j<h500.gridInfo.nlat;j++) {
					h500.dat[i][j] *= 10;
				}
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
			
			ArrayList<TyphoonReport> tythoons = new ArrayList<TyphoonReport>();
			
			WeatherSituationType wst = new WeatherSituationType(h1000,h500,w850,w700,w500,tythoons);
			System.out.println(fileName);
			
		}
		
		
	}
	
	// 濞村鐦崡妞剧肮妤傛ê甯囩拠鍡楀焼閻ㄥ嫮鈻兼惔锟�
	private static void southAsiaHighTest(){
		int[] level = new int[]{100};  //閸楁ぞ绨规妯哄竾娑撴槒顩﹂崗铏暈100hpa
		Calendar start = Calendar.getInstance();
		start.set(2015, 6, 20,8,0);
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
		start.set(2018, 4, 16,8,0);
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
				WeatherSystems hlCentres = SHighLowPressure.getHLCentres(grid,level[i],2.0f);
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
