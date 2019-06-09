package recutil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.Math;
import java.util.ArrayList;




public class Sta4Data extends StaData{
	
	public static int nv=4;
	
	
	public Sta4Data(int nv0,StaData sta0){
		super(nv0,4);
		nsta=sta0.nsta;
		
		dat=new float[nsta][nv];
		
		for(int i=0;i<dat.length;i++){
			
			for(int j=0;j<3;j++){
				dat[i][j]=sta0.dat[i][j];
			}
			////System.out.println(dat[i][3]);
			
			dat[i][3]=sta0.dat[i][nv0];
		//	//System.out.println(dat[i][3]);
		}
		
	}
	
	public Sta4Data getValid(){
		int nValid=0;
		for(int j=0;j<this.nsta;j++){
		    if(this.dat[j][3]!=9999)nValid++;
		}
		Sta4Data validSta4Data=new Sta4Data(nValid);
		int i=0;
		for(int j=0;j<this.nsta;j++){
		    if(this.dat[j][3]!=9999){
		    	for(int k=0;k<4;k++){
		    		validSta4Data.dat[i][k]=dat[j][k];
		    	}
		    	i++;
		    }
		}
		
		return validSta4Data;
	}
	
	public Sta4Data(int nv) {
		// TODO 自动生成的方法存根
		super(nv,4);
	}
	
	public Sta4Data(int nsta, int nv) {
		// TODO自动生成的方法存根
		super(nsta,nv);
	}
	public Sta4Data(GridData grid){
		super(grid.gridInfo.nlon*grid.gridInfo.nlat,nv);
		int k=0;
		int nlon=grid.gridInfo.nlon,nlat=grid.gridInfo.nlat;
		float slon=grid.gridInfo.startlon,slat=grid.gridInfo.startlat;
		float dlon=grid.gridInfo.dlon,dlat=grid.gridInfo.dlat;
		for(int i=0;i<nlon;i++){
			for(int j=0;j<nlat;j++){
				dat[k][0]=i*1000+j;
				dat[k][1]=slon+i*dlon;
				dat[k][2]=slat+j*dlat;
				dat[k][3]=grid.dat[i][j];
				k++;
			}
		}
	}
	
	public float getMean(){
		float m=0;
		int n=0;
		for(int i=0;i<nsta;i++){
			if(dat[i][3]!=9999){
				m+=dat[i][3];
				n++;
			}
		}
		m/=n;
		return m;
	}
	
	public Sta4Data copy(){
		Sta4Data sta=new Sta4Data(this.nsta,4);
		for(int i=0;i<this.nsta;i++){
			for(int j=0;j<sta.dat[0].length;j++){
				sta.dat[i][j]=this.dat[i][j];
			}
		}
		return sta;
	}
	
	public void setValue(float v) {
		// TODO 自动生成的方法存根
		for(int i=0;i<nsta;i++){
			dat[i][3]=v;
		}
	}
	
	
	public void writeToFile(String fileName) {
		File file = new File(fileName);
		int y = 2018;
		int m = 1;
		int d = 1;
		int h = 8;
		int type = 1;
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(file),"GBK");
			BufferedWriter br=new BufferedWriter(fos);
			String str;
			
			int end=file.getName().length();
			int start=Math.max(0, end-16);
			str="diamond 3 "+file.getName().substring(start, end)+"\n"+y+" "+m+" "+d+" "+h+" "+type+" 0 0 0 0\n"+"1 "+nsta;
	
			br.write(str);
			int k;
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();   
			nf.setGroupingUsed(false);  
			for(int i=0;i<nsta;i++){
				br.write("\n");
				k=(int) this.dat[i][0];
				br.write(k+" ");
				br.write(nf.format(this.dat[i][1])+" ");
				br.write(nf.format(this.dat[i][2])+" ");
				br.write("1 ");
				br.write(nf.format(this.dat[i][3])+" ");
			}
			br.flush();
			fos.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			System.out.println(file.getAbsolutePath()+"写入失败");
		}
	}

	
	public void getValueFromGrid(GridData grid){
		int ig,jg;
		float dx,dy,c00,c01,c10,c11;
		for(int k=0;k<nsta;k++){
			ig=(int) ((dat[k][1]-grid.gridInfo.startlon)/grid.gridInfo.dlon);
			jg=(int) ((dat[k][2]-grid.gridInfo.startlat)/grid.gridInfo.dlat);
			dx=((dat[k][1]-grid.gridInfo.startlon)/grid.gridInfo.dlon)-ig;
			dy=((dat[k][2]-grid.gridInfo.startlat)/grid.gridInfo.dlat)-jg;
			c00=(1-dx)*(1-dy);
			c01=dx*(1-dy);
			c10=(1-dx)*dy;
			c11=dx*dy;
			if(ig>=0&&ig<grid.gridInfo.nlon-1&&jg>=0&&jg<grid.gridInfo.nlat-1){
				dat[k][3]=c00*grid.dat[ig][jg]+c01*grid.dat[ig+1][jg]+c10*grid.dat[ig][jg+1]+c11*grid.dat[ig+1][jg+1];	
			}
			else{
				dat[k][3]=0;
			}
		}
	}
}