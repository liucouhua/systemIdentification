package recutil;

import java.util.ArrayList;

/**
 * 鍒囧彉绾�
 */
public class SShear{
	
	public static WeatherSystems getShear(VectorData wind, int level, float scale) {
		// TODO Auto-generated method stub
		
		VectorData tranDirection=new VectorData(wind.gridInfo);  // 瀹氫箟鍒濈寽骞虫祦椋庡悜涓鸿タ鍖楅
		tranDirection.v.setValue(-1.0f); 
		tranDirection.u.setValue(1.0f); 
		
		GridData directionShear = null,marker=null;
		ArrayList<Line>  shearLine=null;
		for(int k=0;k<2;k++){  //閫氳繃涓よ疆杩唬鑾峰彇鍒囧彉绾�
			directionShear = getMeanDirectionShear(tranDirection,wind);              //璁＄畻椋庡悜鍒囧彉
			marker = directionShear.add(-0.5f).sign01();
//			directionShear.writeToFile("G:/data/systemIdentify/directionShear.txt");  
			shearLine = SystemIdentification.HighValueAreaRidge(tranDirection, directionShear,level);  //鏍规嵁椋庡悜鍒囧彉閲忚绠楀垵姝ョ殑鍒囧彉绾夸綅缃�
			shearLine = LineDealing.cutLines(shearLine, marker);			// 娑堢┖
			LineDealing.smoothLines(shearLine, 10);							//骞虫粦
			shearLine = SystemIdentification.getLongLine(scale,shearLine);	//鍘荤煭
			tranDirection = SystemIdentification.getDirectionFromLine(shearLine, directionShear.sign01());  //鏍规嵁鍒囧彉绾块噸鏂拌绠楀钩娴侀
		}
		//LineDealing.writeToFile("G:/data/systemIdentify/shearLine1.txt",shearLine);		
		//tranDirection.writeToFile("G:/data/systemIdentify/tranDirection.txt");
		
		GridData VV = getLineVV(tranDirection,wind);                        //璁＄畻鍒囧彉绾夸袱渚椋庨�熶箣宸�
		//VV.writeToFile("G:/data/systemIdentify/VV.txt");
		GridData minShear = getMinLineShear(tranDirection,wind);            // 璁＄畻鍒囧彉绾夸袱渚ч鍚戝垏鍙樼殑杈冨皬鍊�
		//minShear.writeToFile("G:/data/systemIdentify/minShear.txt");
		
		GridData windShear = getMeanWindShear(tranDirection,wind);           //璁＄畻鍏ㄩ閫熷垏鍙�
		//windShear.writeToFile("G:/data/systemIdentify/windShear.txt");
		
		GridData lineDiv = getLineDiv(tranDirection,wind);					//璁＄畻鍒囧彉绾夸袱渚ф湞鍒囧彉绾跨殑杈愬悎
		//lineDiv.writeToFile("G:/data/systemIdentify/lineDiv.txt");
		
		GridData shearFeature = windShear.add(lineDiv).mutiply(marker);     //鍒囧彉绾跨殑鐗瑰緛閲忓畾涔変负 鍒囧彉绾夸袱渚х殑鍒囧彉+杈愬悎
		shearFeature.smooth(3);
		
		marker = VV.add(-0.3f).sign01().mutiply(minShear.add(0.3f).sign01()).mutiply(shearFeature.sign01()); //鏋勫缓娑堢┖鏉′欢
		//shearFeature.writeToFile("G:/data/systemIdentify/shearFeature.txt");
		//marker.writeToFile("G:/data/systemIdentify/marker.txt");
		
		shearLine = LineDealing.cutLines(shearLine, marker);              //娑堢┖
		shearLine = SystemIdentification.getLongLine(scale,shearLine);		//鍘荤煭
		
		for(int i=0;i<shearLine.size();i++) {
			Line line1= shearLine.get(i);
			float add_v = 0;
			for(int j=0;j< line1.point.size();j++) {
				float[] point = line1.point.get(j);
				float v1 = VectorMathod.getValue(wind.v, point);
				add_v = add_v+v1;
			}
			
			
			if(add_v>0) {
				line1.cold_or_warm = 1;
			}
			else {
				line1.cold_or_warm = -1;
			}
		}
		
		//LineDealing.writeToFile("G:/data/systemIdentify/shearLine2.txt",shearLine);	
		WeatherSystems shear = new WeatherSystems("shear",level);         // 瀹氫箟鍒囧彉绾跨郴缁燂紝璁剧疆杞寸嚎shearLine銆佺壒寰侀噺value浠ュ強鍒嗗尯ids锛屽苟灏嗗畠浠崗璋�
		shear.setAxes(shearLine);
		shear.setValue(shearFeature);
		GridData ids = SystemIdentification.getCuttedRegion(shearFeature,0);
//		//ids.writeToFile("G:/data/systemIdentify/ids.txt");
		shear.setIds(ids);
		shear.type ="切变线";
		shear.reset();
		
		return shear;
	}


