package recutil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 鎬ユ祦
 * @author zhang
 *
 */
public class SJet{
	
	//璁剧疆鎬ユ祦鐨勯�熷害闃堝��
	public static float getJetSpeed(int level){
		if(level >= 850){
			return 12.0f;  //850hpa浠ヤ笅璁剧疆涓�12.0m/s
		}
		else if(level == 700){
			return 16.0f;  //700hpa璁剧疆涓�16m/s
		}
		else{
			return 30.0f;  //700hpa浠ヤ笂璁剧疆涓�30m/s
		}
	}
	
	
	public static WeatherSystems getJet(VectorData wind, int level, float scale) {
		// TODO Auto-generated method stub
		WeatherSystems jet = new WeatherSystems("jet",level);
		float jetSpeed = getJetSpeed(level);
		GridData speed = VectorMathod.getMod(wind);  //椋庨��- 鎬ユ祦椋庨�熼槇鍊�
		GridData speed0 = speed.copy();
		//speed.smooth(3);
		//speed.writeToFile("G:/data/systemIdentify/speed.txt");
		GridData v = wind.v.copy();
		speed =  speed.add(-jetSpeed);
		GridData marker = speed.sign01();  // 椋庨�熷ぇ浜庨槇鍊肩殑閮ㄥ垎灏嗚淇濈暀
		
		//700hpa 浠ヤ笅鍙繚鐣欏亸鍗楅鎬ユ祦锛屼晶閲嶆按姹借緭閫佸拰鍔ㄥ姏杈愬悎
		//500hpa鍙婁互涓婃�ユ祦璁ㄨ鐨勬槸鎬ユ祦鍑哄彛鍜屽叆鍙ｅ尯瀵规丁搴﹀钩娴佺殑宸紓瀵艰嚧鐨勫姩鍔涘己杩紝鍥犳涓嶅尯鍒嗗崡鍖楅
		if(level>=700) marker =marker.mutiply(v.sign01());  
		
		//marker.writeToFile("D:\\develop\\java\\201905-weahter_identification\\output\\marker.txt");
		speed = speed.mutiply(marker);   //淇濈暀鎬ユ祦鍖虹殑椋庨��
		speed.smooth(5);
		marker = speed.sign01();
		//浠ュ瀭鐩存�ユ祦鐨勬柟鍚戝椋庨�熻繘琛屽钩娴�
		VectorData rw = VectorMathod.rotate(wind,-90); 
		ArrayList<Line> jetLine = SystemIdentification.HighValueAreaRidge(rw,speed,level);
		//LineDealing.writeToFile("D:\\develop\\java\\201905-weahter_identification\\output\\jet_700\\line.txt",jetLine);
		jetLine = LineDealing.cutLines(jetLine, marker);   //鐢ㄩ閫熷拰椋庡悜鏉′欢瀵规�ユ祦杞寸嚎杩涜瑁佸壀
		jetLine = SystemIdentification.getLongLine(scale,jetLine);  //淇濈暀澶ч洦scale鐨勬�ユ祦
		LineDealing.smoothLines(jetLine, 10);   // 瀵圭嚎鏉¤繘琛屽钩婊�
		
		//speed.writeToFile("D:\\develop\\java\\201905-weahter_identification\\output\\jet_700\\speed.txt");
		GridData ids = SystemIdentification.getCuttedRegion(speed,1);              //鏍规嵁閫熷害杩涜鍒濇鍒嗗尯
		
		ids = SystemIdentification.combineStrongConnectingRegion_2d(ids,0.8f);   //灏嗙揣閭荤殑鍒嗗尯杩涜鍚堝苟
		
		
		
		jet.setAxes(jetLine);  // 灏嗗ぉ姘旂郴缁熺殑杞寸嚎灞炴�ц缃负jetLine
		jet.setValue(speed0);  //灏嗗ぉ姘旂郴缁熺殑value 灞炴�ц缃负speed
		jet.setIds(ids);      // 灏嗗ぉ姘斿绯荤粺鐨勫垎鍖鸿缃负ids
		jet.reset();         //灏嗗垎鍖鸿缃笌杞寸嚎璁剧疆杩涜鍖归厤
		return jet;
	}


}
