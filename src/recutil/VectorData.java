package recutil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import recutil.GridDataModel;
import recutil.WindUtils;
import recutil.Sta4Data;
import recutil.GridData;

public class VectorData {
	public GridInfo gridInfo;
	public GridData u,v;
	
	public VectorData(GridDataModel model){
		gridInfo=new GridInfo(model);
//		=new float[gridInfo.nlon][gridInfo.nlat];
		float[][] uValue =  new float[gridInfo.nlon][gridInfo.nlat];
		float[][] vValue =  new float[gridInfo.nlon][gridInfo.nlat];
		for(int i=0;i<gridInfo.nlon;i++){
			for(int j=0;j<gridInfo.nlat;j++){
				int index = 0;
				//鍒ゆ柇鏁版嵁鏄珮绾害鍒颁綆绾害杩樻槸鍏朵粬鎺掑垪鐨�
				if(model.getLatstep()>0){
					index = j*gridInfo.nlon+i;
					
				}else{
					index = (gridInfo.nlat-j-1)*gridInfo.nlon+i;
				}
				float wspeed = model.getDatas()[index];
				float wdir = model.getVerctor()[index];
				wdir = 360-wdir;
				if(wdir>=90){
					wdir-=90;
				}else{
					wdir+=270;
				}
				float[] uv = WindUtils.getUV(wspeed, wdir);
				uValue[i][j]=uv[0];
				vValue[i][j]=uv[1];
			}
		}
		u = new GridData(gridInfo);
		v = new GridData(gridInfo);
		u.dat=uValue;
		v.dat=vValue;
	}
	
	
	public VectorData(int nlon,int nlat,float startlon,float startlat,float dlon,float dlat){
		gridInfo=new GridInfo(nlon,nlat,startlon,startlat,dlon,dlat);
		u=new GridData(gridInfo);
		v=new GridData(gridInfo);
	}
	

	
	public VectorData(GridInfo gridInfo0) {
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
		gridInfo=gridInfo0.copy();
		u=new GridData(gridInfo);
		v=new GridData(gridInfo);
	}
	
	public VectorData copy() {
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
		VectorData vector=new VectorData(gridInfo);
		for(int i=0;i<vector.gridInfo.nlon;i++){
			for(int j=0;j<vector.gridInfo.nlat;j++){
				vector.u.dat[i][j]=this.u.dat[i][j];
				vector.v.dat[i][j]=this.v.dat[i][j];
			}
		}
		return vector;
	}
	