	/*
	public static ArrayList<Line> getShear_oldEdition(VectorData wind,int level,float scale) {
		// TODO Auto-generated method stub
		//height.smooth(200);
		VectorData wd = VectorMathod.getDirection(wind);
		wd.u=wd.u.mutiply(4);
		wd.v = wd.v.mutiply(4);
		wd.writeToFile("G:/data/systemIdentify/wd.txt");
		GridData vor =VectorMathod.getVor(wind);
		vor.smooth(10);
		vor.writeToFile("G:/data/systemIdentify/vor.txt");
		GridData div =VectorMathod.getDiv(wind).mutiply(-1.0f);
		div.smooth(10);
		div.writeToFile("G:/data/systemIdentify/div.txt");
		GridData speed=VectorMathod.getMod(wind);
		speed.smooth(5);
		speed.writeToFile("G:/data/systemIdentify/speed.txt");
		
		VectorData tranDirection=new VectorData(wind.gridInfo);
		
		tranDirection.v.setValue(-1.0f); 
		tranDirection.u.setValue(1.0f); 
		GridData marker=(div.add(0.1f).sign01().mutiply(vor.sign01()));
		ArrayList<Line>  shearLine = null;
		GridData windMarker = new GridData(marker.gridInfo);
		windMarker.setValue(1);
		
		
			GridData adve=VectorMathod.getAdvection(tranDirection, speed);    //璁＄畻骞虫祦鍦�
		//	adve.writeToFile("G:/data/systemIdentify/adve.txt");
			ArrayList<Line> line0=LineDealing.creatLine(0.0f, adve);  //璁＄畻骞虫祦鍦�0绾匡紝浣滀负鑴婄嚎鎴栨Ы绾�
			ArrayList<Line> cutedLine1=LineDealing.cutLines(line0, marker); 	
			VectorData adve_grad_direction = VectorMathod.getDirection(VectorMathod.getGrads(adve));
			GridData adveOfAdve= VectorMathod.dot(tranDirection, adve_grad_direction); //
		//	adveOfAdve.writeToFile("G:/data/systemIdentify/adveOfAdve.txt");
			GridData markerAdv = adveOfAdve.add(-0.3f).sign01();
			
			shearLine=SystemIdentification.getLongLine(scale,LineDealing.cutLines(line0, marker)); 
			
			LineDealing.writeToFile("G:/data/systemIdentify/line0.txt",shearLine);
			
			marker.writeToFile("G:/data/systemIdentify/marker.txt");
			
			marker=(marker.mutiply(adveOfAdve.add(-0.3f).sign01()));					 //鐢熸垚marker鍦�
			ArrayList<Line> cutedLine2=LineDealing.cutLines(line0, marker); 	
			shearLine = SystemIdentification.getLongLine(scale,cutedLine2);
			LineDealing.smoothLines(shearLine, 10);
			LineDealing.writeToFile("G:/data/systemIdentify/shearLine.txt",shearLine);
			
			tranDirection = SystemIdentification.getDirectionFromLine(shearLine, windMarker);

		// 杈圭晫瑁佸壀
		int n = (int) (scale/111/wind.gridInfo.dlat);
		GridData markerR= new GridData(marker.gridInfo);
		for(int i= n;i<markerR.gridInfo.nlon-n;i++){
			for(int j=n;j<markerR.gridInfo.nlat;j++){
				markerR.dat[i][j]=1;
			}
		}
		marker = marker.mutiply(markerR);
		shearLine=LineDealing.cutLines(shearLine, marker);  //瑁佸壀鑾峰緱鑴婄嚎
		
		tranDirection.writeToFile("G:/data/systemIdentify/tranDirection.txt");
		
		
		GridData shearFeature = getMeanDirectionShear(tranDirection,wind);
		shearFeature.writeToFile("G:/data/systemIdentify/shearFeature.txt");
		GridData minShear = getMinLineShear(tranDirection,wind);
		minShear.writeToFile("G:/data/systemIdentify/minShear.txt");
		shearFeature = minShear;
		GridData lineDiv = getLineDiv(tranDirection,wind);
		lineDiv.writeToFile("G:/data/systemIdentify/lineDiv.txt");
		GridData VV = getLineVV(tranDirection,wind);
		VV.writeToFile("G:/data/systemIdentify/VV.txt");
		
		
		shearFeature = shearFeature.mutiply(minShear.sign01()).mutiply(lineDiv.sign01()).mutiply(VV.mutiply(-1).add(5.0f).sign01());
		shearFeature = shearFeature.mutiply(speed.add(-8f).mutiply(-1).sign01());
		shearFeature.smooth(3);
		shearFeature.writeToFile("G:/data/systemIdentify/shearFeature.txt");
		GridData adve1=VectorMathod.getAdvection(tranDirection, shearFeature);    //璁＄畻骞虫祦鍦�
		adve.writeToFile("G:/data/systemIdentify/adve.txt");
		
		line0=LineDealing.creatLine(0.0f, adve1);  //璁＄畻骞虫祦鍦�0绾匡紝浣滀负鑴婄嚎鎴栨Ы绾�
		
		marker=shearFeature.add(-0.01f).sign01();
		
		shearLine=LineDealing.cutLines(line0, marker); 
		LineDealing.smoothLines(shearLine, 10);
		shearLine = SystemIdentification.getLongLine(scale,shearLine);
		
		getShearStrenght(shearLine,lineDiv);
		
		return shearLine;
	}
	*/
	
