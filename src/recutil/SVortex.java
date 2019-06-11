package recutil;

import java.util.ArrayList;
import java.util.Random;


/**
 * 濞戯拷
 */
public class SVortex{

	static String test_data_root= "D:/develop/java/";
	
	public static WeatherSystems getVortexCentres(VectorData wind, int level, float scale) {
		// TODO Auto-generated method stub
		
		//
		//

		//get_cents_by_flow(wind,scale);
		
		//wind.u.smooth(10); wind.v.smooth(10);
		//GridData marker = getCenter(wind);  // 鐠侊紕鐣诲☉鈩冩閸栫尨绱檓arker閸欐牕锟介棿璐�1閻ㄥ嫰鍎撮崚鍡礆
		//GridData cycle = GetCycleStrenth(wind.u,wind.v,scale);  //缁夘垰鍨庡妤冨箚濞翠礁宸辨惔锟�
		//cycle.writeToFile("D:\\develop\\java\\201905-weahter_identification\\output/cycle.txt");
		
		//cycle = cycle.mutiply(marker);                            // 娣囨繄鏆�濞戔剝妫嗛崠铏规畱閻滎垱绁﹀鍝勫
		//marker.writeToFile("D:\\develop\\java\\201905-weahter_identification\\output/marker.txt");
		GridData cycle = getcycle(wind);  
		GridData ids = SystemIdentification.getCuttedRegion(cycle,1);  //鐎规矮绠熷☉鈩冩缁崵绮洪敍宀冾啎缂冾喚娴夋惔鏂垮綁闁插骏绱濋獮鍫曪拷姘崇箖reset閸戣姤鏆熺拋锛勭暬濞戔剝妫嗛惃鍕厬韫囧啩缍呯純顔兼嫲閻╃鍙х仦鐐达拷锟�
		WeatherSystems vc = new WeatherSystems("vortex",level);
		vc.setValue(cycle);
		vc.setIds(ids);
		vc.reset();
		return vc;
	}
	public static GridData GetCycleStrenth(GridData gdUwnd, GridData gdVwnd, double minScale)
      {
          //閺嶅洤绻旀潏鎾冲毉閸︼拷
          GridData output = gdUwnd.copy();
          output.setValue(0.0f);
          //閺堬拷鐏忓繑涓侀弮瀣槀鎼达拷
          int xn = (int)(minScale/gdUwnd.gridInfo.dlon);
          int yn = (int)(minScale/gdUwnd.gridInfo.dlat);
          //闁秴宸诲В蹇庨嚋閻愮懓鎯婇悳锟�
          for (int j = 0; j < gdUwnd.gridInfo.nlat; j++)
          {
              for (int i = 0; i < gdUwnd.gridInfo.nlon; i++)
              {
                  //閺勵垰鎯侀弸鍕灇濞戔剝妫嗛惃鍕垼韫囷拷
                  double flag = 0;
                  //娑撳绔熼悾宀�袧閸掞拷
                  for (int ii = i - xn; ii <= i + xn; ii++)
                  {
                      int jj = j - yn;
                      if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                      {
                          flag = flag + gdUwnd.dat[ii][ jj];
                      }
                  }
                  //閸欏疇绔熼悾宀�袧閸掞拷
                  for (int jj = j - yn; jj <= j + yn; jj++)
                  {
                      int ii = i + xn;
                      if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                      {
                          flag = flag + gdVwnd.dat[ii][ jj];
                      }
                  }
                  //娑撳﹨绔熼悾宀�袧閸掞拷
                  for (int ii = i + xn; ii >= i - xn; ii--)
                  {
                      int jj = j + yn;
                      if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                      {
                          flag = flag + (-gdUwnd.dat[ii][ jj]);
                      }
                  }
                  //瀹革箒绔熼悾宀�袧閸掞拷
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

	public static GridData getcycle(VectorData wind) {
		GridData output = new GridData(wind.gridInfo);
		float slon = wind.gridInfo.startlon;
		float dlon = wind.gridInfo.dlon;
		float slat = wind.gridInfo.startlat;
		float dlat = wind.gridInfo.dlat;
		float[] point0 = new float[2],point1= new float[2],uv = new float[2];
		float dx,dy,dis = 0,vor;
        for (int j = 3; j < wind.gridInfo.nlat-3; j++)
        {
            for (int i = 3; i < wind.gridInfo.nlon-3; i++)
            {
            	
            	output.dat[i][j] = 2;
            	point0[0] = slon + i * dlon;
            	point0[1] = slat + j * dlat;
            	
            	for(int d = 1;d<3;d++) {
            		dis = d * wind.gridInfo.dlon;
            		
            		
            
            		boolean isCycle = true;
            		for (int s = 0; s< 360; s += 5) {
            			dx = (float) (dis * Math.cos(s * 3.1416/ 180));
            			dy = (float) (dis * Math.sin(s * 3.1416/ 180));
            			point1[0] = point0[0] + dx;
            			point1[1] = point0[1] + dy;
            			uv = VectorMathod.getValue(wind, point1);
            			vor = dx * uv[1] - dy *uv[0];
            			if (vor <0) {
            				isCycle = false;
            				output.dat[i][j] -= 1;
            				break;
            			}
            		}
            	}
            	
            }
        }
		
        
        //相邻9点和小于3的部分抹除
        GridData sum = new GridData(output.gridInfo);
        for (int j = 3; j < wind.gridInfo.nlat-3; j++)
        {
            for (int i = 3; i < wind.gridInfo.nlon-3; i++)
            {
            	
            	for(int p = -1;p<2;p++) {
            		for(int q =-1;q <2;q++) {
            			sum.dat[i][j] += output.dat[i+p][j+q];
            		}
            	}
            	if (sum.dat[i][j] < 3) {
            		output.dat[i][j] = 0;
            	}
            }
        }
        
        
      //设置一个宽度为4乘4的窗口，以output的权重计算出涡旋的中心
        ArrayList<float[]> cents = new ArrayList<float[]>();       
        for (int j = 3; j < wind.gridInfo.nlat-3; j++)
        {
            for (int i = 3; i < wind.gridInfo.nlon-3; i++)
            {
            	if(output.dat[i][j]>0) {
            		dx = 0;
            		dy = 0;
            		float index_sum = 0;
	            	for(int p = -3;p<4;p++) {
	            		for(int q =-3;q <4;q++) {
	            			dx += output.dat[i+p][j+q] * p;
	            			dy += output.dat[i+p][j+q] * q;
	            			index_sum += output.dat[i+p][j+q];
	            		}
	            	}
	            	dx /= index_sum;
	            	dy /= index_sum;
	            	float lon = wind.gridInfo.startlon + (i+dx) * wind.gridInfo.dlon;
	            	float lat = wind.gridInfo.startlat + (j+dy) * wind.gridInfo.dlat;
	            	
	            	boolean had = false;
	            	for(float[] cent:cents) {
	            		if (lon== cent[0] && lat== cent[1]) {
	            			had = true;
	            			break;
	            		}
	            	}
	            	if (!had) {
	            		float[] cent = new float[2];
	            		cent[0] = lon;
	            		cent[1] = lat;
	            		cents.add(cent);
	            	}
            	}
            }
        }
        
        //以涡旋中心搜素最大的圆
        int ig,jg,nx,ny,i,j;
        float lon,lat,dis2;
        GridData cur_vor = VectorMathod.getVor(wind);
        cur_vor.smooth(5);
        float cur;
        for(float[] cent :cents) {
        	float maxr = 0;
        	float max_cycle = 0;
        	for(int d = 3;d<10000;d++) {
        		dis = 0.5f * d * wind.gridInfo.dlon;	       
        		boolean isCycle = true;
        		float cycle = 0;
        		for (int s = 0; s< 360; s += 5) {
        			dx = (float) (dis * Math.cos(s * 3.1416/ 180));
        			dy = (float) (dis * Math.sin(s * 3.1416/ 180));
        			point1[0] = cent[0] + dx;
        			point1[1] = cent[1] + dy;
        			uv = VectorMathod.getValue(wind, point1);
        			cur = VectorMathod.getValue(cur_vor, point1);
        			vor = dx * uv[1] - dy *uv[0];
        			if (vor <0 || cur <0) {
        				isCycle = false;
        				break;
        			}
        			else {
        				cycle += vor;
        			}
        			
        		}
        		if(!isCycle) {
        			maxr = dis;
        			break;
        		}
        		else {
        			max_cycle = cycle;
        		}
        	}
        	ig =(int)((cent[0] - wind.gridInfo.startlon)/wind.gridInfo.dlon);
        	jg = (int)((cent[1] - wind.gridInfo.startlat)/wind.gridInfo.dlat);
        	nx = (int)(dis/wind.gridInfo.dlon) + 2;
        	ny = (int)(dis/wind.gridInfo.dlat) + 2;
        	
        	for(int p = -nx;p<nx; p++) {
        		for(int q = -ny; q<ny; q++) {
        			i = ig+ p;
        			j = jg + q;
        			if(i>=0 && i< wind.gridInfo.nlon && j>=0 && j<wind.gridInfo.nlat) {
        				lon = wind.gridInfo.startlon + i * wind.gridInfo.dlon;
        				lat = wind.gridInfo.startlat + j * wind.gridInfo.dlat;
        				dis2 = (lon - cent[0]) * (lon - cent[0]) + (lat - cent[1]) * (lat - cent[1]);
        				dis = (float) Math.sqrt(dis2);
        				if(dis < maxr) {
        					output.dat[i][j] = max_cycle/(dis + maxr);
        				}
        				
        			}
        		}
        	}
        	
        }
        
        //GridData pid0 = SystemIdentification.getCuttedRegion(output,1); 
        
        
        //pid0.writeToFile(test_data_root+"201905-weahter_identification\\output\\pid0.txt");
		
		//return pid0;
        return output;
	}
	  
	  
    public static GridData getMarker(GridData gdUwnd, GridData gdVwnd, double minScale)
    {
        //閺嶅洤绻旀潏鎾冲毉閸︼拷
        GridData output = gdUwnd.copy();
        output.setValue(0);
        //閺堬拷鐏忓繑涓侀弮瀣槀鎼达拷
        int xn = (int)(minScale/gdUwnd.gridInfo.dlon);
        int yn = (int)(minScale / gdUwnd.gridInfo.dlat);
        //闁秴宸诲В蹇庨嚋閻愮懓鎯婇悳锟�
        for (int j = 0; j < gdUwnd.gridInfo.nlat; j++)
        {
            for (int i = 0; i < gdUwnd.gridInfo.nlon; i++)
            {
                //閺勵垰鎯侀弸鍕灇濞戔剝妫嗛惃鍕垼韫囷拷
                int flag = 0;
                //娑撳绔熼悾宀�袧閸掞拷
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
                //閸欏疇绔熼悾宀�袧閸掞拷
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
                //娑撳﹨绔熼悾宀�袧閸掞拷
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
                //瀹革箒绔熼悾宀�袧閸掞拷
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
                //閻㈣京鍤庣粔顖氬瀻閸掋倖鏌囬弰顖氭儊閺勵垱涓侀弮锟�
                if (flag == 0)
                {
                    output.dat[i][ j] = 1.0f;
                }
            }
        }
        return output;
    }
	
    public static WeatherSystems getVortexCentres3(VectorData wind,int level,float scale) {

    	StaData sta_index = new StaData(wind.gridInfo.nlon*wind.gridInfo.nlat,9);
    	int m= 0;
		int nlon=wind.gridInfo.nlon,nlat=wind.gridInfo.nlat;
		float slon=wind.gridInfo.startlon,slat=wind.gridInfo.startlat;
		float dlon=wind.gridInfo.dlon,dlat=wind.gridInfo.dlat;
		float elon = wind.gridInfo.endlon,elat = wind.gridInfo.endlat;
		//
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
    	VectorData direction_30 = VectorMathod.rotate(direction0,45);
    	//direction.writeToFile("H:\\task\\link\\xiangji\\201905-weahter_identification\\output\\dir.txt", "2019010108");
    	
    	int ig,jg;
    	float rs = 0.3f;
    	float speed;
    	boolean need_flow = true;
		Sta4Data sta_u = new Sta4Data(sta_index.nsta);
		Sta4Data sta_v = new Sta4Data(sta_index.nsta);
		int fnum=0;
    	while(need_flow && fnum <2000) {
    		System.out.println(fnum);
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
    	sta_u.writeToFile(test_data_root+"201905-weahter_identification/output/sta_u.txt");
    	System.out.println("b");
    	

    	
    	ArrayList<float[]> cents = new ArrayList<float[]>();
    	float x,y;
    	boolean had;
    	float dis2;
      	float u,v;
    	float dx,dy,dis;
    	float vox;
    	
    	//鐠佹澘缍嶅☉鈩冩娑擃厼绺炬担宥囩枂閸滃瞼鍋ｉ弫锟�
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
    	
    
    	
    	
    	//
    	Sta4Data sta_cents = new Sta4Data(cents.size());
    	for(int k=0;k<cents.size();k++) {
    		sta_cents.dat[k][0] = k;
    		sta_cents.dat[k][1] = cents.get(k)[0];
    		sta_cents.dat[k][2] = cents.get(k)[1];
    	}
    	
    	ArrayList<Point> cent_points = new ArrayList<Point>();
    	
    	//
    	GridData vor = VectorMathod.getVor(wind);
    	sta_cents.getValueFromGrid(vor);
    	
    	for(int k=0;k<sta_cents.nsta;k++) {
    		if(sta_cents.dat[k][3] >0) {		
    			Point point = new Point(sta_cents.dat[k][1],sta_cents.dat[k][2]);
    			cent_points.add(point);
    		}
    	}
    	
    	//
    	sta_cents = new Sta4Data(cent_points.size());
    	for(int k=0;k<cent_points.size();k++) {
    		sta_cents.dat[k][0] = k;
    		sta_cents.dat[k][1] = cent_points.get(k).ptLon;
    		sta_cents.dat[k][2] = cent_points.get(k).ptLat;
    	}
    	
    	
    	//System.out.println(flow_times);
    	sta_cents.writeToFile(test_data_root+"201905-weahter_identification\\output\\end.txt");
    	
    	
    	//
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
    	//
    	GridData wor_id = new GridData(wind.gridInfo);
    	GridData wor_value = new GridData(wind.gridInfo);

    	
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
    	//
    	
    	WeatherSystems vc = new WeatherSystems("vortex",level);
		vc.setValue(wor_value);
		vc.setIds(wor_id);
		vc.reset();
		return vc;
 
		
		
		
    }
    public static WeatherSystems getVortexCentres2(VectorData wind,int level,float scale) {
    	//GridData grid_index = new GridData(wind.gridInfo);
    	//Sta4Data sta_index0 = new Sta4Data(grid_index);
    	
    	
    	StaData sta_index = new StaData(wind.gridInfo.nlon*wind.gridInfo.nlat,9);
    	int m= 0;
		int nlon=wind.gridInfo.nlon,nlat=wind.gridInfo.nlat;
		float slon=wind.gridInfo.startlon,slat=wind.gridInfo.startlat;
		float dlon=wind.gridInfo.dlon,dlat=wind.gridInfo.dlat;
		float elon = wind.gridInfo.endlon,elat = wind.gridInfo.endlat;
		//鐏忓棛缍夐弽鍏兼殶閹诡喛娴嗛崣妯硅礋缁旀瑧鍋ｈぐ銏犵础
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
    	
		//妫ｆ牕鍘涘▽璺ㄦ絻妞嬪骸婧�楠炶櫕绁�
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
    			//濞撳懘娅庨崙杞扮啊鏉堝湱鏅惃鍕仯
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
    	sta_u.writeToFile(test_data_root+"201905-weahter_identification/output/sta_u.txt");

    	System.out.println("A");
    	//娑撹桨绨￠柆鍨帳閻滎垰鑸伴弮瀣祮閿涘苯顤冮崝鐘叉倻濞戔�冲娑擃厼绺鹃惃鍕瀻闁诧拷
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
    	sta_u.writeToFile(test_data_root+"201905-weahter_identification/output/sta_u.txt");
    	System.out.println("b");
    	
    	
    	//鐎电懓鐨ta_index缂佸牏鍋ｆ担宥囩枂閿涘矁绁撮崐鐓庡煂缁楋拷6,7閸掞拷
    	for(int n=0;n<sta_index.nsta;n++) {
    		sta_index.dat[n][6] = sta_index.dat[n][1];
    		sta_index.dat[n][7] = sta_index.dat[n][2];
    		sta_index.dat[n][1] = slon+sta_index.dat[n][3]*dlon;
    		sta_index.dat[n][2] = slat+sta_index.dat[n][4]*dlat;
    	}
    	
    	
		//闁插秵鏌婄挧棰佺闁拷,閸掋倖鏌囬張澶嬬梾閺堝寮芥潪顒傛畱閹懎鍠�
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
    	
    	//鐠佹澘缍嶅☉鈩冩娑擃厼绺炬担宥囩枂閸滃瞼鍋ｉ弫锟�
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
    	
    	
    	//鐏忓棛鍋ｉ弫鏉跨毌閻ㄥ嫭涓侀弮瀣╄厬韫囧啳顔囪ぐ鏇氱瑓閺夈儻绱濋獮璺哄灩闂勶拷
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
    	
    	
    	//鐏忓棙涓侀弮瀣╄厬韫囧啰鍋ｉ梿鍡樻暭閹存劗鐝悙鐟拌埌瀵拷
    	Sta4Data sta_cents = new Sta4Data(cents.size());
    	for(int k=0;k<cents.size();k++) {
    		sta_cents.dat[k][0] = k;
    		sta_cents.dat[k][1] = cents.get(k)[0];
    		sta_cents.dat[k][2] = cents.get(k)[1];
    	}
    	
    	ArrayList<Point> cent_points = new ArrayList<Point>();
    	
    	//閸掋倖鏌囧☉鈩冩娑擃厼绺鹃惃鍕仯閺勵垰鎯佹径鍕艾濮濓絾涓佹惔锕�灏�
    	GridData vor = VectorMathod.getVor(wind);
    	sta_cents.getValueFromGrid(vor);
    	
    	for(int k=0;k<sta_cents.nsta;k++) {
    		if(sta_cents.dat[k][3] >0) {		
    			Point point = new Point(sta_cents.dat[k][1],sta_cents.dat[k][2]);
    			cent_points.add(point);
    		}
    	}
    	
    	//娣囨繄鏆�濮濓絾涓佹惔锕�灏惃鍕竵閺冨鑵戣箛鍐х秴缂冿拷
    	sta_cents = new Sta4Data(cent_points.size());
    	for(int k=0;k<cent_points.size();k++) {
    		sta_cents.dat[k][0] = k;
    		sta_cents.dat[k][1] = cent_points.get(k).ptLon;
    		sta_cents.dat[k][2] = cent_points.get(k).ptLat;
    	}
    	
    	
    	//System.out.println(flow_times);
    	sta_cents.writeToFile(test_data_root+"201905-weahter_identification\\output\\end.txt");
    	
    	
    	//閺嶈宓侀獮铏ウ閸氬窏ta_index閻ㄥ嫪缍呯純顕嗙礉閸掋倖鏌囬崗璺虹潣娴滃骸鎽㈡稉锟芥稉顏呬竵閺冨鑵戣箛鍐跨礉楠炴湹浜掑銈勭稊娑撹櫣绱崣锟�
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
    	//閺嶈宓佺粩娆戝仯瑜般垹绱￠惃鍕椽閸欏嚖绱濋崣宥嗗腹鐠у嘲顫愭担宥囩枂閺嶈偐鍋ｆ担宥囩枂閻ㄥ嫮绱崣锟�
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
    	//閸掋倖鏌囧В蹇庨嚋閻愮懓鎷板☉鈩冩娑擃厼绺鹃惃鍕箾缁惧じ绗屾搴℃簚閺傜懓鎮滈惃鍕仚鐟欙拷
    	
    	WeatherSystems vc = new WeatherSystems("vortex",level);
		vc.setValue(wor_value);
		vc.setIds(wor_id);
		vc.reset();
		return vc;
    }
    
    
}
