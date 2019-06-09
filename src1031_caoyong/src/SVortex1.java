
public class SVortex1 {
	
	public static WeatherSystems getVortexCentres(VectorData wind, int level, double scale)
	{		
        GridData gdUwnd = wind.u;
        GridData gdVwnd = wind.v;
        
        gdUwnd.smooth(10);
        gdVwnd.smooth(10);

        GridData output = DetectVortex(gdUwnd, gdVwnd, scale);
        GridData strenth = GetCycleStrenth(gdUwnd, gdVwnd, scale);
        GridData cluster = ClassifyGrid(output);

        float maxLevel = cluster.max();
        double flagLevel = 1.0;
        while (flagLevel <= maxLevel)
        {
            double maxData = 0.0;
            for (int j = 0; j < cluster.gridInfo.nlat; j++)
            {
                for (int i = 0; i < cluster.gridInfo.nlon; i++)
                {
                    if (cluster.dat[i][j] == level)
                    {
                        if (strenth.dat[i][j] >= maxData)
                        {
                            maxData = strenth.dat[i][j];
                        }
                    }
                }
            }
            for (int j = 0; j < cluster.gridInfo.nlat; j++)
            {
                for (int i = 0; i < cluster.gridInfo.nlon; i++)
                {
                    if (cluster.dat[i][j] == level)
                    {
                        if (strenth.dat[i][ j] != maxData)
                        {
                            cluster.dat[i][j] = 0.0f;
                        }
                    }
                }
            }
            flagLevel = flagLevel + 1.0;
        }


		WeatherSystems vc = new WeatherSystems("vortex",level);
		vc.setValue(cycle);
		vc.setIds(ids);
		vc.reset();
		return vc;
	}

	        public static GridData DetectVortex(GridData gdUwnd, GridData gdVwnd, double minScale)
	        {
	            //标志输出场
	            GridData output = gdUwnd.copy();
	            output.setValue(0);
	            //最小涡旋尺度
	            int xn = (int)(minScale / gdUwnd.gridInfo.dlon);
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

	        public static GridData GetCycleStrenth(GridData gdUwnd, GridData gdVwnd, double minScale)
	        {
	            //标志输出场
	            GridData output = gdUwnd.copy();
	            output.setValue(0.0f);
	            //最小涡旋尺度
	            int xn = (int)(minScale / gdUwnd.gridInfo.nlon);
	            int yn = (int)(minScale / gdUwnd.gridInfo.nlat);
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

	        public static GridData ClassifyGrid(GridData gdInputData)
	        {
	            GridData gdOutputData = gdInputData.copy();
	            gdOutputData.setValue(0.0f);

	            GridData CheckFlag = gdInputData.copy();
	            CheckFlag.setValue(0.0f);

	            float totalFlag = 0.0f;

	            for (int j = 0; j < gdInputData.gridInfo.nlat; j++)
	            {
	                for (int i = 0; i < gdInputData.gridInfo.nlon; i++)
	                {
	                    if (CheckFlag.dat[i][ j] == 0)
	                    {
	                        if (gdInputData.dat[i][ j] == 1.0)
	                        {
	                            totalFlag = totalFlag + 1.0f;
	                            FindAroundGrid(gdInputData.dat, gdOutputData.dat, CheckFlag.dat, i, j, totalFlag);
	                        }
	                    }
	                }
	            }

	            return gdOutputData;
	        }

	        public static void FindAroundGrid(float[][] input, float[][] output, float[][] check, int ix, int iy, float flag)
	        {
	            if (check[ix][iy] == 0.0)
	            {
	                if (input[ix][iy] == 1.0)
	                {
	                    output[ix][iy] = flag;
	                    check[ix][iy] = 1.0f;

	                    if (ix - 1 >= 0 && iy - 1 >= 0)
	                    {
	                        FindAroundGrid(input, output, check, ix - 1, iy - 1, flag);
	                    }

	                    if (ix >= 0 && iy - 1 >= 0)
	                    {
	                        FindAroundGrid(input, output, check, ix, iy - 1, flag);
	                    }

	                    if (ix + 1 <input.length && iy - 1 >= 0)
	                    {
	                        FindAroundGrid(input, output, check, ix + 1, iy - 1, flag);
	                    }

	                    if (ix + 1 < input.length && iy >= 0)
	                    {
	                        FindAroundGrid(input, output, check, ix + 1, iy, flag);
	                    }

	                    if (ix + 1 < input.length && iy + 1 < input[0].length)
	                    {
	                        FindAroundGrid(input, output, check, ix + 1, iy + 1, flag);
	                    }

	                    if (ix < input.length && iy + 1 < input[0].length)
	                    {
	                        FindAroundGrid(input, output, check, ix, iy + 1, flag);
	                    }

	                    if (ix - 1 >= 0 && iy + 1 < input[0].length)
	                    {
	                        FindAroundGrid(input, output, check, ix - 1, iy + 1, flag);
	                    }

	                    if (ix - 1 >= 0 && iy < input[0].length)
	                    {
	                        FindAroundGrid(input, output, check, ix - 1, iy, flag);
	                    }
	                }
	                else
	                {
	                    check[ix][ iy] = 1.0f;
	                    return;
	                }
	            }
	            else
	            {
	                return;
	            }
	        }
}