	private static GridData getMeanDirectionShear(VectorData ptranWind, VectorData wind) {
		// TODO Auto-generated method stub
		VectorData mtranWind = VectorMathod.rotate(ptranWind, 180);
		VectorData pDirection = VectorMathod.getDirection(ptranWind);
		VectorData mDirection = VectorMathod.rotate(pDirection, 180);
		VectorData rWindDirection = VectorMathod.getDirection(VectorMathod.rotate(wind, 90));

		GridData shear = VectorMathod.dot(pDirection, rWindDirection);
		GridData upperShear =   getLineMean(0.0f,2.0f,mtranWind,shear);
		shear = VectorMathod.dot(mDirection, rWindDirection);
		GridData downShear =   getLineMean(0.0f,2.0f,ptranWind,shear);
		shear = upperShear.add(downShear);
		return shear;
	}
	
	private static GridData getMeanWindShear(VectorData ptranWind, VectorData wind) {
		// TODO Auto-generated method stub
		VectorData mtranWind = VectorMathod.rotate(ptranWind, 180);
		VectorData pDirection = VectorMathod.getDirection(ptranWind);
		VectorData mDirection = VectorMathod.rotate(pDirection, 180);
		VectorData rWind = VectorMathod.rotate(wind, 90);

		GridData shear = VectorMathod.dot(pDirection, rWind);
		GridData upperShear =   getLineMean(0.0f,2.0f,mtranWind,shear);
		shear = VectorMathod.dot(mDirection, rWind);
		GridData downShear =   getLineMean(0.0f,2.0f,ptranWind,shear);
		shear = upperShear.add(downShear);
		return shear;
	}
	
