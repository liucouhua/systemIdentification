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
		speed = speed.add(-0.5f);
		marker = speed.sign01();
		//浠ュ瀭鐩存�ユ祦鐨勬柟鍚戝椋庨�熻繘琛屽钩娴�
		VectorData rw = VectorMathod.rotate(wind,90); 
		ArrayList<Line> jetLine = SystemIdentification.HighValueAreaRidge(rw,speed,level);
		LineDealing.writeToFile("D:\\develop\\java\\201905-weahter_identification\\output\\jet_850\\line.txt",jetLine);
		jetLine = LineDealing.cutLines(jetLine, marker);   //鐢ㄩ閫熷拰椋庡悜鏉′欢瀵规�ユ祦杞寸嚎杩涜瑁佸壀
		jetLine = SystemIdentification.getLongLine(scale,jetLine);  //淇濈暀澶ч洦scale鐨勬�ユ祦
		
		// 从线条的两端沿着风场方向延伸
		extend_lines(jetLine,wind,jetSpeed);
		
		LineDealing.smoothLines(jetLine, 10);   // 瀵圭嚎鏉¤繘琛屽钩婊�
		

		
		speed.writeToFile("D:\\develop\\java\\201905-weahter_identification\\output\\jet_850\\speed.txt");
		GridData ids = SystemIdentification.getCuttedRegion(speed,1);              //鏍规嵁閫熷害杩涜鍒濇鍒嗗尯
		
		//ids = SystemIdentification.combineStrongConnectingRegion_2d(ids,0.99f);   //灏嗙揣閭荤殑鍒嗗尯杩涜鍚堝苟
		
		jet.type = "急流";
		
		jet.setAxes(jetLine);  // 灏嗗ぉ姘旂郴缁熺殑杞寸嚎灞炴�ц缃负jetLine
		jet.setValue(speed0);  //灏嗗ぉ姘旂郴缁熺殑value 灞炴�ц缃负speed
		jet.setIds(ids);      // 灏嗗ぉ姘斿绯荤粺鐨勫垎鍖鸿缃负ids
		jet.reset();         //灏嗗垎鍖鸿缃笌杞寸嚎璁剧疆杩涜鍖归厤
		
		return jet;
	}


	private static void extend_lines(ArrayList<Line> jetLine, VectorData wind, float jetSpeed) {
		// TODO Auto-generated method stub
		
		int nline = jetLine.size();
		float u,v;
		float [] ps = new float[2];
		for(int i = 0;i <nline;i++) {
			
			Line line1 = jetLine.get(i);
			int npoint = line1.point.size();
			if(npoint >4) {
				line1.point.remove(npoint - 1);
				line1.point.remove(0);
			}
			npoint = line1.point.size();
			ps = line1.point.get(0);
			float dot;
			
			float dx0 = line1.point.get(npoint -1)[1] - line1.point.get(npoint-2)[0];
			float dy0 = line1.point.get(npoint -1)[1] - line1.point.get(npoint-2)[1];
			
			for(int j = 0;j <10000;j++) {
				u = VectorMathod.getValue(wind.u,ps);
				v = VectorMathod.getValue(wind.v, ps);
				if(u * u + v * v < jetSpeed * jetSpeed)break;
				dot = u * dx0 + v * dy0;
				if(dot <0) {
					break;
				}
				
				float[] p1 = new float[2];
				p1[0] = ps[0] - u * 0.05f;
				p1[1] = ps[1] - v * 0.05f;
				if(p1[0]<=wind.gridInfo.startlon+1 || p1[0] >= wind.gridInfo.endlon-1) break;
				if(p1[1]<=wind.gridInfo.startlat+1 || p1[1] >= wind.gridInfo.endlat-1) break;
				line1.point.add(0,p1);
				ps = p1;
				
			}
			
			
			dx0 = line1.point.get(1)[0] - line1.point.get(0)[0];
			dy0 = line1.point.get(1)[1] - line1.point.get(0)[1];
			
			npoint = line1.point.size();
			float[] pe = line1.point.get(npoint -1);
			for(int j = 0;j <10000;j++) {
				u = VectorMathod.getValue(wind.u,pe);
				v = VectorMathod.getValue(wind.v, pe);
				if(u * u + v * v < jetSpeed * jetSpeed)break;
				dot = u * dx0 + v * dy0;
				//cox = dy0 * u - dx0 * v;
				//System.out.println(dx0 + " " + dy0 + " " + u +" " +v +"\n");
				//System.out.print(dot+"\n");
			
				if(dot <0) {
					break;
				}
				
				float[] p1 = new float[2];
				p1[0] = pe[0] + u * 0.05f;
				p1[1] = pe[1] + v * 0.05f;
				if(p1[0]<=wind.gridInfo.startlon+1 || p1[0] >= wind.gridInfo.endlon-1) break;
				if(p1[1]<=wind.gridInfo.startlat+1 || p1[1] >= wind.gridInfo.endlat-1) break;
				line1.point.add(p1);
				pe = p1;
			}
			
			
			
		}
		
	}
	
}