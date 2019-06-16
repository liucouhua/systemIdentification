package recutil;
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
		//鏍规嵁杈撳叆鐨勬牸鐐瑰満缁樺埗鎵�鏈夊彇鍊间负ryz鐨勭瓑鍊肩嚎 杩斿洖鍦ㄥ垪琛ㄤ腑 
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
			a=0;  //瀵绘壘杈圭紭鐨勮捣濮嬬偣锛宎=1琛ㄧず鎵惧埌璧峰鐐�
		    if(fin_edge==0){
			    for(i=0;i<nlon;i++){
				    for(j=0;j<nlat;j++){
					    if(i>0&&i<nlon-1&&j>0&&j<nlat-1)continue;
					    for(p=-1;p<2;p++){
						    for(q=-1;q<2;q++){
		                        if(Math.abs(p)+Math.abs(q)!=1)continue;
		                        if(gp[p+1][q+1][i][j]==1)continue;   //gp 鐢ㄤ簬璁板綍涓�涓偣鏄惁琚亶鍘嗚繃
							    if((i+p==0||i+p==nlon-1||j+q==0||j+q==nlat-1)&&(i+p>=0&&i+p<nlon&&j+q>=0&&j+q<nlat)){
							        if(grid[i][j]>ryz&&grid[i+p][j+q]<ryz){
								        ps[0][0]=((dlon*i+slon)*(ryz-grid[i+p][j+q])+(dlon*(i+p)+slon)*(grid[i][j]-ryz))/(grid[i][j]-grid[i+p][j+q]);
								        ps[1][0]=((dlat*j+slat)*(ryz-grid[i+p][j+q])+(dlat*(j+q)+slat)*(grid[i][j]-ryz))/(grid[i][j]-grid[i+p][j+q]);
								 //       !灏嗭紙p锛宷锛夐『鏃堕拡鏃嬭浆寰楋紙px锛宷x锛�
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
				fin_edge=1; //!璇存槑杈圭紭鐨勮捣鐐瑰凡缁忚鎼滅储瀹屾瘯
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
	
			if(a==0)break; // !鎵�鏈夌殑鐐瑰凡缁忚閬嶅巻
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
				dik=dj*1;  //鍥寸粫澶у�奸『鏃堕拡鏃嬭浆
				djk=-di;   //鍥寸粫澶у�奸『鏃堕拡鏃嬭浆
				i10=i00+dik;
				j10=j00+djk;	
				i11=i00+di+dik;
				j11=j00+dj+djk;
				if(i10<0||i10>=nlon||j10<0||j10>=nlat)break;  //瀵诲埌杈圭晫浜�
				if(grid[i10][j10]<ryz){
					if(grid[i11][j11]<ryz){
						ps[0][1]=((dlon*i00+slon)*(ryz-grid[i10][j10])+(dlon*i10+slon)*(grid[i00][j00]-ryz))/(grid[i00][j00]-grid[i10][j10]);
						ps[1][1]=((dlat*j00+slat)*(ryz-grid[i10][j10])+(dlat*j10+slat)*(grid[i00][j00]-ryz))/(grid[i00][j00]-grid[i10][j10]);
						p=2;
						}
					else{
						//			                !闉嶅瀷鍦烘湁涓ょ璧版硶锛屽鏋滀綆鍊间负鏋佸�间腑蹇冨垯璧�2浣嶄繚鎸佷綆鍊艰繛鎴愮墖锛屽叾瀹冩儏鍐佃蛋4浣�
		                p=0;
		                //							! 浣庡�兼瀬鍊�
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
		        if(gp[px+1][qx+1][i00][j00]==1)break;  //     !瀵诲埌绾跨殑璧风偣浜�     
			
		        float[] onePoint1={ps[0][p-1],ps[1][p-1]};
				line.point.add(onePoint1);         
		        gp[px+1][qx+1][i00][j00]=1;
			}
			lines.add(line);
		}
		return lines;
	}
	
	public static void smoothLines(ArrayList<Line> lines,int smTimes){
		//涓烘瘡涓瓑绾х殑绾挎潯杩涜骞虫粦锛岃繖闇�瑕佺煡閬撶綉鏍肩殑淇℃伅锛屽洜涓洪渶瑕佸垽鏂嚎鏉＄殑璧峰鐐规槸鍚﹀湪缃戞牸杈圭紭锛屽鏋滃湪缃戞牸杈圭紭灏辨槸寮�鍙ｆ洸绾裤�傝�屽钩婊戞鏁扮瓑淇℃伅鍒欎繚鐣欏湪鏁扮粍grade涓� 銆�
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
		// 璇ユ柟娉曠敤浠ュ皢涓�鏉＄瓑鍊肩嚎鍒囧壊鎴愬ソ鍑犳潯锛屽湪鑷姩缁樺埗闇滃喕绾匡紝妲界嚎绛夊姛鑳芥椂鍙敤
		ArrayList<Line> splits=new ArrayList<Line>();
		for(int i=0;i<lines.size();i++){
			Line line=lines.get(i);
			int np=line.point.size();
			int j=0,jStart=0,j1;
			boolean inCut=false;
			if(line.iscycle){ //濡傛灉鏄渾鍦堬紝鍒欑涓�娈电嚎鏉＄殑璧风偣鍦ㄧ涓�涓潪cut鍖哄煙鍐呯殑鐐逛箣鍚�
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
			
			//涓�鏉＄嚎鍒嗗壊缁撴潫
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
		 // 璇ユ柟娉曠敤浠ュ睆钄戒竴鏉℃洸绾跨殑閮ㄥ垎鎻忕偣锛屽湪鑷姩缁樺埗闇滃喕绾匡紝妲界嚎绛夊姛鑳芥椂鍙敤
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
		 // 璇ユ柟娉曠敤浠ュ睆钄界瓑鍊肩嚎鐨勬煇涓弿鐐规槸鍚﹀湪cut鍖哄煙鑼冨洿鍐�
		int i=(int)((p[0]-cut.gridInfo.startlon)/cut.gridInfo.dlon);
		int j=(int)((p[1]-cut.gridInfo.startlat)/cut.gridInfo.dlat);
		int ig,jg;
		for(int m=-ndis;m<=ndis+1;m++){
			for(int n=-ndis;n<=ndis+1;n++){
				ig=i+m;
				jg=j+n;
				if(ig>=0&&jg>=0&&ig<cut.gridInfo.nlon-1&&jg<cut.gridInfo.nlat-1){ 
					if(cut.dat[ig][jg]>0&&cut.dat[ig+1][jg]>0&&cut.dat[ig][jg+1]>0&&cut.dat[ig+1][jg+1]>0){//鎵惧埌浜嗚捣濮嬬偣
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static float lineLenght(Line line){
		//璇ユ柟娉曡繑鍥炵瓑鍊肩嚎鐨勯暱搴�
		ArrayList point =line.point;
		float lenght=0;
		for(int j=0;j<point.size()-1;j++){
			float[]x=(float[]) point.get(j);
			float[]y=(float[]) point.get(j+1);
			lenght+=MyMath.dis(x[0], x[1],y[0], y[1]);
		}
		return lenght;
	}
	
	
	
}
