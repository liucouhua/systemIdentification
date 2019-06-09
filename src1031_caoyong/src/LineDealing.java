import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class LineDealing {
	public static ArrayList<Line> creatLine(float ryz,GridData g){		
		//根据输入的格点场绘制所有取值为ryz的等值线 返回在列表中 
		GridData grid0=g.copy();
		int i=0,j = 0,k;		
		int nlon=grid0.gridInfo.nlon;
		int nlat=grid0.gridInfo.nlat;
		float dlon=grid0.gridInfo.dlon;
		float dlat=grid0.gridInfo.dlat;
		float slon=grid0.gridInfo.startlon;
		float slat=grid0.gridInfo.startlat;
		float[][] grid=new float[nlon][nlat];
		int kt=1;
		int [][][][]gp=new int[3][3][nlon][nlat];
		int fin_edge=0;
		int fin_j = nlat-1,fin_i=1;
		int a=1;
		float [][]ps=new float[2][4];
		int i00=0,i01=0,i10=0,i11=0,j00=0,j01=0,j10=0,j11=0;
		int di,dj,dik,djk,p,q,px,qx;
		Line line = null;
		ArrayList<Line> lines = new ArrayList<Line>();
		for(i=0;i<nlon;i++){
			for(j=0;j<nlat;j++){
				if(grid0.dat[i][j]==ryz){
					grid[i][j]=ryz+1;
				}
				else{
					grid[i][j]=grid0.dat[i][j];
				}
			}
		}
		
		while(a!=0){
			a=0;  //寻找边缘的起始点，a=1表示找到起始点
		    if(fin_edge==0){
			    for(i=0;i<nlon;i++){
				    for(j=0;j<nlat;j++){
					    if(i>0&&i<nlon-1&&j>0&&j<nlat-1)continue;
					    for(p=-1;p<2;p++){
						    for(q=-1;q<2;q++){
		                        if(Math.abs(p)+Math.abs(q)!=1)continue;
		                        if(gp[p+1][q+1][i][j]==1)continue;   //gp 用于记录一个点是否被遍历过
							    if((i+p==0||i+p==nlon-1||j+q==0||j+q==nlat-1)&&(i+p>=0&&i+p<nlon&&j+q>=0&&j+q<nlat)){
							        if(grid[i][j]>ryz&&grid[i+p][j+q]<ryz){
								        ps[0][0]=((dlon*i+slon)*(ryz-grid[i+p][j+q])+(dlon*(i+p)+slon)*(grid[i][j]-ryz))/(grid[i][j]-grid[i+p][j+q]);
								        ps[1][0]=((dlat*j+slat)*(ryz-grid[i+p][j+q])+(dlat*(j+q)+slat)*(grid[i][j]-ryz))/(grid[i][j]-grid[i+p][j+q]);
								 //       !将（p，q）顺时针旋转得（px，qx）
								        px = q;
								        qx = -p;
								        if(i+px>=0&&i+px<nlon&&j+qx>=0&&j+qx<nlat){
									        a=2;
		                                    gp[p+1][q+1][i][j]=1;
									        i00=i;
									        j00=j;
									        i01=i+p;
									        j01=j+q;
									        break;
								        }
							        }
						    	}
						    }
						    if(a==2)break;
					    }
					    if(a==2)break;
				    }
				    if(a==2)break;
			    }
		    }
		    
			if(a==0){
				fin_edge=1; //!说明边缘的起点已经被搜索完毕
				  for(j=fin_j;j>1;j--){
					  for(i=1;i<nlon;i++){
						for(p=-1;p<2;p++){
							for(q=-1;q<2;q++){
		                        if(gp[p+1][q+1][i][j]==1)continue;
								if(i+p>=0&&i+p<nlon&&j+q>=0&&j+q<nlat){
								    if(grid[i][j]>ryz&&grid[i+p][j+q]<ryz&&p*q==0){
								        ps[0][0]=((dlon*i+slon)*(ryz-grid[i+p][j+q])+(dlon*(i+p)+slon)*(grid[i][j]-ryz))/(grid[i][j]-grid[i+p][j+q]);
								        ps[1][0]=((dlat*j+slat)*(ryz-grid[i+p][j+q])+(dlat*(j+q)+slat)*(grid[i][j]-ryz))/(grid[i][j]-grid[i+p][j+q]);
									    a=1;
		                                gp[p+1][q+1][i][j]=1;
									    i00=i;
									    j00=j;
									    i01=i+p;
									    j01=j+q;
		                                fin_j=j;
									    break;
									}
								}
							}
		                    if(a==1)break;
						}
		                if(a==1)break;
					}
				if(a==1)break;
				}
			}
	
			if(a==0)break; // !所有的点已经被遍历
			line=new Line();
			line.value=ryz;
	        float[]onePoint={ps[0][0],ps[1][0]};

			line.point.add(onePoint);         
		    if(a==1){
		    	line.iscycle=true;
		    }
		    else{
		    	line.iscycle=false;
		    }
			
			while(true){
				di=i01-i00;
				dj=j01-j00;
				dik=dj*1;  //围绕大值顺时针旋转
				djk=-di;   //围绕大值顺时针旋转
				i10=i00+dik;
				j10=j00+djk;	
				i11=i00+di+dik;
				j11=j00+dj+djk;
				if(i10<0||i10>=nlon||j10<0||j10>=nlat)break;  //寻到边界了
				if(grid[i10][j10]<ryz){
					if(grid[i11][j11]<ryz){
						ps[0][1]=((dlon*i00+slon)*(ryz-grid[i10][j10])+(dlon*i10+slon)*(grid[i00][j00]-ryz))/(grid[i00][j00]-grid[i10][j10]);
						ps[1][1]=((dlat*j00+slat)*(ryz-grid[i10][j10])+(dlat*j10+slat)*(grid[i00][j00]-ryz))/(grid[i00][j00]-grid[i10][j10]);
						p=2;
						}
					else{
						//			                !鞍型场有两种走法，如果低值为极值中心则走2位保持低值连成片，其它情况走4位
		                p=0;
		                //							! 低值极值
		                if(i10+1>=0&&i10+1<nlon&&j10+1>=0&&j10+1<nlat&&i10-1>=0&&i10-1<nlon&&j10-1>=0&&j10-1<nlat){
		                    if(grid[i10][j10]<grid[i10+1][j10]&&grid[i10][j10]<grid[i10-1][j10]&&grid[i10][j10]<grid[i10][j10+1]&&grid[i10][j10]<grid[i10][j10-1]){
		                        p=2;
		                    }
		                }
		                if(i01+1>=0&&i01+1<nlon&&j01+1>=0&&j01+1<nlat&&i01-1>=0&&i01-1<nlon&&j01-1>=0&&j01-1<nlat){
		                    if(grid[i01][j01]<grid[i01+1][j01]&&grid[i01][j01]<grid[i01-1][j01]&&grid[i01][j01]<grid[i01][j01+1]&&grid[i01][j01]<grid[i01][j01-1]){
		                        p=2;
		                	}
		                }

		                if(p==2){
		                    ps[0][1]=((dlon*i00+slon)*(ryz-grid[i10][j10])+(dlon*i10+slon)*(grid[i00][j00]-ryz))/(grid[i00][j00]-grid[i10][j10]);
						    ps[1][1]=((dlat*j00+slat)*(ryz-grid[i10][j10])+(dlat*j10+slat)*(grid[i00][j00]-ryz))/(grid[i00][j00]-grid[i10][j10]);
		                    p=2;
		                    }
		                else{
						    ps[0][3]=((dlon*i11+slon)*(ryz-grid[i01][j01])+(dlon*i01+slon)*(grid[i11][j11]-ryz))/(grid[i11][j11]-grid[i01][j01]);
						    ps[1][3]=((dlat*j11+slat)*(ryz-grid[i01][j01])+(dlat*j01+slat)*(grid[i11][j11]-ryz))/(grid[i11][j11]-grid[i01][j01]);
						    p=4;	
		                }
					}
				}
				else{
					if(grid[i11][j11]>ryz){
						ps[0][3]=((dlon*i11+slon)*(ryz-grid[i01][j01])+(dlon*i01+slon)*(grid[i11][j11]-ryz))/(grid[i11][j11]-grid[i01][j01]);
						ps[1][3]=((dlat*j11+slat)*(ryz-grid[i01][j01])+(dlat*j01+slat)*(grid[i11][j11]-ryz))/(grid[i11][j11]-grid[i01][j01]);
						p=4;
					}
					else{
						ps[0][2]=((dlon*i10+slon)*(ryz-grid[i11][j11])+(dlon*i11+slon)*(grid[i10][j10]-ryz))/(grid[i10][j10]-grid[i11][j11]);
						ps[1][2]=((dlat*j10+slat)*(ryz-grid[i11][j11])+(dlat*j11+slat)*(grid[i10][j10]-ryz))/(grid[i10][j10]-grid[i11][j11]);
						p=3;
					}
				}
	
				if(p==2){
					i01=i10;
					j01=j10;
					}
				else if(p==3){
					i00=i10;
					j00=j10;
					i01=i11;
					j01=j11;
					}
				else{
					i00=i11;
					j00=j11;
					}
					
		        px=i01-i00;
		        qx=j01-j00;
		        if(gp[px+1][qx+1][i00][j00]==1)break;  //     !寻到线的起点了     
			
		        float[] onePoint1={ps[0][p-1],ps[1][p-1]};
				line.point.add(onePoint1);         
		        gp[px+1][qx+1][i00][j00]=1;
			}
			lines.add(line);
		}
		return lines;
	}
	
	public static void smoothLines(ArrayList<Line> lines,int smTimes){
		//为每个等级的线条进行平滑，这需要知道网格的信息，因为需要判断线条的起始点是否在网格边缘，如果在网格边缘就是开口曲线。而平滑次数等信息则保留在数组grade中 。
		ArrayList<Line> smLines1=new ArrayList<Line> ();
		ArrayList<Line> smLines0=new ArrayList<Line> ();
		for (int t=0;t<smTimes;t++){
			int lineNum=lines.size();
			for(int j=0;j<lineNum;j++){
				int pointNum=lines.get(j).point.size();
				for(int i=1;i<pointNum-1;i++){
					lines.get(j).point.get(i)[0]=(lines.get(j).point.get(i-1)[0]+lines.get(j).point.get(i+1)[0])*0.25f+lines.get(j).point.get(i)[0]*0.5f;
					lines.get(j).point.get(i)[1]=(lines.get(j).point.get(i-1)[1]+lines.get(j).point.get(i+1)[1])*0.25f+lines.get(j).point.get(i)[1]*0.5f;
				}
			}
		}
	}

	
	public static ArrayList<Line> cutLines(ArrayList<Line> lines, GridData cut) {
		// 该方法用以将一条等值线切割成好几条，在自动绘制霜冻线，槽线等功能时可用
		ArrayList<Line> splits=new ArrayList<Line>();
		for(int i=0;i<lines.size();i++){
			Line line=lines.get(i);
			int np=line.point.size();
			int j=0,jStart=0,j1;
			boolean inCut=false;
			if(line.iscycle){ //如果是圆圈，则第一段线条的起点在第一个非cut区域内的点之后
				while(j<np){
					inCut=isPointInCut(0,line.point.get(j),cut);
					if(inCut)j++;
					if(!inCut){
						jStart=j;
						break;
					}
				}
			}
			j1=0;		
			while(j1<np){
				while(j1<np){
					j=MyMath.cycleIndex(line.iscycle, np,jStart,j1);
					inCut=isPointInCut(0,line.point.get(j),cut);
					if(inCut)break;
					j1++;
				}
				if(j1>=np)break;		
				Line split=new Line();
				splits.add(split);
				split.value=line.value;
	//			//System.out.println(i);
				while(j1<np){
					j=MyMath.cycleIndex(line.iscycle, np,jStart,j1);
					inCut=isPointInCut(0,line.point.get(j),cut);
					if(inCut)split.point.add(line.point.get(j));
					j1++;
					if(!inCut)break;
				}
				
			}
			
			//一条线分割结束
		}

		
		ArrayList<Line> splits1=new ArrayList<Line>();
		for(int k=0;k<splits.size();k++){
			boolean noPointinCut=true;
			boolean inCut;
			Line line=splits.get(k);
			for(int p=0;p<line.point.size();p++){
				inCut=isPointInCut(0,line.point.get(p),cut);
				if(inCut)noPointinCut=false;
			}
			if(!noPointinCut){
				splits1.add(line);
			}
		}
		
		return splits1;
	}

	public static ArrayList<Line> maskLines(ArrayList<Line> lines, GridData cut) {
		 // 该方法用以屏蔽一条曲线的部分描点，在自动绘制霜冻线，槽线等功能时可用
		ArrayList<Line> masks=new ArrayList<Line>();
		GridData marker=new GridData(cut.gridInfo);
		for(int i=0;i<marker.gridInfo.nlon;i++){
			for(int j=0;j<marker.gridInfo.nlat;j++){
				marker.dat[i][j]=cut.dat[i][j]-0.999999f;
			}
		}	
		for(int i=0;i<lines.size();i++){
			Line line=lines.get(i);
			int np=line.point.size();
			boolean inMarker=false;
			for(int j=0;j<np;j++){
				inMarker=isPointInCut(0,line.point.get(j),marker);
				if(inMarker){
					masks.add(line);
					break;
				}
			}
			
		}
		return masks;
	}
	
	public static boolean isPointInCut(int ndis,float[] p, GridData cut){
		 // 该方法用以屏蔽等值线的某个描点是否在cut区域范围内
		int i=(int)((p[0]-cut.gridInfo.startlon)/cut.gridInfo.dlon);
		int j=(int)((p[1]-cut.gridInfo.startlat)/cut.gridInfo.dlat);
		int ig,jg;
		for(int m=-ndis;m<=ndis+1;m++){
			for(int n=-ndis;n<=ndis+1;n++){
				ig=i+m;
				jg=j+n;
				if(ig>=0&&jg>=0&&ig<cut.gridInfo.nlon-1&&jg<cut.gridInfo.nlat-1){ 
					if(cut.dat[ig][jg]>0&&cut.dat[ig+1][jg]>0&&cut.dat[ig][jg+1]>0&&cut.dat[ig+1][jg+1]>0){//找到了起始点
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static float lineLenght(Line line){
		//该方法返回等值线的长度
		ArrayList point =line.point;
		float lenght=0;
		for(int j=0;j<point.size()-1;j++){
			float[]x=(float[]) point.get(j);
			float[]y=(float[]) point.get(j+1);
			lenght+=MyMath.dis(x[0], x[1],y[0], y[1]);
		}
		return lenght;
	}

	public static void writeToFile(String fileName,ArrayList<Line> trough) {
		DecimalFormat datafmt = new DecimalFormat("0.000");
		DecimalFormat datafmt1 = new DecimalFormat("0.0");
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(new File(fileName)),"GBK");
			BufferedWriter br=new BufferedWriter(fos);
			String str="diamond 14 "+fileName;
			br.write(str);
			str="\n2017 08 12 16 0\n";
			br.write(str);
			//输出lines
			br.write("LINES: 0\n");
			
			
			//输出trough
			int nline = trough.size();
			br.write("LINES_SYMBOL: "+nline+"\n");
			for(int i=0;i<trough.size();i++){
				br.write("0 4 "+trough.get(i).point.size());
				for(int j=0;j<trough.get(i).point.size();j++){
					if(j%4==0)br.write("\n");
					float [] p1=(float[]) trough.get(i).point.get(j);
					br.write("   "+datafmt.format(p1[0]));
					br.write("   "+datafmt.format(p1[1]));
					if(j==0){
						br.write("         1");
					}
					else{
						br.write("     0.000");
					}
				}
				br.write("\nNoLabel 0\n");
			}


			//输出symbol
		//	br.write("SYMBOLS: "+0+"\n");
			
			br.write("SYMBOLS: "+trough.size()*1+"\n");
			for(int i=0;i<trough.size();i++){
				br.write("  48  ");
				br.write("   "+datafmt.format(trough.get(i).point.get(0)[0]));
				br.write("   "+datafmt.format(trough.get(i).point.get(0)[1]));
				br.write("  1");
				br.write("   " +datafmt1.format(trough.get(i).lenght)+"\n");
			}
			
			br.write("CLOSED_CONTOURS: 0\n");
			br.write("STATION_SITUATION\n");
			br.write("WEATHER_REGION:  0\n"); 
			br.write("FILLAREA:  0\n");
			br.write("NOTES_SYMBOL: "+0+"\n");
			
			br.write("NOTES_SYMBOL: "+trough.size()*1+"\n");
			for(int i=0;i<trough.size();i++){
				br.write("48  ");
				br.write("   "+datafmt.format(trough.get(i).point.get(0)[0]));
				br.write("   "+datafmt.format(trough.get(i).point.get(0)[1]));
				br.write("  0 5");
				br.write("   " +datafmt1.format(trough.get(i).lenght)+" 0 10 simhei.ttf 16 1 255 255 0 0\n");
			}	
			
			br.write("WithProp_LINESYMBOLS: "+trough.size()+"\n");
			for(int i=0;i<trough.size();i++){
				br.write("0 4 255 255 255 0 0 0\n"+trough.get(i).point.size());
				for(int j=0;j<trough.get(i).point.size();j++){
					if(j%4==0)br.write("\n");
					float [] p1=(float[]) trough.get(i).point.get(j);
					br.write("   "+datafmt.format(p1[0]));
					br.write("   "+datafmt.format(p1[1]));
					br.write("     0.000");
				}
				br.write("\nNoLabel 0\n");
			}
			br.flush();
			fos.close();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			System.out.println(fileName+"写入失败");
			
		}
	}
	
	
	
}
