import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class VectorData {
	public GridInfo gridInfo;
	public GridData u,v;
	public VectorData(int nlon,int nlat,float startlon,float startlat,float dlon,float dlat){
		gridInfo=new GridInfo(nlon,nlat,startlon,startlat,dlon,dlat);
		u=new GridData(gridInfo);
		v=new GridData(gridInfo);
	}
	
	public VectorData(GridInfo gridInfo0) {
		// TODO 自动生成的方法存根
		gridInfo=gridInfo0.copy();
		u=new GridData(gridInfo);
		v=new GridData(gridInfo);
	}
	
	public VectorData copy() {
		// TODO 自动生成的方法存根
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
		// TODO 自动生成的构造函数存根
		// TODO 自动生成的构造函数存根
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
			this.gridInfo=u.gridInfo.copy();
			
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			System.out.println(file.getAbsolutePath()+"读取失败");
		}
	}
	public void writeToFile(String fileName)  {
		// TODO 自动生成的方法存根
		File file=new File(fileName);
		DecimalFormat datafmt = new DecimalFormat("0.000000");
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(file),"GBK");
			
			BufferedWriter br=new BufferedWriter(fos);
			String str;
		
			int end=file.getName().length();
			int start=Math.max(0, end-16);
			str="diamond 11 "+file.getName().substring(start, end)+"\n"+2010+" "+01+" "+01+" "+8+" 000 "
						+gridInfo.dlon+" "+gridInfo.dlat+" "+gridInfo.startlon+" "+gridInfo.endlon+" "
						+gridInfo.startlat+" "+gridInfo.endlat+" "+gridInfo.nlon+" "+gridInfo.nlat;
	
			br.write(str);
			int k;
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
			// TODO 自动生成的 catch 块
			System.out.println(file.getAbsolutePath()+"写入失败");
		}
	}
}
