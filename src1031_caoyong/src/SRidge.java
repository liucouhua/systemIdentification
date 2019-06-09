import java.util.ArrayList;

public class SRidge{

	public static WeatherSystems getRidge(GridData height, int level, float scale) {
		// TODO Auto-generated method stub
		GridData curVor=VectorMathod.getCurvatureVor(height).mutiply(-1);
		curVor.smooth(100);
		curVor.writeToFile("G:/data/systemIdentify/curVor.txt");
		VectorData wind = VectorMathod.getGeostrophicWind(height);
		wind.writeToFile("G:/data/systemIdentify/wind.txt");
		ArrayList<Line> ridge = SystemIdentification.HighValueAreaRidge(wind,curVor,level);
		
		GridData marker=(curVor.add(-1.0f).sign01());   //曲率涡度阈值
		ridge=LineDealing.cutLines(ridge, marker); 
		LineDealing.writeToFile("G:/data/systemIdentify/markLine1.txt", ridge);
		
		
		GridData u = wind.u;
		u.writeToFile("G:/data/systemIdentify/windSpeed.txt");
		marker = marker.mutiply(u.add(-6.0f));    //该类的方法侧重识别西风带中的脊线，需满足一定的西风风速条件
		ridge=LineDealing.cutLines(ridge, marker); 
		LineDealing.writeToFile("G:/data/systemIdentify/markLine2.txt", ridge);
		
		GridData adve=VectorMathod.getAdvection(wind, curVor);    //计算平流场
		GridData curOfAdve=VectorMathod.getCurvature(adve);
		marker = marker.mutiply(curOfAdve.add(-0.4f).mutiply(-1).sign01());  // 逆时针弯曲度阈值
		curOfAdve.writeToFile("G:/data/systemIdentify/curOfAdve.txt");
		ridge=LineDealing.cutLines(ridge, marker); 
		LineDealing.writeToFile("G:/data/systemIdentify/markLine3.txt", ridge);
		
		
		ridge = SystemIdentification.getLongLine(scale,ridge);
		
		GridData ids = SystemIdentification.getCuttedRegion(curVor);
		marker=(curVor.add(-1.0f).sign01());
		ids = ids.mutiply(marker);
		ids.writeToFile("G:/data/systemIdentify/ids.txt");
		
		WeatherSystems ridgeS = new WeatherSystems("trough",level);  
	
		ridgeS.setAxes(ridge);
		ridgeS.setValue(curVor.mutiply(marker));
		ridgeS.setIds(ids);
		
		ridgeS.reset();
		
		return ridgeS;
	}
	public static ArrayList<Line> getRidges1_oldEdition(float scale, GridData height0){
		GridData height= height0.mutiply(-1);
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
		GridData marker=(cur.add(-0.1f).sign01());
		ArrayList<Line> cutedTrough1=LineDealing.cutLines(line0, marker); 
		LineDealing.writeToFile("G:/data/systemIdentify/markLine1.txt", cutedTrough1);
		
		GridData windSpeed = VectorMathod.getMod(wind);
		windSpeed.writeToFile("G:/data/systemIdentify/windSpeed.txt");
		marker = marker.mutiply(windSpeed.add(-0.0f));
		cutedTrough1=LineDealing.cutLines(line0, marker); 
		LineDealing.writeToFile("G:/data/systemIdentify/markLine2.txt", cutedTrough1);
		
		GridData curVor=VectorMathod.getCurvatureVor(height);
		curVor.smooth(100);
		curVor.writeToFile("G:/data/systemIdentify/curVor.txt");
		marker = marker.mutiply(curVor.add(-0.00f).sign01());
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
			for(int j=n;j<markerR.gridInfo.nlat;j++){
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
	
	public static ArrayList<Line> getRidges(float scale, GridData height) {
		// TODO Auto-generated method stub
		//height.smooth(200);
		GridData cur =VectorMathod.getCurvature(height).mutiply(-1);
		cur.smooth(100);
	 //   cur.writeToFile("G:/data/systemIdentify/cur.txt");
		VectorData wind = VectorMathod.rotate(VectorMathod.getGrads(height),90);
		
		VectorData tranWind=new VectorData(height.gridInfo);
		tranWind.u.setValue(0);
		tranWind.v.setValue(-1);
		
		VectorData tranDirection=VectorMathod.getDirection(tranWind);
		GridData adve=VectorMathod.getAdvection(tranDirection, cur);    //计算平流场
		ArrayList<Line> line0=LineDealing.creatLine(0.0f, adve);  //计算平流场0线，作为脊线或槽线
		GridData marker=(cur.add(-0.05f).sign01());
		
		ArrayList<Line> trough =SystemIdentification.lineSystem(scale,cur,marker,tranWind);
		
		/*
		GridData curOfAdve=VectorMathod.getCurvature(adve);
		curOfAdve.writeToFile("G:/data/systemIdentify/curOfAdve.txt");
		//marker = marker.mutiply(curOfAdve.add(-0.4f).mutiply(-1).sign01());
		
		VectorData adve_grad_direction = VectorMathod.getDirection(VectorMathod.getGrads(adve));
		VectorData roWind = VectorMathod.rotate(tranDirection,0);
		GridData adveOfAdve= VectorMathod.dot(roWind, adve_grad_direction); //计算平流的平流，在槽线位置平流的平流为正，脊线位置平流的平流为负
		adveOfAdve.writeToFile("G:/data/systemIdentify/adveOfAdve.txt");

		marker=marker.mutiply(adveOfAdve.add(0.6f).mutiply(-1).sign01());					 //生成marker场

		ArrayList<Line> cutedTrough=LineDealing.cutLines(line0, marker);  //裁剪获得脊线
		ArrayList<Line> trough = getLongTrough(scale,cutedTrough);
		*/
		return trough;
	}

}
