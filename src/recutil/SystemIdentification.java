package recutil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class SystemIdentification {
	
		static ArrayList<Line> getLongLine(float scale, ArrayList<Line> lines) {
			// TODO Auto-generated method stub
			ArrayList<Line> longLines=new ArrayList<Line>();
			for(Line line: lines ){
				float lineLenght=LineDealing.lineLenght(line);
				if(lineLenght>scale) longLines.add(line);
			}
			return longLines;
		}

		static VectorData getDirectionFromLine(ArrayList<Line> trough, GridData marker) {
			
			// 根据线条生成垂直于线条的平流风
			VectorData paDirect=new VectorData(marker.gridInfo);
			VectorData veDirect=new VectorData(marker.gridInfo);
			
			GridData nearMarker=marker.copy();
			float slon=marker.gridInfo.startlon;
			float slat=marker.gridInfo.startlat;
			float dlon=marker.gridInfo.dlon;
			float dlat=marker.gridInfo.dlat;
			int nlon=marker.gridInfo.nlon;
			int nlat=marker.gridInfo.nlat;
			for(Line line :trough){
				int pointNum=line.point.size();
				for(int i=0;i<pointNum-1;i++){
					//将每个脊线点的方向标记到附近的格点上
					float lon=line.point.get(i)[0];
					float lat=line.point.get(i)[1];
					float u=(line.point.get(i+1)[0]-line.point.get(i)[0]);
					float v=(line.point.get(i+1)[1]-line.point.get(i)[1]);
					float mod=(float) Math.sqrt(u*u+v*v);
					float du=u/mod;
					float dv=v/mod;
					int ig=(int)((lon-slon)/dlon);
					int jg=(int)((lat-slat)/dlat);
					for(int p=0;p<2;p++){
						for(int q=0;q<2;q++){
							nearMarker.dat[ig+p][jg+q]=0;
							paDirect.u.dat[ig+p][jg+q]=du;
							paDirect.v.dat[ig+p][jg+q]=dv;
						}
					}
				}
			}
		    float  maxChange=1;
//			nearMarker.writeToFile("nearMarker.txt");
			int smTime=0;
			while(maxChange>0.1 && smTime<100){
				smTime++;
				maxChange=0;
				for(int i=1;i<nlon-1;i++){
					for(int j=1;j<nlat-1;j++){
						if(nearMarker.dat[i][j]==1){
							float smNum=0;
							float smU=0;
							float smV=0;
							for(int p=-1;p<2;p++){
								for(int q=-1;q<2;q++){
									if(marker.dat[i+p][j+q]==1){
										smNum++;
										smU+=paDirect.u.dat[i+p][j+q];
										smV+=paDirect.v.dat[i+p][j+q];
									}
								}
							}
							smU/=smNum;
							smV/=smNum;
							float change=Math.abs(paDirect.u.dat[i][j]-smU);
							if(change>maxChange)maxChange=change;
							change=Math.abs(paDirect.v.dat[i][j]-smV);
							if(change>maxChange)maxChange=change;
							
							paDirect.u.dat[i][j]=smU;
							paDirect.v.dat[i][j]=smV;
							
						}
					}
				}
				paDirect = VectorMathod.getDirection(paDirect);
			}
			veDirect=VectorMathod.rotate(paDirect, 90.0f);
			// 求平均,将脊线附近的平均风速赋值给空白区域
			float meanU=veDirect.u.mean();
			float meanV=veDirect.v.mean();
			GridData speed = VectorMathod.getMod(veDirect);
			for(int i=0;i<nlon;i++){
				for(int j=0;j<nlat;j++){
					if(speed.dat[i][j]<0.1){
						veDirect.u.dat[i][j]=meanU;
						veDirect.v.dat[i][j]=meanV;
					}
				}
			}
			
			veDirect=VectorMathod.getDirection(veDirect);
		
			//为使得非marker区域风场和marker区域之间平滑过渡
			for(int k=0;k<3;k++){
				for(int i=1;i<nlon-1;i++){
					for(int j=1;j<nlat-1;j++){
						if(marker.dat[i][j]==0){
							float smU=0;
							float smV=0;
							for(int p=-1;p<2;p++){
								for(int q=-1;q<2;q++){
									smU+=veDirect.u.dat[i+p][j+q];
									smV+=veDirect.v.dat[i+p][j+q];
								}
							}
							veDirect.u.dat[i][j]=smU/9;
							veDirect.v.dat[i][j]=smV/9;
						}
					}
				}
			}
			veDirect=VectorMathod.getDirection(veDirect);
			return veDirect;
		}
/*
		public static ArrayList<Line> lineSystem(float scale, VectorData tranWind, GridData feature, GridData marker0) {
			// TODO Auto-generated method stub
			
			//首先根据feature绘制其平流0线（其中平流风的初始设置为西北风），在使用marker进行裁剪
			ArrayList<Line> trough=new ArrayList<Line>();
			VectorData tranDirection=VectorMathod.getDirection(tranWind);
			GridData marker1=marker0.sign01();
			GridData adve=VectorMathod.getAdvection(tranDirection, feature);    //计算平流场
			adve.writeToFile("G:/data/systemIdentify/adve.txt");
			GridData curOfAdve=VectorMathod.getCurvature(adve);
			curOfAdve.writeToFile("G:/data/systemIdentify/curOfadve.txt");
			VectorData adve_grad = VectorMathod.getGrads(adve);
			VectorData adve_grad_direction = VectorMathod.getDirection(adve_grad);
		//	tranWind.writeToFile("G:/data/systemIdentify/tranWind.txt");
		//	adve_grad_direction.writeToFile("G:/data/systemIdentify/adve_grad_direction.txt");
		//	VectorData mixWind = tranWind.copy();
		//	mixWind.u = tranWind.u.add(adve_grad_direction.u.mutiply(-0.5f));
		//	mixWind.v = tranWind.v.add(adve_grad_direction.v.mutiply(-0.5f));
		
		//	mixWind = VectorMathod.getDirection(mixWind);
		//	mixWind.writeToFile("G:/data/systemIdentify/mixWind.txt");
		//	GridData adve1=VectorMathod.getAdvection(mixWind, feature);    //计算平流场
		//	adve1.writeToFile("G:/data/systemIdentify/adve1.txt");
			ArrayList<Line> origainalTrough=LineDealing.creatLine(0.0f, adve);  //计算平流场0线，作为脊线或槽线
			VectorData roWind = VectorMathod.rotate(tranDirection,-15);
			GridData adveOfAdve= VectorMathod.dot(roWind, adve_grad_direction); //计算平流的平流，在槽线位置平流的平流为正，脊线位置平流的平流为负
			adveOfAdve.writeToFile("G:/data/systemIdentify/adveOfAdve.txt");
			adveOfAdve = adveOfAdve.add(0.6f);
			curOfAdve = curOfAdve.add(-0.4f);
			adveOfAdve.writeToFile("G:/data/systemIdentify/adveOfAdve.txt");
			GridData minalAofA=adveOfAdve.mutiply(-1).sign01();    //为提取脊线部分，保留平流的平流为负的区域
			GridData minalcofA=curOfAdve.mutiply(-1).sign01();
			minalAofA.writeToFile("G:/data/systemIdentify/maOfa.txt");
			minalcofA.writeToFile("G:/data/systemIdentify/maOfc.txt");
			GridData marker=marker1.mutiply(minalAofA).mutiply(minalcofA);					 //生成marker场
			marker.writeToFile("G:/data/systemIdentify/marker.txt");
			ArrayList<Line> cutedTrough=LineDealing.cutLines(origainalTrough, marker);  //裁剪获得脊线
			LineDealing.writeToFile("G:/data/systemIdentify/cutedtroughs.txt", cutedTrough);
			trough = getLongTrough(scale,cutedTrough);
			LineDealing.writeToFile("G:/data/systemIdentify/troughs.txt", trough);
			return trough;
			
		}
		
	
		
		public static void getLineStrenght(ArrayList<Line> line, GridData grid, VectorData wind){
			ArrayList<float[]> strenght =new ArrayList<float[]>();
			// 将风速转换成等经纬度网格矢量
			int nlat=grid.gridInfo.nlat;
			int nlon=grid.gridInfo.nlon;
			int j_1,j1,i_1,i1,dj,di;
			float sr;
			float dlat=grid.gridInfo.dlat;
			float dlon=grid.gridInfo.dlon;
			float slat=grid.gridInfo.startlat;
			float slon=grid.gridInfo.startlon;
	
			for(int i = 0; i < line.size(); i++){
				line.get(i).lenght = LineDealing.lineLenght(line.get(i));
				float maxStrenght=0;
				float totalStrenght=0;
				for(int j = 0; j < line.get(i).point.size()-1; j++){
					//对每个点沿着风向对上下游的feature进行积分，积分停止点在feature极小点处或marker=0处停止
					sr = (float) Math.cos(line.get(i).point.get(j)[1]*3.14/180);
					float dx =sr* (line.get(i).point.get(j+1)[0]-line.get(i).point.get(j)[0]);
					float dy = line.get(i).point.get(j+1)[1]-line.get(i).point.get(j)[1];
					
					float[] startP = line.get(i).point.get(j);
					float u0 = VectorMathod.getValue(wind.u,startP);
					float v0 = VectorMathod.getValue(wind.v,startP);
					float cross = Math.abs(-dx*v0 + dy*u0);
					//往上游积分
					float[] point = new float[2];
					point[0] = startP[0]; point[1] =startP[1];
					
					
					float stepLenght = 0.1f;
					float u,v ;
					float lineValue = VectorMathod.getValue(grid,point);
					if(lineValue >maxStrenght){
						maxStrenght= lineValue;
					}
					float value1,value0=lineValue,value_1=lineValue;
					float mark1;
					float onePointLenght=0,onePointStrenght=0;
					
					while(true){
						u= VectorMathod.getValue(wind.u,point);
						v= VectorMathod.getValue(wind.v,point);
						if( u !=9999 && v!=9999){
							point[0] -= u * stepLenght;
							point[1] -= v * stepLenght;
		
							value1 = VectorMathod.getValue(grid,point);
							if((value0<value_1 &&value1 > value0) ||value1<=0 ||value1 ==9999){
								break;
							}
							else{
								float step =(float) (Math.sqrt(u*u+v*v)*stepLenght);
								onePointLenght += step;
								onePointStrenght += step * value1;
								value_1=value0;
								value0 = value1;
							}
						
						}
						else{
							break;
						}
					}
					//往下游积分
					point[0] = startP[0]; point[1] =startP[1];
					value0=lineValue;value_1=lineValue;
					while(true){
						u= VectorMathod.getValue(wind.u,point);
						v= VectorMathod.getValue(wind.v,point);
						if( u !=9999 && v!=9999){
							point[0] += u * stepLenght;
							point[1] += v * stepLenght;
	
								value1 = VectorMathod.getValue(grid,point);
								if((value0<value_1 &&value1 > value0) ||value1<=0 ||value1 ==9999){
									break;
								}
								else{
									float step =(float) (Math.sqrt(u*u+v*v)*stepLenght);
									onePointLenght += step;
									onePointStrenght += step * value1;
									value_1=value0;
									value0 = value1;
									
								}
						}
						else{
							break;
						}
					}

				}
			}
			
		
		}
		*/
		public static GridData getCuttedRegion(GridData grid0){
			// 根据格点场分布，将大于0的区域按梯度上升法切割成各个极值点的覆盖分区
			
			GridData crId = new GridData(grid0.gridInfo);
			crId.setValue(0.0f);			//重置成0，后面将返回目标编号
			int nlon=crId.gridInfo.nlon;
			int nlat=crId.gridInfo.nlat;
			
			int nobj=0;
			GridData oriData= grid0.copy();   //用于保存原始数据，但网格重设
			oriData.gridInfo = new GridInfo(nlon,nlat,0.0f,0.0f,1.0f,1.0f);	
			VectorData grad = getGradsDirection(oriData);
			class GrowPoint{
				int i,j;
				public GrowPoint(int i0,int j0){
					i=i0;j=j0;
				}
			}
			Queue<GrowPoint> queue=new LinkedList<GrowPoint>(); 

			float u,v;
			int i1,j1,i2,j2;
			//先对所有极值点附近格点赋值
			for(int i=0;i<nlon;i++){
				for(int j=0;j<nlat;j++){
			        if(crId.dat[i][j]!=0||oriData.dat[i][j]<=0)continue;  
			        boolean isMax=true;
			        for(int p=-1;p<2;p++){
			        	for(int q=-1;q<2;q++){
			        		i1= MyMath.cycleIndex(false, nlon, i, p);
			        		j1= MyMath.cycleIndex(false, nlat, j, q);
			        		if(oriData.dat[i1][j1]>oriData.dat[i][j]){
			        			isMax = false;
			        			break;
			        		}
			        	}
			        	if(!isMax)break;
			        }
			        if(!isMax) continue;
			        
			        nobj++;
			        for(int p=-1;p<2;p++){
			        	for(int q=-1;q<2;q++){
			        		i1= MyMath.cycleIndex(false, nlon, i, p);
			        		j1= MyMath.cycleIndex(false, nlat, j, q);
			        		if(oriData.dat[i1][j1]>0&&crId.dat[i1][j1]==0){
			        			crId.dat[i1][j1]=nobj;   //将极值中心点附近9个点都赋为同一编号
			     		        queue.offer(new GrowPoint(i1,j1));
			        		}
			        	}
			        }
				}
			}
			GrowPoint gp;
			float[] point = new float[2];
			float speed;
			int ig,jg;
			int num=0;
		    while((gp=queue.poll())!=null){
		    	i1=gp.i;
		    	j1=gp.j;
		    	//把相邻点加入队列
		        for(int p=-1;p<2;p++){
		        	for(int q=-1;q<2;q++){
		        		if((p*p+q*q)!=1)continue;
		        		i2= MyMath.cycleIndex(false, nlon, i1, p);
		        		j2= MyMath.cycleIndex(false, nlat, j1, q);      		
		        		if(oriData.dat[i2][j2]>0&&crId.dat[i2][j2]==0){
		        			crId.dat[i2][j2] =-1;
		     		        queue.offer(new GrowPoint(i2,j2));
		        		}
		        	}
		        }
		    	//沿梯度上升至周围4个点都有相同标记停止
		        point[0] = i1;
		        point[1] = j1;
		        int step =0;
		        while(crId.dat[i1][j1]<=0){
		        	u= VectorMathod.getValue(grad.u,point);
					v= VectorMathod.getValue(grad.v,point);
					float random = (float) Math.random()*0.2f;
					if(u<0 && point[0]<1)u=-Math.abs(random*v);
					if(u>0 && point[0]>nlon-2)u=Math.abs(random*v);
					if(v<0 && point[1]<1)v=-Math.abs(random*u);
					if(v>0 && point[1]>nlat-2)v = Math.abs(random*u);
					if(u==0 && v == 0) u=1;
					speed = (float) Math.sqrt(u * u + v * v)+0.000001f;
					point[0] += u / speed;
					point[1] += v / speed;
					if(point[0]<=0)point[0]=0.001f;
					if(point[0]>=nlon-1) point[0] = nlon-1.001f;
					if(point[1]<=0)point[1]=0.001f;
					if(point[1]>=nlat-1) point[1] = nlat-1.001f;
					ig = (int)point[0];
					jg = (int)point[1];
					//将矩形框四个顶点中所有非0id找出来，如果他们相同则循环结束
					
					float sameId =Math.max(crId.dat[ig][jg], Math.max(crId.dat[ig+1][jg], Math.max(crId.dat[ig][jg+1], crId.dat[ig+1][jg+1])));		
					step++;
					if(step>nlon+nlat){
						crId.dat[i1][j1] = sameId;
						break;
					}
					if(crId.dat[ig][jg]>0 &&crId.dat[ig][jg]!=sameId) sameId =0;
					if(crId.dat[ig+1][jg]>0 &&crId.dat[ig+1][jg]!=sameId) sameId =0;
					if(crId.dat[ig][jg+1]>0 &&crId.dat[ig][jg+1]!=sameId) sameId =0;
					if(crId.dat[ig+1][jg+1]>0 &&crId.dat[ig+1][jg+1]!=sameId) sameId =0;
					
					crId.dat[i1][j1] = sameId;
							
				}
		    }
		    smoothIds(crId);
			return crId;
		}
		public static VectorData getGradsDirection(GridData grid){
			VectorData ve=new VectorData(grid.gridInfo);
			int nlat=grid.gridInfo.nlat;
			int nlon=grid.gridInfo.nlon;
			int j_1,j1,i_1,i1,dj,di;
			for(int j=0;j<nlat;j++){
				j_1=MyMath.cycleIndex(false, nlat, j, -1);
				j1=MyMath.cycleIndex(false, nlat, j, 1);
				dj=j1-j_1;
				for(int i=0;i<ve.gridInfo.nlon;i++){
					i_1=MyMath.cycleIndex(false, nlon, i, -1);
					i1=MyMath.cycleIndex(false, nlon, i, 1);
					di=i1-i_1;
					ve.u.dat[i][j]=(grid.dat[i1][j]-grid.dat[i_1][j])/di;
					ve.v.dat[i][j]=(grid.dat[i][j1]-grid.dat[i][j_1])/dj;
					float mod = (float) Math.pow(ve.u.dat[i][j]*ve.u.dat[i][j]+ve.v.dat[i][j]*ve.v.dat[i][j],0.5f)+0.000001f;
					ve.u.dat[i][j]/= mod;
					ve.v.dat[i][j]/= mod;
				}
			}
			return ve;
		}

		public static void smoothIds(GridData crId){
			int nlon=crId.gridInfo.nlon;
			int nlat=crId.gridInfo.nlat;
			int i1,j1;
			GridData smId = new GridData(crId.gridInfo);
		    for (int k=0;k<2;k++){
			    for(int i=0;i<nlon;i++){
					for(int j=0;j<nlat;j++){
						Map<Integer,Integer> idNum=new HashMap<Integer,Integer>();
						 for(int p=-4;p<5;p++){
					        	for(int q=-4;q<5;q++){
					        		i1= MyMath.cycleIndex(false, nlon, i, p);
					        		j1= MyMath.cycleIndex(false, nlat, j, q);
					        		int key =(int)crId.dat[i1][j1];
					        		if(idNum.containsKey(key)){
					        			idNum.put(key, idNum.get(key)+1);
					        		}
					        		else{
					        			idNum.put(key, 1);
					        		}
					        	}
					    }
						int maxNum = 1;
				
						for (Map.Entry<Integer,Integer> entry : idNum.entrySet()){
							if(entry.getValue()>maxNum){
								maxNum = entry.getValue();
								smId.dat[i][j] =entry.getKey();
							}
						}	 
					}
			    }
			    for(int i=0;i<nlon;i++){
					for(int j=0;j<nlat;j++){
						crId.dat[i][j] = smId.dat[i][j];
					}
			    }
		    }
		    return ;
		}

		
		public static void smoothIds(GridData crId,int window_size){
			
			int nlon=crId.gridInfo.nlon;
			int nlat=crId.gridInfo.nlat;
			int i1,j1;
			GridData smId = new GridData(crId.gridInfo);
		    for (int k=0;k<2;k++){
			    for(int i=0;i<nlon;i++){
					for(int j=0;j<nlat;j++){
						Map<Integer,Integer> idNum=new HashMap<Integer,Integer>();
						 for(int p=-window_size;p<window_size+1;p++){
					        	for(int q=-window_size;q<window_size+1;q++){
					        		i1= MyMath.cycleIndex(false, nlon, i, p);
					        		j1= MyMath.cycleIndex(false, nlat, j, q);
					        		int key =(int)crId.dat[i1][j1];
					        		if(idNum.containsKey(key)){
					        			idNum.put(key, idNum.get(key)+1);
					        		}
					        		else{
					        			idNum.put(key, 1);
					        		}
					        	}
					    }
						int maxNum = 1;
				
						for (Map.Entry<Integer,Integer> entry : idNum.entrySet()){
							if(entry.getValue()>maxNum){
								maxNum = entry.getValue();
								smId.dat[i][j] =entry.getKey();
							}
						}	 
					}
			    }
			    for(int i=0;i<nlon;i++){
					for(int j=0;j<nlat;j++){
						crId.dat[i][j] = smId.dat[i][j];
					}
			    }
		    }
		    return ;
		}
		
		public static ArrayList<Line> HighValueAreaRidge(VectorData tranWind, GridData gridFeature,int level){
			//根据平流风方法获取高值区的脊线
			VectorData tranDirection = VectorMathod.getDirection(tranWind);          //计算平流方向
			GridData adve=VectorMathod.getAdvection(tranDirection, gridFeature);    //计算平流场
			ArrayList<Line> line0 = LineDealing.creatLine(0.0f, adve);  //计算平流场0线，作为脊线或槽线
			VectorData adve_grad_direction = VectorMathod.getDirection(VectorMathod.getGrads(adve));
			GridData adveOfAdve= VectorMathod.dot(tranDirection, adve_grad_direction); //
			GridData marker=gridFeature.sign01().mutiply(adveOfAdve.add(0.5f).mutiply(-1).sign01());
			
			// 边界裁剪
			int n = 10;
			GridData markerR= new GridData(marker.gridInfo);
			for(int i= n;i<markerR.gridInfo.nlon-n;i++){
				for(int j=n;j<markerR.gridInfo.nlat-n;j++){
					markerR.dat[i][j]=1;
				}
			}
			marker = marker.mutiply(markerR);
			
			GridData geoHeight0 = new GridData("height_oy.txt");
			GridData geoHeight = new GridData(gridFeature.gridInfo);
			geoHeight.linearIntepolatedFrom(geoHeight0);
			geoHeight.setDefaultValue(0);
			geoHeight.smooth(10);
			float levelHeight  = MyMath.getMeanHeight(level);
			marker = marker.mutiply(geoHeight.mutiply(-1).add(levelHeight).sign01());
			ArrayList<Line> lines= LineDealing.cutLines(line0, marker); 
			return lines;
		}
		
		public static Map<Integer, SystemFeature> getCentreAreaStrenght(GridData grid, GridData ids) {
			// TODO Auto-generated method stub
			//给定分区和特征值，返回每个系统的中心和面积、积分强度等属性
			Map<Integer,SystemFeature> idPros = new HashMap<Integer,SystemFeature>();
			int id;
			int nlon = ids.gridInfo.nlon;
			int nlat = ids.gridInfo.nlat;
			float cellArea = grid.gridInfo.dlat*grid.gridInfo.dlon;
			float sr,lon,lat;
			SystemFeature idPro =null;
			for(int j=0;j<nlat;j++){
				lat = grid.gridInfo.startlat + j * grid.gridInfo.dlat;
				sr = (float) Math.cos(lat*3.14/180);
				for(int i=0;i<nlon;i++){
					id = (int) ids.dat[i][j];
					lon = grid.gridInfo.startlon + i * grid.gridInfo.dlon;
					if(id!=0){			
						if(idPros.containsKey(id)){
							idPro = idPros.get(id);
						}
						else{
							if(id>0){
								idPro=new SystemFeature(new Point(0,0,-999999));
							}
							else{
								idPro=new SystemFeature(new Point(0,0,999999));
							}
							idPros.put(id, idPro);
						}
						if(id*(grid.dat[i][j]-idPro.centrePoint.ptVal)>0){
							idPro.setCentrePoint(new Point(lon,lat,grid.dat[i][j]));	
						}
						idPro.addValueForFeature("area", cellArea * sr);
						idPro.addValueForFeature("strenght", cellArea * sr*grid.dat[i][j]);
					}
				}
			}	
			return idPros;
		}
		
		
		public static void resetId(GridData ids,int oldId,int newId,int startI,int startJ){
			//将格点场ids中的某个分区编号重设
			int nlon = ids.gridInfo.nlon;
			int nlat = ids.gridInfo.nlat;
			class GrowPoint{
				int i,j;
				public GrowPoint(int i0,int j0){
					i=i0;j=j0;
				}
			}
			Queue<GrowPoint> queue=new LinkedList<GrowPoint>();
			ids.dat[startI][startJ]=newId;
			queue.offer(new GrowPoint(startI,startJ));
			GrowPoint gp;
			int i1,j1;
	        while((gp=queue.poll())!=null){
	        	//从队列中取出第一个，如果它不为空则判断周围26个点

	        	for(int p=-3;p<4;p++){
	        		i1=MyMath.cycleIndex(false, nlon,gp.i,p);
	        		for(int q=-3;q<4;q++){
	        			j1=MyMath.cycleIndex(false, nlat,gp.j,q);
	        			if(ids.dat[i1][j1]==oldId){
	        				//如果周围26个点中有未被修改的点，则改正过来，并将周围的添加到队列中
	        				ids.dat[i1][j1]=newId;
	        				queue.offer(new GrowPoint(i1,j1));
	        			}
	        		}
	        	}
	        	
	        }
		}
		
		
		public static GridData combineStrongConnectingRegion_2d(GridData ids,float connectingRate){			
			//如果两个相邻的系统边界线很长，而面积不大，则它们应该属于同一个系统
			GridData grid =ids.copy();
			//逐个合并链接度超过阈值的目标
			int nlon = ids.gridInfo.nlon;
			int nlat = ids.gridInfo.nlat;
		
			Map<Integer,Map<Integer,Float>> map = getRegionAreaAndConnectingId_2d(grid,ids);
			while(true){
				//获取每个目标的面积，以及它和其它目标的边界线长度
				
				float maxRate = 0;
				int id0=0,id1=0;
				for (Map.Entry<Integer,Map<Integer,Float>> entry : map.entrySet()){
					Map<Integer,Float> map1 = entry.getValue();
					float area = map1.get(0);
					for (Map.Entry<Integer,Float> entry1 : map1.entrySet()){
						if(entry1.getKey()!=0 && map.containsKey(entry1.getKey())){
							float lenght = entry1.getValue();
							float rate = lenght * lenght / area;   //采用边界线长度的平方/面积 作为两个系统紧邻的程度
							if(maxRate < rate) {
								id0 = entry.getKey();
								id1 = entry1.getKey();
								maxRate = rate;
							}
						}
					}
				}
				if(maxRate < connectingRate){
					break;
				}
				else{
					//合并目标id
					for(int i=0;i<nlon;i++){
						for(int j=0;j<nlat;j++){
							if(ids.dat[i][j]==id1) ids.dat[i][j]=id0;
						}
					}
					//合并map中的元素
					Map<Integer,Float> map0 = map.get(id0);
					Map<Integer,Float> map1 = map.get(id1);
					map0.put(0, map0.get(0)+map1.get(0));
					map0.put(-1,Math.min(map0.get(-1), map1.get(-1)));
					for (Map.Entry<Integer,Float> entry1 : map1.entrySet()){
						int key = entry1.getKey();
						if(key!=0 && key!= id0){
							if(map0.containsKey(key)){
								map0.put(key,map0.get(key)+entry1.getValue());
							}
							else{
								map0.put(key,entry1.getValue());
							}
							if(map.containsKey(key)){
								Map<Integer,Float> map2 = map.get(key);
								if(map2.containsKey(id0)){
									map2.put(id0,map0.get(key)+entry1.getValue());
								}
								else{
									map2.put(id0,entry1.getValue());
								}
								map.put(key, map2);
							}
						}
				
					}
					map.put(id0, map0);
					map.remove(id1);
				}
			}
			return ids;
		}
		
		public static Map<Integer,Map<Integer,Float>> getRegionAreaAndConnectingId_2d(GridData grid0,GridData ids){
			
			// 计算一个高值区的面积以及和其它分区的边界线长度
			Map<Integer,Map<Integer,Float>> map_area = new HashMap<Integer,Map<Integer,Float>>();
			int id,i1,j1,id1;
			int nlon = ids.gridInfo.nlon;
			int nlat = ids.gridInfo.nlat;
		
			for(int i=0;i<nlon;i++){
				for(int j=0;j<nlat;j++){
					id = (int) ids.dat[i][j];
					if(id!=0){
						Map<Integer,Float> map_line =new HashMap<Integer,Float>();
						if(map_area.containsKey(id)){
						    map_line = map_area.get(id);
						}
						float area = 0;
						if(map_line.containsKey(0))area = map_line.get(0);
						map_line.put(0,area+1.0f);
						
						if(map_line.containsKey(-1)){
							map_line.put(-1, Math.max(grid0.dat[i][j],map_line.get(-1)));
							map_line.put(-3, (float)i);
							map_line.put(-2, (float)j);
						}
						else{
							map_line.put(-1, grid0.dat[i][j]);
							map_line.put(-3, (float)i);
							map_line.put(-2, (float)j);
						}
						
						
						//map_line.put(0,area+grid0.dat[i][j]);
						Map<Integer,Integer> map_point=new HashMap<Integer,Integer>();
					    for(int p=-1;p<2;p++){
				        	for(int q=-1;q<2;q++){
				        		if(Math.abs(p)+Math.abs(q)!=1) continue;
				        		i1= MyMath.cycleIndex(false, nlon, i, p);
				        		j1= MyMath.cycleIndex(false, nlat, j, q);
				        		id1 = (int) ids.dat[i1][j1];
				        		if(id1 !=0 && id1 != id){
				        			if(map_point.containsKey(id1)){
				        				map_point.put(id1, map_point.get(id1)+1);
				        			}
				        			else{
				        				map_point.put(id1,1);
				        			}
				        		}
				        	}
				        }
					    for (Map.Entry<Integer,Integer> entry : map_point.entrySet()){
					    	id1 = entry.getKey();
					    	float lenght =0;
					    	if(entry.getValue() == 1){
					    		lenght = 1.0f; //*grid0.dat[i][j];
					    	}
					    	else if(entry.getValue() == 2){
					    		lenght =1.414f; //*grid0.dat[i][j];
					    	}
					    	else {
					    		lenght = 2.0f; //*grid0.dat[i][j];
					    	}
					    	if(map_line.containsKey(id1)){
					    		map_line.put(id1, map_line.get(id1)+lenght);
					    	}
					    	else{
					    		map_line.put(id1,lenght);
					    	}
					    }
					    
					    map_area.put(id, map_line);
					}
				}
			}
			return map_area;
		}
		
}
