package recutil;

import java.util.ArrayList;
import java.util.Random;

/**
 * 娑�
 */
public class SVortex{

	public static WeatherSystems getVortexCentres1(VectorData wind, int level, float scale) {
		// TODO Auto-generated method stub
		
		//
		//

		//get_cents_by_flow(wind,scale);
		
		//wind.u.smooth(10); wind.v.smooth(10);
		GridData marker = getMarker(wind.u,wind.v,scale);  // 璁＄畻娑℃棆鍖猴紙marker鍙栧�间负1鐨勯儴鍒嗭級
		GridData cycle = GetCycleStrenth(wind.u,wind.v,scale);  //绉垎寰楃幆娴佸己搴�
		cycle.writeToFile("D:\\develop\\java\\201905-weahter_identification\\output/cycle.txt");
		
		cycle = cycle.mutiply(marker);                            // 淇濈暀娑℃棆鍖虹殑鐜祦寮哄害
		marker.writeToFile("D:\\develop\\java\\201905-weahter_identification\\output/marker.txt");
		GridData ids = SystemIdentification.getCuttedRegion(cycle);  //瀹氫箟娑℃棆绯荤粺锛岃缃浉搴斿彉閲忥紝骞堕�氳繃reset鍑芥暟璁＄畻娑℃棆鐨勪腑蹇冧綅缃拰鐩稿叧灞炴��
		WeatherSystems vc = new WeatherSystems("vortex",level);
		vc.setValue(cycle);
		vc.setIds(ids);
		vc.reset();
		return vc;
	}
	  public static GridData GetCycleStrenth(GridData gdUwnd, GridData gdVwnd, double minScale)
      {
          //鏍囧織杈撳嚭鍦�
          GridData output = gdUwnd.copy();
          output.setValue(0.0f);
          //鏈�灏忔丁鏃嬪昂搴�
          int xn = (int)(minScale/gdUwnd.gridInfo.dlon);
          int yn = (int)(minScale/gdUwnd.gridInfo.dlat);
          //閬嶅巻姣忎釜鐐瑰惊鐜�
          for (int j = 0; j < gdUwnd.gridInfo.nlat; j++)
          {
              for (int i = 0; i < gdUwnd.gridInfo.nlon; i++)
              {
                  //鏄惁鏋勬垚娑℃棆鐨勬爣蹇�
                  double flag = 0;
                  //涓嬭竟鐣岀Н鍒�
                  for (int ii = i - xn; ii <= i + xn; ii++)
                  {
                      int jj = j - yn;
                      if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                      {
                          flag = flag + gdUwnd.dat[ii][ jj];
                      }
                  }
                  //鍙宠竟鐣岀Н鍒�
                  for (int jj = j - yn; jj <= j + yn; jj++)
                  {
                      int ii = i + xn;
                      if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                      {
                          flag = flag + gdVwnd.dat[ii][ jj];
                      }
                  }
                  //涓婅竟鐣岀Н鍒�
                  for (int ii = i + xn; ii >= i - xn; ii--)
                  {
                      int jj = j + yn;
                      if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                      {
                          flag = flag + (-gdUwnd.dat[ii][ jj]);
                      }
                  }
                  //宸﹁竟鐣岀Н鍒�
                  for (int jj = j + yn; jj >= j - yn; jj--)
                  {
                      int ii = i - xn;
                      if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                      {
                          flag = flag + (-gdVwnd.dat[ii][ jj]);
                      }
                  }

                  output.dat[i][ j] =(float)( flag);
              }
          }
          return output;
      }

	
    public static GridData getMarker(GridData gdUwnd, GridData gdVwnd, double minScale)
    {
        //鏍囧織杈撳嚭鍦�
        GridData output = gdUwnd.copy();
        output.setValue(0);
        //鏈�灏忔丁鏃嬪昂搴�
        int xn = (int)(minScale/gdUwnd.gridInfo.dlon);
        int yn = (int)(minScale / gdUwnd.gridInfo.dlat);
        //閬嶅巻姣忎釜鐐瑰惊鐜�
        for (int j = 0; j < gdUwnd.gridInfo.nlat; j++)
        {
            for (int i = 0; i < gdUwnd.gridInfo.nlon; i++)
            {
                //鏄惁鏋勬垚娑℃棆鐨勬爣蹇�
                int flag = 0;
                //涓嬭竟鐣岀Н鍒�
                for (int ii = i - xn; ii <= i + xn; ii++)
                {
                    int jj = j - yn;
                    if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                    {
                        if (gdUwnd.dat[ii][ jj] >= 0)
                        {
                            flag = flag + 0;
                        }
                        else
                        {
                            flag = flag + 1;
                        }
                    }
                    else
                    {
                        flag = flag + 1;
                    }
                }
                //鍙宠竟鐣岀Н鍒�
                for (int jj = j - yn; jj <= j + yn; jj++)
                {
                    int ii = i + xn;
                    if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                    {
                        if (gdVwnd.dat[ii][ jj] >= 0)
                        {
                            flag = flag + 0;
                        }
                        else
                        {
                            flag = flag + 1;
                        }
                    }
                    else
                    {
                        flag = flag + 1;
                    }
                }
                //涓婅竟鐣岀Н鍒�
                for (int ii = i + xn; ii >= i - xn; ii--)
                {
                    int jj = j + yn;
                    if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                    {
                        if (gdUwnd.dat[ii][ jj] <= 0)
                        {
                            flag = flag + 0;
                        }
                        else
                        {
                            flag = flag + 1;
                        }
                    }
                    else
                    {
                        flag = flag + 1;
                    }
                }
                //宸﹁竟鐣岀Н鍒�
                for (int jj = j + yn; jj >= j - yn; jj--)
                {
                    int ii = i - xn;
                    if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                    {
                        if (gdVwnd.dat[ii][ jj] <= 0)
                        {
                            flag = flag + 0;
                        }
                        else
                        {
                            flag = flag + 1;
                        }
                    }
                    else
                    {
                        flag = flag + 1;
                    }
                }
                //鐢辩嚎绉垎鍒ゆ柇鏄惁鏄丁鏃�
                if (flag == 0)
                {
                    output.dat[i][ j] = 1.0f;
                }
            }
        }
        return output;
    }
	
    
    public static WeatherSystems getVortexCentres(VectorData wind,int level,float scale) {
    	//GridData grid_index = new GridData(wind.gridInfo);
    	//Sta4Data sta_index0 = new Sta4Data(grid_index);
    	
    	
    	StaData sta_index = new StaData(wind.gridInfo.nlon*wind.gridInfo.nlat,9);
    	int m= 0;
		int nlon=wind.gridInfo.nlon,nlat=wind.gridInfo.nlat;
		float slon=wind.gridInfo.startlon,slat=wind.gridInfo.startlat;
		float dlon=wind.gridInfo.dlon,dlat=wind.gridInfo.dlat;
		float elon = wind.gridInfo.endlon,elat = wind.gridInfo.endlat;
		//灏嗙綉鏍兼暟鎹浆鍙樹负绔欑偣褰㈠紡
    	for(int i=0;i<wind.gridInfo.nlon;i++) {
    		for(int j=0;j<wind.gridInfo.nlat;j++) {
    			sta_index.dat[m][0]=i*1000+j;
    			sta_index.dat[m][1]=slon+i*dlon;
    			sta_index.dat[m][2]=slat+j*dlat;
    			sta_index.dat[m][3]=i;
    			sta_index.dat[m][4]=j;
				m++;
    		}
    	}
    	//sta_index.writeToFile("H:\\task\\link\\xiangji\\201905-weahter_identification\\output\\start.txt");
    	int flow_times = 0;
    	VectorData direction0 =VectorMathod.getDirection(wind);
    	VectorData direction = VectorMathod.rotate(direction0,15);
    	VectorData direction_30 = VectorMathod.rotate(direction0,30);
    	//direction.writeToFile("H:\\task\\link\\xiangji\\201905-weahter_identification\\output\\dir.txt", "2019010108");
    	
    	int ig,jg;
    	float rs = 0.3f;
    	float speed;
    	boolean need_flow = true;
		Sta4Data sta_u = new Sta4Data(sta_index.nsta);
		Sta4Data sta_v = new Sta4Data(sta_index.nsta);
    	
		//棣栧厛娌跨潃椋庡満骞虫祦
		StaData sta_index_in = null;
    	for(int s = 0;s<101;s++) {
    		sta_u = new Sta4Data(sta_index.nsta);
    		sta_v = new Sta4Data(sta_index.nsta);
    		for (int n= 0;n<sta_index.nsta;n++) {
    			sta_u.dat[n][1] = sta_index.dat[n][1];
    			sta_u.dat[n][2] = sta_index.dat[n][2];
    			sta_v.dat[n][1] = sta_index.dat[n][1];
    			sta_v.dat[n][2] = sta_index.dat[n][2];
    		}
	    	sta_u.getValueFromGrid(direction.u);
	    	sta_v.getValueFromGrid(direction.v);
	    	int nin = 0;
    		for (int n = 0;n < sta_index.nsta;n++) {
     			sta_index.dat[n][1] += sta_u.dat[n][3] * rs;
     			sta_index.dat[n][2] += sta_v.dat[n][3] * rs;
     			if(sta_index.dat[n][1] > slon && sta_index.dat[n][1] <elon &&sta_index.dat[n][2] > slat && sta_index.dat[n][2] <elat) {
     				nin ++ ;
     			}
    		}
    		if(s%30 == 0) {
    			//娓呴櫎鍑轰簡杈圭晫鐨勭偣
	    		sta_index_in = new StaData(nin,9);
	    		nin = 0;
	    		for (int n = 0;n < sta_index.nsta;n++) {
	     			if(sta_index.dat[n][1] > slon && sta_index.dat[n][1] <elon &&sta_index.dat[n][2] > slat && sta_index.dat[n][2] <elat) {
	     				for(int j=0;j<9;j++) {
	     					sta_index_in.dat[nin][j] = sta_index.dat[n][j];
	     				}
	     				nin ++ ;
	     			}
	    		}
	    		sta_index = sta_index_in.copy();
	    	}
    		
    	}
    	sta_u.writeToFile("D:/develop/java/201905-weahter_identification/output/sta_u.txt");

    	System.out.println("A");
    	//涓轰簡閬垮厤鐜舰鏃嬭浆锛屽鍔犲悜娑″害涓績鐨勫垎閲�
    	int fnum=0;
    	while(need_flow && fnum <1000) {
    		fnum ++;
    		sta_u = new Sta4Data(sta_index.nsta);
    		sta_v = new Sta4Data(sta_index.nsta);
    		for (int n= 0;n<sta_index.nsta;n++) {
    			sta_u.dat[n][1] = sta_index.dat[n][1];
    			sta_u.dat[n][2] = sta_index.dat[n][2];
    			sta_v.dat[n][1] = sta_index.dat[n][1];
    			sta_v.dat[n][2] = sta_index.dat[n][2];
    		}
	    	sta_u.getValueFromGrid(direction_30.u);
	    	sta_v.getValueFromGrid(direction_30.v); 	
	    	need_flow = false;
	    	speed = 0;
    		for (int n = 0;n < sta_index.nsta;n++) {
    			if(need_flow == false) {
    				speed = sta_u.dat[n][3] * sta_u.dat[n][3] + sta_v.dat[n][3] * sta_v.dat[n][3];
    			}
    			if (speed >1e-6) {
    				need_flow = true;
    			}
     			sta_index.dat[n][1] += sta_u.dat[n][3] * rs;
     			sta_index.dat[n][2] += sta_v.dat[n][3] * rs;
    		}	
    	}
    	sta_u.writeToFile("D:/develop/java/201905-weahter_identification/output/sta_u.txt");
    	System.out.println("b");
    	
    	
    	//瀵瑰皢sta_index缁堢偣浣嶇疆锛岃祴鍊煎埌绗�6,7鍒�
    	for(int n=0;n<sta_index.nsta;n++) {
    		sta_index.dat[n][6] = sta_index.dat[n][1];
    		sta_index.dat[n][7] = sta_index.dat[n][2];
    		sta_index.dat[n][1] = slon+sta_index.dat[n][3]*dlon;
    		sta_index.dat[n][2] = slat+sta_index.dat[n][4]*dlat;
    	}
    	
    	
		//閲嶆柊璧颁竴閬�,鍒ゆ柇鏈夋病鏈夊弽杞殑鎯呭喌
    	float u,v;
    	float dx,dy,dis;
    	float vox;
    	for(int s = 0;s<0;s++) {
    		sta_u = new Sta4Data(sta_index.nsta);
    		sta_v = new Sta4Data(sta_index.nsta);
    		for (int n= 0;n<sta_index.nsta;n++) {
    			sta_u.dat[n][1] = sta_index.dat[n][1];
    			sta_u.dat[n][2] = sta_index.dat[n][2];
    			sta_v.dat[n][1] = sta_index.dat[n][1];
    			sta_v.dat[n][2] = sta_index.dat[n][2];
    		}
	    	sta_u.getValueFromGrid(direction.u);
	    	sta_v.getValueFromGrid(direction.v);
	    	int npv=0;
    		for (int n = 0;n < sta_index.nsta;n++) {
    			u = sta_u.dat[n][3];
    			v = sta_v.dat[n][3];
     			sta_index.dat[n][1] += u * rs;
     			sta_index.dat[n][2] += v * rs;
     			dx = sta_index.dat[n][6] -sta_index.dat[n][1];
     			dy = sta_index.dat[n][7] -sta_index.dat[n][2];
     			
     			dis = (float) Math.sqrt(dx*dx +dy *dy);
     			if(dis > 0) {
	     			vox = (u * dy - v * dx)/dis;
	     			if(sta_index.dat[n][8] > vox) {
	     				sta_index.dat[n][8] = vox;
	     			}
     			}
     			if(sta_index.dat[n][8]>=-0.5) {
     				npv ++;
     			}
    		}	
    		
    		//if(s%1000 ==0) {
	    		sta_index_in = new StaData(npv,9);
	    		npv = 0;
	    		for (int n = 0;n < sta_index.nsta;n++) {
	     			if(sta_index.dat[n][8] >=-0.5) {
	     				for(int j=0;j<9;j++) {
	     					sta_index_in.dat[npv][j] = sta_index.dat[n][j];
	     				}
	     				npv ++ ;
	     			}
	    		}
	    		sta_index = sta_index_in.copy();
    		//}    	
    		
    	}
    	
    	
    	ArrayList<float[]> cents = new ArrayList<float[]>();
    	float x,y;
    	boolean had;
    	float dis2;
    	
    	//璁板綍娑℃棆涓績浣嶇疆鍜岀偣鏁�
    	for (int n = 0;n < sta_index.nsta;n++) {
    		x = sta_index.dat[n][6];
    		y = sta_index.dat[n][7];
    		if(x > wind.gridInfo.startlon+scale && x <wind.gridInfo.endlon-scale && y >wind.gridInfo.startlat+scale && y < wind.gridInfo.endlat-scale) {
    			had = false;
    			for (int k=0;k<cents.size();k++) {
    				float[] pk = cents.get(k);
    				dis2 = (x-pk[0])*(x-pk[0]) + (y-pk[1]) * (y-pk[1]);
    				if(dis2 < 0.1) {
    					had = true;
    					pk[2] ++;
    					break;
    				}
    			}
    			if(!had) {
    				float[] point = new float[3];
    				point[0] = x;
    				point[1] = y;
    				point[2] = 1;
    				cents.add(point);
    				//System.out.println(point.ptLon + " " + point.ptLat);
    			}
    		}
    	}
    	
    	
    	//灏嗙偣鏁板皯鐨勬丁鏃嬩腑蹇冭褰曚笅鏉ワ紝骞跺垹闄�
    	float min_num =0 ;//2 * ((scale/wind.gridInfo.dlat) * (scale/wind.gridInfo.dlon));
    	ArrayList<float[]> cents_small = new ArrayList<float[]>();
    	for(float[] c : cents) {
    		System.out.println(c[2]);
    		if(c[2] <= min_num){
    			cents_small.add(c);
    		}
    	}
    	System.out.println(cents.size());
    	cents.removeAll(cents_small);
    	System.out.println(cents.size());
    	
    	
    	//灏嗘丁鏃嬩腑蹇冪偣闆嗘敼鎴愮珯鐐瑰舰寮�
    	Sta4Data sta_cents = new Sta4Data(cents.size());
    	for(int k=0;k<cents.size();k++) {
    		sta_cents.dat[k][0] = k;
    		sta_cents.dat[k][1] = cents.get(k)[0];
    		sta_cents.dat[k][2] = cents.get(k)[1];
    	}
    	
    	ArrayList<Point> cent_points = new ArrayList<Point>();
    	
    	//鍒ゆ柇娑℃棆涓績鐨勭偣鏄惁澶勪簬姝ｆ丁搴﹀尯
    	GridData vor = VectorMathod.getVor(wind);
    	sta_cents.getValueFromGrid(vor);
    	
    	for(int k=0;k<sta_cents.nsta;k++) {
    		if(sta_cents.dat[k][3] >0) {		
    			Point point = new Point(sta_cents.dat[k][1],sta_cents.dat[k][2]);
    			cent_points.add(point);
    		}
    	}
    	
    	//淇濈暀姝ｆ丁搴﹀尯鐨勬丁鏃嬩腑蹇冧綅缃�
    	sta_cents = new Sta4Data(cent_points.size());
    	for(int k=0;k<cent_points.size();k++) {
    		sta_cents.dat[k][0] = k;
    		sta_cents.dat[k][1] = cent_points.get(k).ptLon;
    		sta_cents.dat[k][2] = cent_points.get(k).ptLat;
    	}
    	
    	
    	//System.out.println(flow_times);
    	sta_cents.writeToFile("D:\\develop\\java\\201905-weahter_identification\\output\\end.txt");
    	
    	
    	//鏍规嵁骞虫祦鍚巗ta_index鐨勪綅缃紝鍒ゆ柇鍏跺睘浜庡摢涓�涓丁鏃嬩腑蹇冿紝骞朵互姝や綔涓虹紪鍙�
    	for (int n = 0;n < sta_index.nsta;n++) {
    		x = sta_index.dat[n][6];
    		y = sta_index.dat[n][7];
    		if(x > wind.gridInfo.startlon && x <wind.gridInfo.endlon && y >wind.gridInfo.startlat && y < wind.gridInfo.endlat) {
    			for (int k=0;k<cent_points.size();k++) {
    				Point pk = cent_points.get(k);
    				dis2 = (x-pk.ptLon)*(x-pk.ptLon) + (y-pk.ptLat) * (y-pk.ptLat);
    				if(dis2 < 0.1) {
    					sta_index.dat[n][5] = k+1;
    					break;
    				}
    			}
    		}
    	}
    	GridData grid_speed = VectorMathod.getMod(wind);
    	grid_speed.smooth(50);
    	float lon,lat;
    	//鏍规嵁绔欑偣褰㈠紡鐨勭紪鍙凤紝鍙嶆帹璧峰浣嶇疆鏍肩偣浣嶇疆鐨勭紪鍙�
    	GridData wor_id = new GridData(wind.gridInfo);
    	GridData wor_value = new GridData(wind.gridInfo);
    	float dw;
    	for (int n = 0;n < sta_index.nsta;n++) {
    		ig = (int) sta_index.dat[n][3];
    		jg = (int) sta_index.dat[n][4];
    		lon = slon + ig * dlon;
    		lat = slat + jg * dlat;
 			dx = sta_index.dat[n][6] -lon;
 			dy = sta_index.dat[n][7] -lat;
 			dis = (float) Math.sqrt(dx*dx +dy *dy);

 			
			speed = grid_speed.dat[ig][jg];
 			vox = speed/dis;
			System.out.println(vox);
			
			u = direction.u.dat[ig][jg];
			v = direction.v.dat[ig][jg];
			dw = 0;
 			if(dis > 0) {
     			dw = (u * dy - v * dx);
 			}
 			wor_value.dat[ig][jg] = vox;
			
			if(vox >1 && dw > -1) {
				wor_id.dat[ig][jg] = sta_index.dat[n][5];		
			}
    	}
    	wor_value.smooth(3);
    	
    	SystemIdentification.smoothIds(wor_id,2);
    	
    	for(int i=0;i<nlon;i++) {
    		for (int j = 0;j<nlat;j++) {
    			if(wor_id.dat[i][j] == 0) {
    				wor_value.dat[i][j] =0;
    			}
    		}
    	}
    	
    	//wor_id.writeToFile("H:\\task\\link\\xiangji\\201905-weahter_identification\\output\\wor_id.txt");
    	
    	//wor_value.writeToFile("H:\\task\\link\\xiangji\\201905-weahter_identification\\output\\wor_value.txt");
    	//鍒ゆ柇姣忎釜鐐瑰拰娑℃棆涓績鐨勮繛绾夸笌椋庡満鏂瑰悜鐨勫す瑙�
    	
    	WeatherSystems vc = new WeatherSystems("vortex",level);
		vc.setValue(wor_value);
		vc.setIds(wor_id);
		vc.reset();
		return vc;
    }
    
    
}
