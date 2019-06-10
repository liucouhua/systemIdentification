package recutil;
public class VectorMathod {

	public static VectorData getGrads(GridData grid){
		VectorData ve=new VectorData(grid.gridInfo);
		int nlat=grid.gridInfo.nlat;
		int nlon=grid.gridInfo.nlon;
		int j_1,j1,i_1,i1,dj,di;
		float sr;
		float dlat=grid.gridInfo.dlat;
		float dlon=grid.gridInfo.dlon;
		float slat=grid.gridInfo.startlat;
		float slon=grid.gridInfo.startlon;
		for(int j=0;j<nlat;j++){
			j_1=MyMath.cycleIndex(false, nlat, j, -1);
			j1=MyMath.cycleIndex(false, nlat, j, 1);
			dj=j1-j_1;
			sr=(float) Math.cos((j*dlat+slat)*3.14f/180.0f);
			for(int i=0;i<ve.gridInfo.nlon;i++){
				i_1=MyMath.cycleIndex(false, nlon, i, -1);
				i1=MyMath.cycleIndex(false, nlon, i, 1);
				di=i1-i_1;
				ve.u.dat[i][j]=(grid.dat[i1][j]-grid.dat[i_1][j])/(sr*dlon*di);
				ve.v.dat[i][j]=(grid.dat[i][j1]-grid.dat[i][j_1])/(dlat*dj);
			}
		}
		return ve;
	}
	
	public static VectorData getGeostrophicWind(GridData grid){
		VectorData ve=new VectorData(grid.gridInfo);
		int nlat=grid.gridInfo.nlat;
		int nlon=grid.gridInfo.nlon;
		int j_1,j1,i_1,i1,dj,di;
		float sr,fd;
		float dlat=grid.gridInfo.dlat;
		float dlon=grid.gridInfo.dlon;
		float slat=grid.gridInfo.startlat;
		float slon=grid.gridInfo.startlon;
		float lon;
		for(int j=0;j<nlat;j++){
			j_1=MyMath.cycleIndex(false, nlat, j, -1);
			j1=MyMath.cycleIndex(false, nlat, j, 1);
			dj=j1-j_1;
			lon = j*dlat+slat;
			sr=(float) Math.cos((lon)*3.14f/180.0f);
			if(lon>=0 && lon<20)lon=20;
			if(lon<0 && lon>-20)lon = -20;
			fd = MyMath.getF(lon)*MyMath.oneDegreeDis;
			for(int i=0;i<ve.gridInfo.nlon;i++){
				i_1=MyMath.cycleIndex(false, nlon, i, -1);
				i1=MyMath.cycleIndex(false, nlon, i, 1);
				di=i1-i_1;
				ve.u.dat[i][j]=-10*(grid.dat[i][j1]-grid.dat[i][j_1])/(fd*dlat*dj);
				ve.v.dat[i][j]=10*(grid.dat[i1][j]-grid.dat[i_1][j])/(fd*sr*dlon*di);
			}
		}
		return ve;
	}
	
	public static VectorData rotate(VectorData ve0,float angle){
		VectorData ve=new VectorData(ve0.gridInfo);
		int nlat=ve0.gridInfo.nlat;
		int nlon=ve0.gridInfo.nlon;
		for(int j=0;j<nlat;j++){	
			for(int i=0;i<ve.gridInfo.nlon;i++){
				float c=(float) Math.cos(angle*3.14/180);
				float s=(float) Math.sin(angle*3.14/180);
				ve.u.dat[i][j]=ve0.u.dat[i][j]*c-ve0.v.dat[i][j]*s;
				ve.v.dat[i][j]=ve0.u.dat[i][j]*s+ve0.v.dat[i][j]*c;
			}
		}
		return ve;
	}
	
