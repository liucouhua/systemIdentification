package code;


public class SVortex{

	public static WeatherSystems getVortexCentres(VectorData wind, int level, double scale) {
		// TODO Auto-generated method stub
		
		//
		//

		wind.u.smooth(10); wind.v.smooth(10);
		GridData marker = getMarker(wind.u,wind.v,scale);  // 计算涡旋区（marker取值为1的部分）
		GridData cycle = GetCycleStrenth(wind.u,wind.v,scale);  //积分得环流强度
	//	cycle.writeToFile("G:/data/systemIdentify/cycle.txt");
		
		cycle = cycle.mutiply(marker);                            // 保留涡旋区的环流强度
	//	marker.writeToFile("G:/data/systemIdentify/marker.txt");
		GridData ids = SystemIdentification.getCuttedRegion(cycle);  //定义涡旋系统，设置相应变量，并通过reset函数计算涡旋的中心位置和相关属性
		WeatherSystems vc = new WeatherSystems("vortex",level);
		vc.setValue(cycle);
		vc.setIds(ids);
		vc.reset();
		return vc;
	}
	  public static GridData GetCycleStrenth(GridData gdUwnd, GridData gdVwnd, double minScale)
      {
          //标志输出场
          GridData output = gdUwnd.copy();
          output.setValue(0.0f);
          //最小涡旋尺度
          int xn = (int)(minScale/gdUwnd.gridInfo.dlon);
          int yn = (int)(minScale/gdUwnd.gridInfo.dlat);
          //遍历每个点循环
          for (int j = 0; j < gdUwnd.gridInfo.nlat; j++)
          {
              for (int i = 0; i < gdUwnd.gridInfo.nlon; i++)
              {
                  //是否构成涡旋的标志
                  double flag = 0;
                  //下边界积分
                  for (int ii = i - xn; ii <= i + xn; ii++)
                  {
                      int jj = j - yn;
                      if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                      {
                          flag = flag + gdUwnd.dat[ii][ jj];
                      }
                  }
                  //右边界积分
                  for (int jj = j - yn; jj <= j + yn; jj++)
                  {
                      int ii = i + xn;
                      if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                      {
                          flag = flag + gdVwnd.dat[ii][ jj];
                      }
                  }
                  //上边界积分
                  for (int ii = i + xn; ii >= i - xn; ii--)
                  {
                      int jj = j + yn;
                      if (ii >= 0 && ii < gdUwnd.gridInfo.nlon && jj >= 0 && jj < gdUwnd.gridInfo.nlat)
                      {
                          flag = flag + (-gdUwnd.dat[ii][ jj]);
                      }
                  }
                  //左边界积分
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
        //标志输出场
        GridData output = gdUwnd.copy();
        output.setValue(0);
        //最小涡旋尺度
        int xn = (int)(minScale/gdUwnd.gridInfo.dlon);
        int yn = (int)(minScale / gdUwnd.gridInfo.dlat);
        //遍历每个点循环
        for (int j = 0; j < gdUwnd.gridInfo.nlat; j++)
        {
            for (int i = 0; i < gdUwnd.gridInfo.nlon; i++)
            {
                //是否构成涡旋的标志
                int flag = 0;
                //下边界积分
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
                //右边界积分
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
                //上边界积分
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
                //左边界积分
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
                //由线积分判断是否是涡旋
                if (flag == 0)
                {
                    output.dat[i][ j] = 1.0f;
                }
            }
        }
        return output;
    }
	
}
