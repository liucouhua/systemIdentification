import java.util.ArrayList;

public class STrough{
	
	public static WeatherSystems getTrough(GridData height, int level, float scale) {
		
		GridData curVor=VectorMathod.getCurvatureVor(height);
		curVor.smooth(100);
		curVor.writeToFile("G:/data/systemIdentify/curVor.txt");
		VectorData wind = VectorMathod.getGeostrophicWind(height);
		wind.writeToFile("G:/data/systemIdentify/wind.txt");
		ArrayList<Line> trough = SystemIdentification.HighValueAreaRidge(wind,curVor,level);
		
		GridData marker=(curVor.add(-0.5f).sign01());   //曲率涡度阈值
		trough=LineDealing.cutLines(trough, marker); 
		LineDealing.writeToFile("G:/data/systemIdentify/markLine1.txt", trough);
		
		
		GridData windSpeed = VectorMathod.getMod(wind);
		windSpeed.writeToFile("G:/data/systemIdentify/windSpeed.txt");
		marker = marker.mutiply(windSpeed.add(-4.0f));    //风速阈值
		trough=LineDealing.cutLines(trough, marker); 
		LineDealing.writeToFile("G:/data/systemIdentify/markLine2.txt", trough);
		
		GridData adve=VectorMathod.getAdvection(wind, curVor);    //计算平流场
		GridData curOfAdve=VectorMathod.getCurvature(adve);
		marker = marker.mutiply(curOfAdve.add(-0.4f).mutiply(-1).sign01());  // 逆时针弯曲度阈值
		curOfAdve.writeToFile("G:/data/systemIdentify/curOfAdve.txt");
		trough=LineDealing.cutLines(trough, marker); 
		LineDealing.writeToFile("G:/data/systemIdentify/markLine3.txt", trough);
		
		
		trough = SystemIdentification.getLongLine(scale,trough);
		
		GridData ids = SystemIdentification.getCuttedRegion(curVor);
		marker=(curVor.add(-0.05f).sign01());
		ids = ids.mutiply(marker);
		ids.writeToFile("G:/data/systemIdentify/ids.txt");
		
		WeatherSystems troughS = new WeatherSystems("trough",level);  
	
		troughS.setAxes(trough);
		troughS.setValue(curVor.mutiply(marker));
		troughS.setIds(ids);
		
		troughS.reset();
		
		return troughS;
	}
	
	
	public static ArrayList<Line> getTrough_oldEdition(float scale, GridData height) {
		// TODO Auto-generated method stub
		//height.smooth(200);
		GridData cur =VectorMathod.getCurvature(height);
		cur.smooth(100);
		cur.writeToFile("G:/data/systemIdentify/cur.txt");
		VectorData wind = VectorMathod.rotate(VectorMathod.getGrads(height),90);
		VectorData tranDirection=VectorMathod.getDirection(wind);
		tranDirection.writeToFile("G:/data/systemIdentify/tranDirection.txt");
		GridData adve=VectorMathod.getAdvection(tranDirection, cur);    //计算平流场
		adve.writeToFile("G:/data/systemIdentify/adve.txt");
		ArrayList<Line> trough=new ArrayList<Line>();
		ArrayList<Line> line0=LineDealing.creatLine(0.0f, adve);  //计算平流场0线，作为脊线或槽线
		GridData marker=(cur.add(-0.02f).sign01());
		ArrayList<Line> cutedTrough1=LineDealing.cutLines(line0, marker); 
		LineDealing.writeToFile("G:/data/systemIdentify/markLine1.txt", cutedTrough1);
		
		GridData windSpeed = VectorMathod.getMod(wind);
		windSpeed.writeToFile("G:/data/systemIdentify/windSpeed.txt");
		marker = marker.mutiply(windSpeed.add(-0.3f));
		cutedTrough1=LineDealing.cutLines(line0, marker); 
		LineDealing.writeToFile("G:/data/systemIdentify/markLine2.txt", cutedTrough1);
		
		GridData curVor=VectorMathod.getCurvatureVor(height);
		curVor.smooth(100);
		curVor.writeToFile("G:/data/systemIdentify/curVor.txt");
		marker = marker.mutiply(curVor.add(-0.02f).sign01());
		cutedTrough1=LineDealing.cutLines(line0, marker); 
		LineDealing.writeToFile("G:/data/systemIdentify/markLine3.txt", cutedTrough1);
	//	GridData vor=VectorMathod.getVor(wind);
	//	vor.smooth(100);
	//	marker = marker.mutiply(vor.add(-0.001f).sign01());	
		
		VectorData adve_grad_direction = VectorMathod.getDirection(VectorMathod.getGrads(adve));
		VectorData roWind = VectorMathod.rotate(tranDirection,-15);
		GridData adveOfAdve= VectorMathod.dot(roWind, adve_grad_direction); //计算平流的平流，在槽线位置平流的平流为正，脊线位置平流的平流为负
		adveOfAdve.writeToFile("G:/data/systemIdentify/adveOfAdve.txt");
		marker=(marker.mutiply(adveOfAdve.add(0.6f).mutiply(-1).sign01())).sign01();					 //生成marker场
		cutedTrough1=LineDealing.cutLines(line0, marker); 
		LineDealing.writeToFile("G:/data/systemIdentify/markLine4.txt", cutedTrough1);
		
		
		GridData curOfAdve=VectorMathod.getCurvature(adve);
		marker = marker.mutiply(curOfAdve.add(-0.4f).mutiply(-1).sign01());
		curOfAdve.writeToFile("G:/data/systemIdentify/curOfAdve.txt");
		cutedTrough1=LineDealing.cutLines(line0, marker); 
		LineDealing.writeToFile("G:/data/systemIdentify/markLine5.txt", cutedTrough1);
		


		// 边界裁剪
		int n = (int) (scale/111/height.gridInfo.dlat);
		GridData markerR= new GridData(marker.gridInfo);
		for(int i= n;i<markerR.gridInfo.nlon-n;i++){
			for(int j=n;j<markerR.gridInfo.nlat-n;j++){
				markerR.dat[i][j]=1;
			}
		}
		marker = marker.mutiply(markerR);
		ArrayList<Line> cutedTrough=LineDealing.cutLines(line0, marker);  //裁剪获得脊线
		
		trough = SystemIdentification.getLongLine(scale,cutedTrough);
		
		cur = cur.mutiply(markerR);
		SystemIdentification.getLineStrenght(trough, cur,tranDirection);
		return trough;
	}

}