	public static GridData getU(VectorData ve) {
		return ve.u.copy();
	}
	public static GridData getV(VectorData ve) {
		return ve.v.copy();
	}
	public static GridData getMod(VectorData ve) {
		GridData grid=new GridData(ve.gridInfo);
		for(int i=0;i<ve.gridInfo.nlon;i++){
			for(int j=0;j<ve.gridInfo.nlat;j++){
				grid.dat[i][j]=(float) Math.sqrt(ve.u.dat[i][j]*ve.u.dat[i][j]+ve.v.dat[i][j]*ve.v.dat[i][j]);
			}
		}
		return grid;
	}
	public static GridData getDiv(VectorData ve) {
		GridData grid=new GridData(ve.gridInfo);
		int i_1,i1,j_1,j1,di,dj;
		int nlat=ve.gridInfo.nlat,nlon=ve.gridInfo.nlon;
		float sr;
		float dlat=ve.gridInfo.dlat,slat=ve.gridInfo.startlat;
		float dlon=ve.gridInfo.dlon;
		for(int j=0;j<nlat;j++){
			j_1=MyMath.cycleIndex(false, nlat, j, -1);
			j1=MyMath.cycleIndex(false, nlat, j, 1);
			dj=j1-j_1;
			sr=(float) Math.cos((j*dlat+slat)*3.14f/180.0f);
			for(int i=0;i<ve.gridInfo.nlon;i++){
				i_1=MyMath.cycleIndex(false, nlon, i, -1);
				i1=MyMath.cycleIndex(false, nlon, i, 1);
				di=i1-i_1;
				grid.dat[i][j]=(ve.u.dat[i1][j]-ve.u.dat[i_1][j])/(sr*dlon*di)+(ve.v.dat[i][j1]-ve.v.dat[i][j_1])/(dlat*dj);
			}
		}		
		return grid;
	}
	
	public static GridData getVor(VectorData ve) {
		GridData grid=new GridData(ve.gridInfo);
		int i_1,i1,j_1,j1,di,dj;
		int nlat=ve.gridInfo.nlat,nlon=ve.gridInfo.nlon;
		float sr;
		float dlat=ve.gridInfo.dlat,slat=ve.gridInfo.startlat;
		float dlon=ve.gridInfo.dlon;
		float sign=dlat*dlon/(Math.abs(dlat*dlon));
		for(int j=0;j<nlat;j++){
			j_1=MyMath.cycleIndex(false, nlat, j, -1);
			j1=MyMath.cycleIndex(false, nlat, j, 1);
			dj=j1-j_1;
			sr=(float) Math.cos((j*dlat+slat)*3.14f/180.0f);
			for(int i=0;i<ve.gridInfo.nlon;i++){
				i_1=MyMath.cycleIndex(false, nlon, i, -1);
				i1=MyMath.cycleIndex(false, nlon, i, 1);
				di=i1-i_1;
				grid.dat[i][j]=sign*((ve.v.dat[i1][j]-ve.v.dat[i_1][j])/(dlat*dj)-(ve.u.dat[i][j1]-ve.u.dat[i][j_1])/(sr*dlon*di));
			}
		}		
		return grid;
	}
	
	public static GridData getCurvatureVor(GridData grid){
		GridData cur= getCurvature(grid);
		GridData gradMod= VectorMathod.getMod(VectorMathod.getGeostrophicWind(grid));
		for(int i=0;i<cur.gridInfo.nlon;i++){
			for(int j=0;j<cur.gridInfo.nlat;j++){
				cur.dat[i][j]*=gradMod.dat[i][j];
			}
		}
		return cur;
	}
	
