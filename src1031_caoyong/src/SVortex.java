

public class SVortex{

	public static WeatherSystems getVortexCentres(VectorData wind, int level, double scale) {
		// TODO Auto-generated method stub
		
		//*********************************************************************
		//计算格点附近环流，作为涡旋特征，并且设计一定的消空条件，将环流为正但不符合条件的部分去除，计算好了格点场cycle之后，其它部分不同条件，但需校验cycle的分布是否符合预期
		GridData eastCycle = new GridData(wind.gridInfo);
		GridData westCycle= new GridData(wind.gridInfo);
		GridData northCycle = new GridData(wind.gridInfo);
		GridData southCycle = new GridData(wind.gridInfo);
		int d  = (int) (scale*1000/MyMath.oneDegreeDis/wind.gridInfo.dlat);
		for(int i=d+1;i<wind.gridInfo.nlon-d-1;i++){
			for(int j= d+1; j<wind.gridInfo.nlat-d-1;j++){
				for(int k=-d;k<=d;k++){
					eastCycle.dat[i][j] += wind.v.dat[i+d][j+k];
					westCycle.dat[i][j] -= wind.v.dat[i-d][j+k];
					northCycle.dat[i][j] -= wind.u.dat[i+k][j+d];
					southCycle.dat[i][j] += wind.u.dat[i+k][j-d];
				}
			}
		}
		
		GridData marker = eastCycle.sign01().mutiply(westCycle.sign01()).mutiply(northCycle.sign01()).mutiply(southCycle.sign01());
		GridData cycle = eastCycle.add(westCycle).add(northCycle).add(southCycle).mutiply(marker);
		cycle.writeToFile("G:/data/systemIdentify/cycle.txt");
		
		//********************************************************************
		
		
		GridData ids = SystemIdentification.getCuttedRegion(cycle);
		WeatherSystems vc = new WeatherSystems("vortex",level);
		vc.setValue(cycle);
		vc.setIds(ids);
		vc.reset();
		return vc;
	}

}
