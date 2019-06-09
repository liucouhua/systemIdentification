package recutil;

import java.util.Map;

//import com.google.common.collect.ImmutableMap;

public class Constants {
	
	public static final String OUTPUT_DATA_PATH = "D:/mlog/data/";//输出的文件位置E:/mlog/data/
	
	public static final String OUTPUT_DATA_PATH_RANDOM = "K:/random/";
	
	//public static final Map<String,String> vars = ImmutableMap.of("wind","WIND","height","HGT");
	
	public static final String CAS_IP = "10.104.234.11";
	
	public static final int CAS_PORT = 8080; 
	
//	public static final int[] levels = new int[]{100,200,300,400,500,600,700,850,900,925,950,1000};
	public static final int[] levels = new int[]{500,700,850,1000};
	//锋面气旋类
	public static final int FRONTAL_JIANGHUAI_CYCLONE = 1001; //江淮气旋 110°-120°，30-35°N
	public static final int FRONTAL_XITAI_1_HIGH_PRESSURE=1002; //西太副高1  位置：20-25°N
	public static final int FRONTAL_SJET = 1003; //低空急流   位置：110-120°E，25-33°N
	public static final int FRONTAL_SHEAR_01 =1004; //切变线1  位置： 110-120°，E30-35°N
	public static final int FRONTAL_INVERTED_TROUGH_LINE=1005; //地面倒槽  位置：110°-120°，25-35°N  1000hpa高度场
	public static final int FRONTAL_HIGH_TROUGH_01_LINE=1006;  //高空槽1 位置：105°-115°E，30-40°N
	public static final int FRONTAL_WARM_SECTOR = 1007; //暖区 位置：110°-120°E，25-35°N 高度：850hpa温度场
	public static final int FRONTAL_COLD_TONGUE=1008; //冷舌 位置：110°-120°E，30-35°N 高度：850hpa温度场
		
	//西南涡切变类
	public static final int SOUTHWEST_VORTEX=2001; //西南涡  位置：105°-115E，30°-32
	public static final int SOUTHWEST_HIGH_TROUGH_01_LINE=2002;//位置：105°-110E，30°-40°
	public static final int SOUTHWEST_XITAI_1_HIGH_PRESSURE=2003; //西太副高1  位置：20-25°N
	public static final int SOUTHWEST__SJET = 2004; //低空急流   位置：110-120°E，25-30°N
	public static final int SOUTHWEST_SHEAR_01 =2005; //切变线1  位置： 110-120°，E30-35°N
	public static final int SOUTHWEST_INVERTED_TROUGH_LINE=2006; //地面倒槽  位置：110°-120°，25-35°N  1000hpa高度场
	public static final int SOUTHWEST_JIANGHAN_VORTEX=2007; //江汉平原涡 位置：110-115°E，28-32°N
	public static final int SOUTHWEST_HUABEI_HIGH_PRESSURE=2008; //位置：110-120°E，35-40°N
	
	
	//西南涡----东北气旋类
	public static final int NORTHEAST_CYCLONE=3001;//东北气旋 115°-120E，40°-50°  高度：850hPa高度场
	public static final int NORTHEAST_XITAI_1_HIGH_PRESSURE=3002; //西太副高1  位置：20-25°N
	public static final int NORTHEAST_SHEAR_02 =3003; //切变线2  位置： 110-120°E，30-40°N
	public static final int NORTHEAST_INVERTED_TROUGH_LINE=3004; //地面倒槽  位置：110°-120°，25-35°N  1000hpa高度场
	public static final int NORTHEAST_HIGH_TROUGH_02_LINE=3005;  //高空槽2 位置：105°-115°，35-45°N
	public static final int NORTHEAST_COLD_TONGUE=3006; //冷舌 位置：110°-120°E，30-35°N 高度：850hpa温度场
	
	//盛夏暖倒槽类
	public static final int SUMMER_HIGH_TROUGH_02_LINE=4001; //位置：105°-115°，35-45°N
	public static final int SUMMER_NORTH_CYCLONE=4002;//北方气旋 位置：110°-120°，35-45°N
	public static final int SUMMER_HUABEI_HIGH_PRESSURE=4003;//华北高压  位置： 110-120°E，35-40°N
	public static final int SUMMER_XITAI_1_HIGH_PRESSURE=4004; //西太副高2 位置：25-28°N
	public static final int SUMMER_INVERTED_TROUGH_LINE=4005; //地面倒槽  位置：110°-120°，30-35°N  1000hpa高度场
	public static final int SUMMER__SHEAR_03 =4006; //切变线1  位置： 10°-120°，35-40°N
	
	//登陆台风低压类
	public static final int TYPHOON_XITAI_3_HIGH_PRESSURE= 5001; //西太副高3 位置：35-40°N
	public static final int TYPHOON_DALU_HIGH_PRESSURE = 5002;//大陆高压   位置： 105-115°E，35-40°N
	public static final int TYPHOON = 5003;//台风  位置：110°-120°，20-25°N
}