	public static GridData getCurvature(GridData grid){
		GridData cur=new GridData(grid.gridInfo);
		int nlon=grid.gridInfo.nlon;
		int nlat=grid.gridInfo.nlat;
		float slat=grid.gridInfo.startlat;
		float slon= grid.gridInfo.startlon;
		float dlat=grid.gridInfo.dlat;
		float dlon=grid.gridInfo.dlon;
		VectorData grad = VectorMathod.getGrads(grid);
		GridData gradMod= VectorMathod.getMod(grad);
		VectorData lineDir = VectorMathod.getDirection(VectorMathod.rotate(grad, 90));
		
		float[] leftPoint=new float[2];
		float[] rightPoint=new float[2];
		float leftValue,rightValue,leftGd,rightGd;
		for(int j=1;j<nlat-1;j++){
			float sr=(float) (1/Math.cos((slat+j*dlat)*3.14/180));
			for(int i=1;i<nlon-1;i++){
				 leftPoint[0] = slon + i*dlon - lineDir.u.dat[i][j]*sr;
				 leftPoint[1] = slat + j*dlat - lineDir.v.dat[i][j];
				 rightPoint[0] = slon + i*dlon + lineDir.u.dat[i][j]*sr;
				 rightPoint[1] = slat + j*dlat + lineDir.v.dat[i][j];
				 leftValue = getValue(grid,leftPoint);
				 rightValue = getValue(grid,rightPoint);
				 leftGd = getValue(gradMod,leftPoint);
				 rightGd= getValue(gradMod,rightPoint);
				 if(leftValue!=9999 && rightValue!=9999){
					 cur.dat[i][j] = ((leftValue-grid.dat[i][j])/leftGd+(rightValue-grid.dat[i][j])/rightGd);
				 }
				 else{
					 cur.dat[i][j] = 0;
				 }	
			}
		}
		return cur;
	}
	
	
	public static GridData getCurvature(VectorData wind){
		
		GridData cur=new GridData(wind.gridInfo);
		int nlon=wind.gridInfo.nlon;
		int nlat=wind.gridInfo.nlat;
		float slat=wind.gridInfo.startlat;
		float slon= wind.gridInfo.startlon;
		float dlat=wind.gridInfo.dlat;
		float dlon=wind.gridInfo.dlon;

		VectorData lineDir =getDirection(wind);
		
		float[] leftPoint=new float[2];
		float[] rightPoint=new float[2];
		float left_u,left_v,right_u,right_v;
		for(int j=1;j<nlat-1;j++){
			float sr=(float) (1/Math.cos((slat+j*dlat)*3.14/180));
			for(int i=1;i<nlon-1;i++){
				 leftPoint[0] = slon + i*dlon - lineDir.u.dat[i][j]*sr;
				 leftPoint[1] = slat + j*dlat - lineDir.v.dat[i][j];
				 rightPoint[0] = slon + i*dlon + lineDir.u.dat[i][j]*sr;
				 rightPoint[1] = slat + j*dlat + lineDir.v.dat[i][j];
				 left_u = getValue(lineDir.u,leftPoint);
				 left_v = getValue(lineDir.v,leftPoint);
				 right_u = getValue(lineDir.u,rightPoint);
				 right_v = getValue(lineDir.v,rightPoint);
				 if(left_u != 9999 && right_u != 9999){
					 cur.dat[i][j] = left_u * right_v - right_u * left_v;
				 }
				 else{
					 cur.dat[i][j] = 0;
				 }	
			}
		}
		return cur;
	}
	
	public static GridData getCurvatureVor(VectorData wind){
		GridData cur = getCurvature(wind);
		GridData speed = getMod(wind);
		for(int i=0;i< wind.gridInfo.nlon;i++) {
			for(int j=0;j<wind.gridInfo.nlat;j++) {
				cur.dat[i][j] = cur.dat[i][j] * speed.dat[i][j];
			}
		}
		
		return cur;
	}
	
	
	static float getValue(GridData grid, float[] point) {
		// TODO Auto-generated method stub
		int ig=(int) ((point[0]-grid.gridInfo.startlon)/grid.gridInfo.dlon);
		int jg=(int) ((point[1]-grid.gridInfo.startlat)/grid.gridInfo.dlat);
		float dx=((point[0]-grid.gridInfo.startlon)/grid.gridInfo.dlon)-ig;
		float dy=((point[1]-grid.gridInfo.startlat)/grid.gridInfo.dlat)-jg;
		float c00=(1-dx)*(1-dy);
		float c01=dx*(1-dy);
		float c10=(1-dx)*dy;
		float c11=dx*dy;
		float value;
		if(ig>=0&&ig<grid.gridInfo.nlon-1&&jg>=0&&jg<grid.gridInfo.nlat-1){
			value=c00*grid.dat[ig][jg]+c01*grid.dat[ig+1][jg]+c10*grid.dat[ig][jg+1]+c11*grid.dat[ig+1][jg+1];	
		}
		else{
			value=9999;
		}
		return value;
	}

