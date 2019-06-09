package recutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class StaData {
	
	public int nsta;
	public int nv;
	transient public float[][] dat;
	public StaData(int nsta0,int nv0){
		InitialData(nsta0,nv0);
	}
	

	
	public void InitialData(int nsta0,int nv0){
		nsta=nsta0;
		nv=nv0;
		dat=new float[nsta0][nv0];
		for(int i=0;i<dat.length;i++){
			for(int j=0;j<dat[0].length;j++){
				dat[i][j]=0;
			}
		}
	}
	


	public StaData setVar(int nv,int nv0, StaData sta0) {
		// TODO 自动生成的方法存根
		
		for(int i=0;i<nsta;i++){
			dat[i][nv]=9999;
			for(int j=0;j<sta0.nsta;j++){
				if(dat[i][0]==sta0.dat[j][0]){
					dat[i][nv]=sta0.dat[j][nv0];
				}
			}
		}
		return this;
	}
	
	public StaData copy(){
		StaData sta=new StaData(this.nsta,this.nv);
		for(int i=0;i<this.nsta;i++){
			for(int j=0;j<sta.dat[0].length;j++){
				sta.dat[i][j]=this.dat[i][j];
			}
		}
		return sta;
	}
	
	public StaData(File file) throws Exception {

		// TODO 自动生成的构造函数存根
		String str;
		String[] strs;
		String zz="\\s+";
		Pattern pat=Pattern.compile(zz);
		try {
			FileReader fis=new FileReader(file);
			BufferedReader br=new BufferedReader(fis);
			str=br.readLine();
			str = str.trim();
			strs = pat.split(str);
			int fileType = Integer.parseInt(strs[1]);
			if(fileType ==2) {
				str=br.readLine().trim();
				strs=pat.split(str);
				int y=Integer.parseInt(strs[0]);
				int m=Integer.parseInt(strs[1]);
				int d=Integer.parseInt(strs[2]);
				int h=Integer.parseInt(strs[3]);
				int lev=Integer.parseInt(strs[4]);
				nsta=Integer.parseInt(strs[5]);
				InitialData(nsta,10);
				
				for(int i=0;i<nsta;i++){
					
					try{
						str=br.readLine().trim();
						strs=pat.split(str);
					}catch(Exception e){
						break;
					}
					int length=Math.min(this.nv,strs.length);
					for (int j=0;j<length;j++){
						try{
							dat[i][j]=Float.parseFloat(strs[j]);
						}catch(Exception e){
							for(int jj=3;jj<this.nv;jj++){
								dat[i][jj]=9999;
							}
							break;
						}
					}
				}
			}
			else {
				System.out.println("输入文件格式不兼容");
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			throw new Exception(file.getAbsolutePath()+"文件格式异常");
		}

		
	}
	public StaData(String fileName) throws Exception{
		this(new File(fileName));
	}
	

    
}