	private static GridData getMinLineShear(VectorData ptranWind, VectorData wind) {
		// TODO Auto-generated method stub
		VectorData mtranWind = VectorMathod.rotate(ptranWind, 180);
		VectorData pDirection = VectorMathod.getDirection(ptranWind);
		VectorData mDirection = VectorMathod.rotate(pDirection, 180);
		VectorData rWindDirection = VectorMathod.getDirection(VectorMathod.rotate(wind, 90));

		GridData shear = VectorMathod.dot(pDirection, rWindDirection);
		GridData upperShear =   getLineMean(0.0f,2.0f,mtranWind,shear);
		shear = VectorMathod.dot(mDirection, rWindDirection);
		GridData downShear =   getLineMean(0.0f,2.0f,ptranWind,shear);
		for(int i=0;i<shear.gridInfo.nlon;i++){
			for(int j=0;j<shear.gridInfo.nlat;j++){
				if(upperShear.dat[i][j]<downShear.dat[i][j]){
					shear.dat[i][j] = upperShear.dat[i][j];
				}
				else{
					shear.dat[i][j] = downShear.dat[i][j];
				}
			}
		}
		return shear;
	}
	private static GridData getLineDiv(VectorData ptranWind, VectorData wind) {
		// TODO Auto-generated method stub
		VectorData mtranWind = VectorMathod.rotate(ptranWind, 180);
		VectorData pDirection = VectorMathod.getDirection(ptranWind);
		VectorData mDirection = VectorMathod.rotate(pDirection, 180);
		
		GridData div = VectorMathod.dot(mDirection, wind);
		GridData upperDiv =   getLineMean(1.9f,2.0f,mtranWind,div);
		div = VectorMathod.dot(pDirection, wind);
		GridData downDiv =   getLineMean(1.9f,2.0f,ptranWind,div);
		
		div = upperDiv.add(downDiv).mutiply(-1);	
		return div;
	}
	
	private static GridData getLineVV(VectorData ptranWind, VectorData wind) {
		// TODO Auto-generated method stub
		VectorData windDirecion = VectorMathod.getDirection(wind);
		VectorData mtranWind = VectorMathod.rotate(ptranWind, 180);
		GridData upperV =   getLineMean(0.0f,2.0f,mtranWind,windDirecion.v);
		GridData downV =   getLineMean(0.0f,2.0f,ptranWind,windDirecion.v);
		GridData VV = upperV.mutiply(-1).add(downV);
		
		return VV;
	}
	

	public static GridData getRotateWind(VectorData crossShear, VectorData wind){
		GridData shearFeature = new GridData(wind.gridInfo);
		int nlon = shearFeature.gridInfo.nlon;
		int nlat = shearFeature.gridInfo.nlat;
		float dlat=shearFeature.gridInfo.dlat;
		float dlon=shearFeature.gridInfo.dlon;
		float slat=shearFeature.gridInfo.startlat;
		float slon=shearFeature.gridInfo.startlon;
		int i,j,k,p;
		float [] point0 = new float[2];
		float [] point = new float[2];
		float [][] winds = new float[3][];
		for(k = 0;k < 3; k++){
			winds[k] = new float[3];
		}
		
		float sr,sf,maxsf;
		float[] speeds =new float[3];
		float[] rotateSpeeds=new float[3];
		float[] divSpeeds=new float[3];
		float[] dxy = new float[2];
		boolean nearEdge;
		float[] d = new float[10];
		for(k = 0; k<d.length;k++){
			d[k]= 0.1f+0.1f*k;
		}
		float[] meandv = new float[2];
		float[] meanv = new float[2];
		float[] meanrv = new float[2];
		
		GridData gridSpeed = VectorMathod.getMod(wind);
		gridSpeed.smooth(5);
		
		for(j = 0; j < nlat; j++){
			point0[1] = slat + j * dlat;
			sr = (float) Math.cos(point0[1]*3.14/180);
			for(i = 0; i < nlon; i++){
				nearEdge =false;
				point0[0] = slon + i * dlon;
				shearFeature.dat[i][j] =-1;
				winds[0][0] = VectorMathod.getValue(wind.u, point0);
				winds[0][1] = VectorMathod.getValue(wind.v, point0);
				speeds[0] =(float) Math.sqrt(winds[0][0]*winds[0][0] + winds[0][1]* winds[0][1]);	
				maxsf=0;
				
				for(k=0;k<d.length;k++){
					for(p=-1;p<2;p=p+2){
						dxy[0] = d[k]*p*crossShear.u.dat[i][j];
						dxy[1] = d[k]*p*crossShear.v.dat[i][j];
						point[0] = point0[0] + dxy[0]/ sr; 
						point[1] = point0[1] + dxy[1];
						winds[p+1][0] = VectorMathod.getValue(wind.u, point);
						winds[p+1][1] = VectorMathod.getValue(wind.v, point);
						if(winds[p+1][0]==9999){
							nearEdge =true;
							break;
						}
						speeds[p+1] = (float) Math.sqrt(winds[p+1][0]*winds[p+1][0] + winds[p+1][1]* winds[p+1][1]);
						rotateSpeeds[p+1] = (- winds[p+1][0] * dxy[1] + winds[p+1][1] * dxy[0])/d[k];
						divSpeeds[p+1] = (- winds[p+1][0] * dxy[0] - winds[p+1][1] * dxy[1])/d[k];
						
					}
					if(nearEdge){
						break;
					}
				//	sf = Math.max(0,rotateSpeeds[0]/speeds[0]+rotateSpeeds[2]/speeds[2]);c
					
				//	sf = (rotateSpeeds[0]/speeds[0]+rotateSpeeds[2]/speeds[2])/(d[k]);
					
					//if(divSpeeds[0]+divSpeeds[2]<0)sf=0;
					//if(winds[0][1]*winds[2][1]>0)sf=0;
					meanrv[0] = rotateSpeeds[0];meanrv[1]=rotateSpeeds[2];
					meandv[0] = divSpeeds[0];meandv[1] = divSpeeds[2];
					meanv[0]= winds[0][1];meanv[1]= winds[2][1];
				//	if(sf>maxsf)maxsf =sf;
				}
				if(nearEdge){
					continue;
				}
				maxsf =meanrv[0]/d.length+ meanrv[1]/d.length;
				if(meanrv[0]<0 || meanrv[1]<0) maxsf =0;
				if(meandv[0]+meandv[1]<0)maxsf=0;
				if(meanv[0]*meanv[1]>4)maxsf=0;
				shearFeature.dat[i][j] =maxsf;  //gridSpeed.dat[i][j];
			}
		}
		shearFeature.smooth(5);
		return shearFeature;
	}
	