	static float[] getValue(VectorData wind, float[] point) {
		// TODO Auto-generated method stub
		float [] uv = new float[2];
		uv[0] = getValue(wind.u,point);
		uv[1] = getValue(wind.v,point);
		return uv;
	}
	
	
	public static GridData getFlux(VectorData ve,GridData q) {
		GridData grid=new GridData(ve.gridInfo);
		GridData mod=getMod(ve);
		for(int i=0;i<ve.gridInfo.nlon;i++){
			for(int j=0;j<ve.gridInfo.nlat;j++){
				grid.dat[i][j]=mod.dat[i][j]*q.dat[i][j];
			}
		}
		return grid;
	}
	public static GridData getFluxDiv(VectorData ve,GridData q) {

		GridData grid=new GridData(ve.gridInfo);
		int i_1,i1,j_1,j1,di,dj;
		int nlat=ve.gridInfo.nlat,nlon=ve.gridInfo.nlon;
		float sr;
		float dlat=ve.gridInfo.dlat,slat=ve.gridInfo.startlat;
		float dlon=ve.gridInfo.dlon;
		for(int j=0;j<nlat;j++){
			j_1=MyMath.cycleIndex(false, nlat, j, -1);
			j1=MyMath.cycleIndex(false, nlat, j, 1);
			dj=j1-j_1;
			sr=(float) Math.cos((j*dlat+slat)*3.14f/180.0f);
			for(int i=0;i<ve.gridInfo.nlon;i++){
				i_1=MyMath.cycleIndex(false, nlon, i, -1);
				i1=MyMath.cycleIndex(false, nlon, i, 1);
				di=i1-i_1;
				grid.dat[i][j]=(ve.u.dat[i1][j]*q.dat[i1][j]-ve.u.dat[i_1][j]*q.dat[i_1][j])/(sr*dlon*di)
						+(ve.v.dat[i][j1]*q.dat[i][j1]-ve.v.dat[i][j_1]*q.dat[i][j_1])/(dlat*dj);
			}
		}		
		return grid;
	}
	// get direction of wind ,return a vectorData with mod=1
	public static VectorData getDirection(VectorData ve) {
		GridData mod=getMod(ve);
		VectorData direction=new VectorData(ve.gridInfo);
		for(int i=0;i<ve.gridInfo.nlon;i++){
			for(int j=0;j<ve.gridInfo.nlat;j++){
				direction.u.dat[i][j]=ve.u.dat[i][j]/(mod.dat[i][j]+0.000001f);
				direction.v.dat[i][j]=ve.v.dat[i][j]/(mod.dat[i][j]+0.000001f);
			}
		}
		return direction;
	}
	// get advection of q by ve, return a GridData
	public static GridData getAdvection(VectorData ve,GridData q) {
		VectorData grads=getGrads(q);
		return dot(ve,grads);
	}
	
	
	public static GridData dot(VectorData ve1,VectorData ve2){
		GridData grid=new GridData(ve1.gridInfo);
		for(int i=0;i<ve1.gridInfo.nlon;i++){
			for(int j=0;j<ve1.gridInfo.nlat;j++){
				grid.dat[i][j]=ve1.u.dat[i][j]*ve2.u.dat[i][j]+ve1.v.dat[i][j]*ve2.v.dat[i][j];
			}
		}
		return grid;
	}
	
	
	
}
