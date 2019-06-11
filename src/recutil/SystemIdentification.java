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
			
			// 鏍规嵁绾挎潯鐢熸垚鍨傜洿浜庣嚎鏉＄殑骞虫祦椋�
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
					//灏嗘瘡涓剨绾跨偣鐨勬柟鍚戞爣璁板埌闄勮繎鐨勬牸鐐逛笂
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
			// 姹傚钩鍧�,灏嗚剨绾块檮杩戠殑骞冲潎椋庨�熻祴鍊肩粰绌虹櫧鍖哄煙
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
		
			//涓轰娇寰楅潪marker鍖哄煙椋庡満鍜宮arker鍖哄煙涔嬮棿骞虫粦杩囨浮
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
			
			//棣栧厛鏍规嵁feature缁樺埗鍏跺钩娴�0绾匡紙鍏朵腑骞虫祦椋庣殑鍒濆璁剧疆涓鸿タ鍖楅锛夛紝鍦ㄤ娇鐢╩arker杩涜瑁佸壀
			ArrayList<Line> trough=new ArrayList<Line>();
			VectorData tranDirection=VectorMathod.getDirection(tranWind);
			GridData marker1=marker0.sign01();
			GridData adve=VectorMathod.getAdvection(tranDirection, feature);    //璁＄畻骞虫祦鍦�
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
		//	GridData adve1=VectorMathod.getAdvection(mixWind, feature);    //璁＄畻骞虫祦鍦�
		//	adve1.writeToFile("G:/data/systemIdentify/adve1.txt");
			ArrayList<Line> origainalTrough=LineDealing.creatLine(0.0f, adve);  //璁＄畻骞虫祦鍦�0绾匡紝浣滀负鑴婄嚎鎴栨Ы绾�
			VectorData roWind = VectorMathod.rotate(tranDirection,-15);
			GridData adveOfAdve= VectorMathod.dot(roWind, adve_grad_direction); //璁＄畻骞虫祦鐨勫钩娴侊紝鍦ㄦЫ绾夸綅缃钩娴佺殑骞虫祦涓烘锛岃剨绾夸綅缃钩娴佺殑骞虫祦涓鸿礋
			adveOfAdve.writeToFile("G:/data/systemIdentify/adveOfAdve.txt");
			adveOfAdve = adveOfAdve.add(0.6f);
			curOfAdve = curOfAdve.add(-0.4f);
			adveOfAdve.writeToFile("G:/data/systemIdentify/adveOfAdve.txt");
			GridData minalAofA=adveOfAdve.mutiply(-1).sign01();    //涓烘彁鍙栬剨绾块儴鍒嗭紝淇濈暀骞虫祦鐨勫钩娴佷负璐熺殑鍖哄煙
			GridData minalcofA=curOfAdve.mutiply(-1).sign01();
			minalAofA.writeToFile("G:/data/systemIdentify/maOfa.txt");
			minalcofA.writeToFile("G:/data/systemIdentify/maOfc.txt");
			GridData marker=marker1.mutiply(minalAofA).mutiply(minalcofA);					 //鐢熸垚marker鍦�
			marker.writeToFile("G:/data/systemIdentify/marker.txt");
			ArrayList<Line> cutedTrough=LineDealing.cutLines(origainalTrough, marker);  //瑁佸壀鑾峰緱鑴婄嚎
			LineDealing.writeToFile("G:/data/systemIdentify/cutedtroughs.txt", cutedTrough);
			trough = getLongTrough(scale,cutedTrough);
			LineDealing.writeToFile("G:/data/systemIdentify/troughs.txt", trough);
			return trough;
			
		}
		
	
		
		public static void getLineStrenght(ArrayList<Line> line, GridData grid, VectorData wind){
			ArrayList<float[]> strenght =new ArrayList<float[]>();
			// 灏嗛閫熻浆鎹㈡垚绛夌粡绾害缃戞牸鐭㈤噺
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
					//瀵规瘡涓偣娌跨潃椋庡悜瀵逛笂涓嬫父鐨刦eature杩涜绉垎锛岀Н鍒嗗仠姝㈢偣鍦╢eature鏋佸皬鐐瑰鎴杕arker=0澶勫仠姝�
					sr = (float) Math.cos(line.get(i).point.get(j)[1]*3.14/180);
					float dx =sr* (line.get(i).point.get(j+1)[0]-line.get(i).point.get(j)[0]);
					float dy = line.get(i).point.get(j+1)[1]-line.get(i).point.get(j)[1];
					
					float[] startP = line.get(i).point.get(j);
					float u0 = VectorMathod.getValue(wind.u,startP);
					float v0 = VectorMathod.getValue(wind.v,startP);
					float cross = Math.abs(-dx*v0 + dy*u0);
					//寰�涓婃父绉垎
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
					//寰�涓嬫父绉垎
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
		public static GridData getCuttedRegion(GridData grid0, int  sm_id_window_size){
			// 鏍规嵁鏍肩偣鍦哄垎甯冿紝灏嗗ぇ浜�0鐨勫尯鍩熸寜姊害涓婂崌娉曞垏鍓叉垚鍚勪釜鏋佸�肩偣鐨勮鐩栧垎鍖�
			
			GridData crId = new GridData(grid0.gridInfo);
			crId.setValue(0.0f);			//閲嶇疆鎴�0锛屽悗闈㈠皢杩斿洖鐩爣缂栧彿
			int nlon=crId.gridInfo.nlon;
			int nlat=crId.gridInfo.nlat;
			
			int nobj=0;
			GridData oriData= grid0.copy();   //鐢ㄤ簬淇濆瓨鍘熷鏁版嵁锛屼絾缃戞牸閲嶈
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
			//鍏堝鎵�鏈夋瀬鍊肩偣闄勮繎鏍肩偣璧嬪��
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
			        			crId.dat[i1][j1]=nobj;   //灏嗘瀬鍊间腑蹇冪偣闄勮繎9涓偣閮借祴涓哄悓涓�缂栧彿
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
		    	//鎶婄浉閭荤偣鍔犲叆闃熷垪
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
		    	//娌挎搴︿笂鍗囪嚦鍛ㄥ洿4涓偣閮芥湁鐩稿悓鏍囪鍋滄
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
					//灏嗙煩褰㈡鍥涗釜椤剁偣涓墍鏈夐潪0id鎵惧嚭鏉ワ紝濡傛灉浠栦滑鐩稿悓鍒欏惊鐜粨鏉�
					
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
					//System.out.println(sameId);
							
				}
		    }
		    smoothIds(crId,sm_id_window_size);
			return crId;
		}
		
		public static GridData getCuttedRegion(GridData grid0){
			// 鏍规嵁鏍肩偣鍦哄垎甯冿紝灏嗗ぇ浜�0鐨勫尯鍩熸寜姊害涓婂崌娉曞垏鍓叉垚鍚勪釜鏋佸�肩偣鐨勮鐩栧垎鍖�
			
			GridData crId = new GridData(grid0.gridInfo);
			crId.setValue(0.0f);			//閲嶇疆鎴�0锛屽悗闈㈠皢杩斿洖鐩爣缂栧彿
			int nlon=crId.gridInfo.nlon;
			int nlat=crId.gridInfo.nlat;
			
			int nobj=0;
			GridData oriData= grid0.copy();   //鐢ㄤ簬淇濆瓨鍘熷鏁版嵁锛屼絾缃戞牸閲嶈
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
			//鍏堝鎵�鏈夋瀬鍊肩偣闄勮繎鏍肩偣璧嬪��
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
			        			crId.dat[i1][j1]=nobj;   //灏嗘瀬鍊间腑蹇冪偣闄勮繎9涓偣閮借祴涓哄悓涓�缂栧彿
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
		    	//鎶婄浉閭荤偣鍔犲叆闃熷垪
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
		    	//娌挎搴︿笂鍗囪嚦鍛ㄥ洿4涓偣閮芥湁鐩稿悓鏍囪鍋滄
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
					//灏嗙煩褰㈡鍥涗釜椤剁偣涓墍鏈夐潪0id鎵惧嚭鏉ワ紝濡傛灉浠栦滑鐩稿悓鍒欏惊鐜粨鏉�
					
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
					//System.out.println(sameId);
							
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
			//鏍规嵁骞虫祦椋庢柟娉曡幏鍙栭珮鍊煎尯鐨勮剨绾�
			VectorData tranDirection = VectorMathod.getDirection(tranWind);          //璁＄畻骞虫祦鏂瑰悜
			GridData adve=VectorMathod.getAdvection(tranDirection, gridFeature);    //璁＄畻骞虫祦鍦�
			ArrayList<Line> line0 = LineDealing.creatLine(0.0f, adve);  //璁＄畻骞虫祦鍦�0绾匡紝浣滀负鑴婄嚎鎴栨Ы绾�
			VectorData adve_grad_direction = VectorMathod.getDirection(VectorMathod.getGrads(adve));
			GridData adveOfAdve= VectorMathod.dot(tranDirection, adve_grad_direction); //
			GridData marker=gridFeature.sign01().mutiply(adveOfAdve.add(0.5f).mutiply(-1).sign01());
			
			// 杈圭晫瑁佸壀
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
			//缁欏畾鍒嗗尯鍜岀壒寰佸�硷紝杩斿洖姣忎釜绯荤粺鐨勪腑蹇冨拰闈㈢Н銆佺Н鍒嗗己搴︾瓑灞炴��
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
			//灏嗘牸鐐瑰満ids涓殑鏌愪釜鍒嗗尯缂栧彿閲嶈
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
	        	//浠庨槦鍒椾腑鍙栧嚭绗竴涓紝濡傛灉瀹冧笉涓虹┖鍒欏垽鏂懆鍥�26涓偣

	        	for(int p=-3;p<4;p++){
	        		i1=MyMath.cycleIndex(false, nlon,gp.i,p);
	        		for(int q=-3;q<4;q++){
	        			j1=MyMath.cycleIndex(false, nlat,gp.j,q);
	        			if(ids.dat[i1][j1]==oldId){
	        				//濡傛灉鍛ㄥ洿26涓偣涓湁鏈淇敼鐨勭偣锛屽垯鏀规杩囨潵锛屽苟灏嗗懆鍥寸殑娣诲姞鍒伴槦鍒椾腑
	        				ids.dat[i1][j1]=newId;
	        				queue.offer(new GrowPoint(i1,j1));
	        			}
	        		}
	        	}
	        	
	        }
		}
		
		
		public static GridData combineStrongConnectingRegion_2d(GridData ids,float connectingRate){			
			//濡傛灉涓や釜鐩搁偦鐨勭郴缁熻竟鐣岀嚎寰堥暱锛岃�岄潰绉笉澶э紝鍒欏畠浠簲璇ュ睘浜庡悓涓�涓郴缁�
			GridData grid =ids.copy();
			//閫愪釜鍚堝苟閾炬帴搴﹁秴杩囬槇鍊肩殑鐩爣
			int nlon = ids.gridInfo.nlon;
			int nlat = ids.gridInfo.nlat;
		
			Map<Integer,Map<Integer,Float>> map = getRegionAreaAndConnectingId_2d(grid,ids);
			while(true){
				//鑾峰彇姣忎釜鐩爣鐨勯潰绉紝浠ュ強瀹冨拰鍏跺畠鐩爣鐨勮竟鐣岀嚎闀垮害
				
				float maxRate = 0;
				int id0=0,id1=0;
				for (Map.Entry<Integer,Map<Integer,Float>> entry : map.entrySet()){
					Map<Integer,Float> map1 = entry.getValue();
					float area = map1.get(0);
					for (Map.Entry<Integer,Float> entry1 : map1.entrySet()){
						if(entry1.getKey()!=0 && map.containsKey(entry1.getKey())){
							float lenght = entry1.getValue();
							float rate = lenght * lenght / area;   //閲囩敤杈圭晫绾块暱搴︾殑骞虫柟/闈㈢Н 浣滀负涓や釜绯荤粺绱ч偦鐨勭▼搴�
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
					//鍚堝苟鐩爣id
					for(int i=0;i<nlon;i++){
						for(int j=0;j<nlat;j++){
							if(ids.dat[i][j]==id1) ids.dat[i][j]=id0;
						}
					}
					//鍚堝苟map涓殑鍏冪礌
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
			
			// 璁＄畻涓�涓珮鍊煎尯鐨勯潰绉互鍙婂拰鍏跺畠鍒嗗尯鐨勮竟鐣岀嚎闀垮害
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