	public static GridData getLineMean(float start,float end,VectorData vector , GridData grid){
		GridData lineMean = new GridData(grid.gridInfo);
		int nlon = lineMean.gridInfo.nlon;
		int nlat = lineMean.gridInfo.nlat;
		float dlat=lineMean.gridInfo.dlat;
		float dlon=lineMean.gridInfo.dlon;
		float slat=lineMean.gridInfo.startlat;
		float slon=lineMean.gridInfo.startlon;
		int i,j,k;
		float [] point0 = new float[2];
		float [] point = new float[2];
		float sr;
		float[] dxy = new float[2];
		boolean nearEdge;
		float[] d = new float[10];
		for(k = 0; k<d.length;k++){
			d[k]= start+(end-start)*(k+1)/d.length;
		}
		float value;
		for(j = 0; j < nlat; j++){
			point0[1] = slat + j * dlat;
			sr = (float) Math.cos(point0[1]*3.14/180);
			for(i = 0; i < nlon; i++){
				nearEdge =false;
				point0[0] = slon + i * dlon;
				for(k=0;k<d.length;k++){
					dxy[0] = d[k]*vector.u.dat[i][j];
					dxy[1] = d[k]*vector.v.dat[i][j];
					point[0] = point0[0] + dxy[0]/ sr; 
					point[1] = point0[1] + dxy[1];
					value = VectorMathod.getValue(grid, point);
					if(value == 9999){
						nearEdge = true;
						break;
					}
					lineMean.dat[i][j] += VectorMathod.getValue(grid, point);
				}
				if(nearEdge){
					continue;
				}
				lineMean.dat[i][j] /= d.length;
			}
		}
		return lineMean;
	}
	
	/*
	public static void getShearStrenght(ArrayList<Line> line, GridData grid){
		float[] point = new float[2];
		float value ;
		for(int i = 0; i < line.size(); i++){
			line.get(i).lenght = LineDealing.lineLenght(line.get(i));
			float maxStrenght=0;
			for(int j = 0; j < line.get(i).point.size()-1; j++){			
				point  = line.get(i).point.get(j);
				value = VectorMathod.getValue(grid, point);
			}
		}
		
	
	}
*/


	
}