	public VectorData(String fileName)  {
		File file=new File(fileName);
		

		// TODO 鑷姩鐢熸垚鐨勬瀯閫犲嚱鏁板瓨鏍�
		// TODO 鑷姩鐢熸垚鐨勬瀯閫犲嚱鏁板瓨鏍�
		String str;
		String[] strs;
		String zz="\\s+";
		Pattern pat=Pattern.compile(zz);
		try {
			FileInputStream in = new FileInputStream(file);
			byte[] readBytes = new byte[in.available()];
			in.read(readBytes);
			str = new String(readBytes);
			
			strs=pat.split(str.trim());
			int fileType = Integer.parseInt(strs[1]);
			if(fileType == 11) {
				int y=Integer.parseInt(strs[3]);
				int m=Integer.parseInt(strs[4]);
				int d=Integer.parseInt(strs[5]);
				int h=Integer.parseInt(strs[6]);
				gridInfo=new GridInfo();
				int k;
				try{
					gridInfo.dlon=Float.parseFloat(strs[8]);
					gridInfo.dlat=Float.parseFloat(strs[9]);
					gridInfo.startlon=Float.parseFloat(strs[10]);
					gridInfo.endlon=Float.parseFloat(strs[11]);
					gridInfo.startlat=Float.parseFloat(strs[12]);
					gridInfo.endlat=Float.parseFloat(strs[13]);		
					gridInfo.nlon=Integer.parseInt(strs[14]);
					gridInfo.nlat=Integer.parseInt(strs[15]);
					k=15;
					if((gridInfo.nlat-1)*gridInfo.dlat!=(gridInfo.endlat-gridInfo.startlat)||(gridInfo.nlon-1)*gridInfo.dlon!=(gridInfo.endlon-gridInfo.startlon)){
						throw new Exception();
					}
				}catch (Exception e){
					gridInfo.dlon=Float.parseFloat(strs[9]);
					gridInfo.dlat=Float.parseFloat(strs[10]);
					gridInfo.startlon=Float.parseFloat(strs[11]);
					gridInfo.endlon=Float.parseFloat(strs[12]);
					gridInfo.startlat=Float.parseFloat(strs[13]);
					gridInfo.endlat=Float.parseFloat(strs[14]);		
					gridInfo.nlon=Integer.parseInt(strs[15]);
					gridInfo.nlat=Integer.parseInt(strs[16]);
					k=16;
				}
				u=new GridData(gridInfo);
				v=new GridData(gridInfo);
				for (int j=0;j<gridInfo.nlat;j++){
					for(int i=0;i<gridInfo.nlon;i++){
						k=k+1;
						u.dat[i][j]=Float.parseFloat(strs[k]);
					//	//System.out.println(u.dat[i][j]);
					}
				}
				
				for (int j=0;j<gridInfo.nlat;j++){
					for(int i=0;i<gridInfo.nlon;i++){
						k=k+1;
						v.dat[i][j]=Float.parseFloat(strs[k]);
					//	//System.out.println(j+" "+i+" "+v.dat[i][j]);
					}
				}
				
				u.reSetXY();
				v.reSetXY();
				u.reSetResolution();
				v.reSetResolution();
				this.gridInfo=u.gridInfo.copy();
			}
			else if(fileType == 2) {
				StaData staData = new StaData(fileName);
				//System.out.println(fileName);
				Sta4Data sta_speed = new Sta4Data(9,staData);
				Sta4Data sta_direction = new Sta4Data(8,staData);
				GridData speed = new GridData(sta_speed);
				GridData direction = new GridData(sta_direction);
				gridInfo = speed.gridInfo.copy();
				u=new GridData(gridInfo);
				v=new GridData(gridInfo);
				
				for(int i = 0;i<gridInfo.nlon;i++) {
					for(int j = 0;j<gridInfo.nlat;j++) {		
						float[] uv = WindUtils.getUV(speed.dat[i][j],direction.dat[i][j]);						
						u.dat[i][j] =uv[0];
						v.dat[i][j] =uv[1];
					}
				}
				
				
			}
			else {
				System.out.println(file.getAbsolutePath()+"骞堕潪micaps椋庡満鏂囦欢");
			}
			
		} catch (Exception e) {
			// TODO 鑷姩鐢熸垚鐨� catch 鍧�
			System.out.println(e.getMessage());
			System.out.println(file.getAbsolutePath()+"璇诲彇澶辫触");
		}
	}
	public void writeToFile(String fileName,String time)  {
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
		File file=new File(fileName);
		File dir = file.getParentFile();
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		DecimalFormat datafmt = new DecimalFormat("0.000000");
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(file),"GBK");
			
			BufferedWriter br=new BufferedWriter(fos);
			String str;
		
			int end=file.getName().length();
			int start=Math.max(0, end-16);
			str="diamond 11 "+file.getName().substring(start, end)+"\n"+time.substring(0,4)+" "+time.substring(4,6)+" "
					+time.substring(6,8)+" "+time.substring(8,10)+" 000 "
						+gridInfo.dlon+" "+gridInfo.dlat+" "+gridInfo.startlon+" "+gridInfo.endlon+" "
						+gridInfo.startlat+" "+gridInfo.endlat+" "+gridInfo.nlon+" "+gridInfo.nlat;
	
			br.write(str);
			for (int j=0;j<gridInfo.nlat;j++){
				br.write("\n");
				for(int i=0;i<gridInfo.nlon;i++){
					if(i==0){
						br.write(datafmt.format(u.dat[i][j]));
					}
					else{
						br.write(" "+datafmt.format(u.dat[i][j]));
					}
				}
			}
			for (int j=0;j<gridInfo.nlat;j++){
				br.write("\n");
				for(int i=0;i<gridInfo.nlon;i++){
					if(i==0){
						br.write(datafmt.format(v.dat[i][j]));
					}
					else{
						br.write(" "+datafmt.format(v.dat[i][j]));
					}
				}
			}
			br.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			// TODO 鑷姩鐢熸垚鐨� catch 鍧�
			System.out.println(file.getAbsolutePath()+"鍐欏叆澶辫触");
		}
	}
}
