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
import recutil.VectorData;
import recutil.VectorMathod;
import recutil.WeatherSystems;
public class MainTest {
	public static void main(String[] args) throws Exception
	{
		
		//GridData grid = new GridData("G:\\data\\dt24grid\\output\\15010620.000");
		//grid = grid.mutiply(-1).add(-4);
		//GridData id = SystemIdentification.getCuttedRegion(grid); 
		//id.writeToFile("E:/java/coldair/newId.txt");
		
		
		//鎵�鏈夌被鍨嬬郴缁熻瘑鍒殑娴嬭瘯绋嬪簭鍏ュ彛
		//southAsiaHighTest();
		//hLCentreTest();
		vortexCentreTest();
		//troughTest();
		//ridgeTest();
		//shearLineTest();
		//jetLineTest();
		//SubtropicalHighTest();
		
	}
	
	// 娴嬭瘯鍗椾簹楂樺帇璇嗗埆鐨勭▼搴�
	private static void southAsiaHighTest(){
		int[] level = new int[]{100};  //鍗椾簹楂樺帇涓昏鍏虫敞100hpa
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
	
	// 娴嬭瘯鍓儹甯﹂珮鍘嬭瘑鍒殑绋嬪簭
	private static void SubtropicalHighTest() {
		// TODO Auto-generated method stub
		int[] level = new int[]{500};  // 鍓儹甯﹂珮鍘嬩富瑕佸叧娉�500hpa
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
	
	// 娴嬭瘯涓綆灞傞珮浣庡帇鍖鸿瘑鍒殑绋嬪簭
	private static void hLCentreTest() {
		int[] level = new int[]{500};  // 涓�鑸殑楂樹綆鍘嬪尯涓昏鍏虫敞涓綆灞�
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

	//娴嬭瘯娑℃棆璇嗗埆鐨勭▼搴�  
	private static void vortexCentreTest() {
		// TODO Auto-generated method stub
		int[] level = new int[]{850};   // 娑℃棆鍏虫敞涓眰鍜屼綆灞�
		Calendar start = Calendar.getInstance();
		start.set(2010, 6, 3,2,0);
		
		Calendar end =Calendar.getInstance();
		end.set(2011, 6, 4,8,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 6);
				VectorData wind=new VectorData("D:/develop/java/201905-weahter_identification/gfs0/"+fileName.substring(0,4)+"/wind/"+level[i]+"/"+fileName.substring(2,10)+".000");
				wind.u.smooth(1); wind.v.smooth(1);
				int nlon = wind.gridInfo.nlon * 2 - 1;
				int nlat = wind.gridInfo.nlat * 2 - 1;
				GridInfo grid025 = new GridInfo(nlon,nlat,wind.gridInfo.startlon,wind.gridInfo.startlat,wind.gridInfo.dlon/2,wind.gridInfo.dlat/2);
				VectorData wind025 = new VectorData(grid025);
				wind025.u.linearIntepolatedFrom(wind.u);
				wind025.v.linearIntepolatedFrom(wind.v);
				wind = wind025;
				GridData cor = VectorMathod.getCurvatureVor(wind);
				wind.writeToFile("D:/develop/java/201905-weahter_identification/output/wind.txt", "2019060108");
				cor.smooth(5);
				cor.writeToFile("D:/develop/java/201905-weahter_identification/output/cor.txt", "2019060108");
				
				wind.writeToFile("D:/develop/java/201905-weahter_identification/output/wind/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				WeatherSystems vorCentres = SVortex.getVortexCentres(wind,level[i],2.0f);
				vorCentres.writeIds("D:/develop/java/201905-weahter_identification/output/ids/vortexCentre/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				vorCentres.writeFeatures("D:/develop/java/201905-weahter_identification/output/features/vortexCentre/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				vorCentres.writeValues("D:/develop/java/201905-weahter_identification/output/values/vortexCentre/"+level[i]+"/"+fileName.substring(2,10)+".000",fileName);
				System.out.println(fileName);
			}
		}
	}

	//娴嬭瘯涓眰瑗块鐜祦涓剨绾胯瘑鍒殑绋嬪簭
	private static void ridgeTest() { 
		// TODO Auto-generated method stub
		int[] level = new int[]{500};                     // 鑴婄嚎涓昏鍏虫敞500hpa
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
	
	//娴嬭瘯涓眰妲界嚎璇嗗埆鐨勭▼搴�
	private static void troughTest() {
		// TODO Auto-generated method stub
		int[] level = new int[]{500};                  // 妲界嚎涔熶富瑕佸叧娉�500hpa
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
	
	//娴嬭瘯鎬ユ祦璇嗗埆鐨勭▼搴�
	private static void jetLineTest() {
		// TODO Auto-generated method stub
		int[] level = new int[]{850,700,500,200,100};       // 鎬ユ祦杞寸嚎鍚勫眰閮芥湁鍏虫敞锛屼笉鍚屽眰娆℃湁涓嶅悓鐨勯閫熼槇鍊兼爣鍑�
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
	
	
	//娴嬭瘯涓綆灞傚垏鍙樼嚎璇嗗埆鐨勭▼搴�
	private static void shearLineTest() throws Exception {
		// TODO Auto-generated method stub
		int[] level = new int[]{850,700};                          // 鍒囧彉绾夸富瑕佸叧娉�850hpa 鍜�700hpa
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
