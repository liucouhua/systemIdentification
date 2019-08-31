package recutil;
import java.awt.GridLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.regex.Pattern;

import recutil.GridInfo;
import recutil.Sta4Data;
import recutil.GridDataModel;

public class GridData {
//	public String name;
	public GridInfo gridInfo;
	transient public   float[][] dat;
	
	
	public GridData(GridDataModel model){
		gridInfo=new GridInfo(model);
		dat=new float[gridInfo.nlon][gridInfo.nlat];
		
		for(int i=0;i<gridInfo.nlon;i++){
			for(int j=0;j<gridInfo.nlat;j++){
				int index = 0;
				//鍒ゆ柇鏁版嵁鏄珮绾害鍒颁綆绾害杩樻槸鍏朵粬鎺掑垪鐨�
				if(model.getLatstep()<0){
					index = (gridInfo.nlat-j-1)*gridInfo.nlon+i;
				}else{
					index = j*gridInfo.nlon+i;
				}
				dat[i][j]=model.getDatas()[index];
			}
		}
	}
	
	public GridData(int nlon,int nlat,float startlon,float startlat,float dlon,float dlat){
		gridInfo=new GridInfo(nlon,nlat,startlon,startlat,dlon,dlat);
		InitialData();
	}
	public GridData(GridInfo gridInfo0){
		gridInfo=new GridInfo(gridInfo0.nlon,gridInfo0.nlat,gridInfo0.startlon,gridInfo0.startlat,gridInfo0.dlon,gridInfo0.dlat);
		InitialData();
	}

	
	
	
	private void InitialData() {
		// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
		dat=new float[gridInfo.nlon][gridInfo.nlat];
		for(int i=0;i<gridInfo.nlon;i++){
			for(int j=0;j<gridInfo.nlat;j++){
				dat[i][j]=0.0f;
			}
		}
	}
	
	public GridData copy(){
		GridData grid=new GridData(gridInfo);
		for(int i=0;i<grid.gridInfo.nlon;i++){
			for(int j=0;j<grid.gridInfo.nlat;j++){
				grid.dat[i][j]=this.dat[i][j];
			}
		}
		return grid;
	  }
	

	public GridData(Sta4Data sta4) throws Exception {
		// TODO 鑷姩鐢熸垚鐨勬瀯閫犲嚱鏁板瓨鏍�
		float slon,slat,elon,elat;
		float mindlon,mindlat;
		float dlon=360,dlat=360;
		float ddlon,ddlat;
		int ig,jg;
		slon=sta4.dat[0][1];
		slat=sta4.dat[0][2];
		elon=sta4.dat[0][1];
		elat=sta4.dat[0][2];
		for(int k=1;k<sta4.nsta;k++){
			if(sta4.dat[k][1]<slon)slon=sta4.dat[k][1];
			if(sta4.dat[k][1]>elon)elon=sta4.dat[k][1];
			if(sta4.dat[k][2]<slat)slat=sta4.dat[k][2];
			if(sta4.dat[k][2]>elat)elat=sta4.dat[k][2];
			mindlon=Math.abs(sta4.dat[k][1]-sta4.dat[k-1][1]);
			if(mindlon!=0&&mindlon<dlon)dlon=mindlon;
			mindlat=Math.abs(sta4.dat[k][2]-sta4.dat[k-1][2]);
			if(mindlat!=0&&mindlat<dlat)dlat=mindlat;
		}
		if(dlon==0||dlat==0)throw new Exception("绔欑偣搴忓垪涓嶆瀯鎴愮綉鏍�");
		for(int k=1;k<sta4.nsta;k++){
			ddlon=Math.abs(sta4.dat[k][1]-sta4.dat[k-1][1])/dlon;
			ddlat=Math.abs(sta4.dat[k][2]-sta4.dat[k-1][2])/dlat;
			if(ddlon!=(int)ddlon||ddlat!=(int)ddlat)throw new Exception("绔欑偣搴忓垪涓嶆瀯鎴愯鍒欑綉鏍�");
		}
		int nlon =(int)((elon-slon)/dlon+1);
		int nlat=(int)((elat-slat)/dlat+1);
		
		gridInfo=new GridInfo(nlon,nlat,slon,slat,dlon,dlat);
		InitialData();
		for(int k=0;k<sta4.nsta;k++){
			ig=(int) ((sta4.dat[k][1]-slon)/dlon);
			jg=(int) ((sta4.dat[k][2]-slat)/dlat);
			//System.out.println(ig);
			//System.out.println(jg);
			
			dat[ig][jg]=sta4.dat[k][3];
		}
	}
	
	
	public void linearIntepolatedFrom(GridData grid) {
		// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
		float slon=gridInfo.startlon;
		float slat=gridInfo.startlat;
		float dlon=gridInfo.dlon;
		float dlat=gridInfo.dlat;
		int nlon=gridInfo.nlon;
		int nlat=gridInfo.nlat;
		
		
		float slon1=grid.gridInfo.startlon;
		float slat1=grid.gridInfo.startlat;
		float dlon1=grid.gridInfo.dlon;
		float dlat1=grid.gridInfo.dlat;
		int nlat1=grid.gridInfo.nlat;
		int nlon1=grid.gridInfo.nlon;
		
		float lon,lat,dx,dy,c00,c01,c10,c11;
		int ig,jg,ig1,jg1;
		boolean iscycle = false;
		if(nlon1*dlon1>=360)iscycle=true;
		
		////System.out.println(nlon+" "+nlat);
		this.setValue(9999);
		for(int i=0;i<nlon;i++){
			lon=slon+i*dlon;
			ig=(int)((lon-slon1)/dlon1);
			dx=(lon-slon1)/dlon1-ig;
			if(ig>=0&&ig<nlon1-1){
				ig1=ig+1;
			}
			else if(iscycle){
				ig=MyMath.cycleIndex(iscycle, nlon1, ig, 0);
				ig1=MyMath.cycleIndex(iscycle, nlon1, ig, 1);
			}
			else if(ig==nlon1-1&&dx==0){
				ig1=ig;
			}
			else{
			//	//System.out.println(i+" "+lon+" "+ig+" "+dx);
				continue;
			}
			
			for(int j=0;j<nlat;j++){
				lat=slat+j*dlat;
				jg=(int)((lat-slat1)/dlat1);
				dy=(lat-slat1)/dlat1-jg;
				if(jg>=0&&jg<nlat1-1){
					jg1=jg+1;
				}
				else if(jg==nlat1-1&&dy==0){
					jg1=jg;
				}
				else{
			//		//System.out.println(j+" "+lat+" "+jg+" "+dy);
					
					continue;
				}
				c00=(1-dx)*(1-dy);
				c01=dx*(1-dy);
				c10=(1-dx)*dy;
				c11=dx*dy;
				////System.out.println(ig+" "+jg+" "+c00+" "+grid.dat[ig][jg]);
				if(c00!=0&&grid.dat[ig][jg]==9999||c01!=0&&grid.dat[ig1][jg]==9999||c10!=0&&grid.dat[ig][jg1]==9999||c11!=0&&grid.dat[ig1][jg1]==9999){
					dat[i][j]=9999;
				}
				else{
					dat[i][j]=c00*grid.dat[ig][jg]+c01*grid.dat[ig1][jg]+c10*grid.dat[ig][jg1]+c11*grid.dat[ig1][jg1];	
				}
			}
		}
	}


	public void reSetXY(){
		float tran;
		if(gridInfo.dlon<0){
			tran=gridInfo.startlon;
			gridInfo.startlon=gridInfo.endlon;
			gridInfo.endlon=tran;
			gridInfo.dlon=-gridInfo.dlon;
			int lastI=gridInfo.nlon-1;
			for(int i=0;i<gridInfo.nlon/2;i++){
				for(int j=0;j<gridInfo.nlat;j++){
					tran=dat[i][j];
					dat[i][j]=dat[lastI-i][j];
					dat[lastI-i][j]=tran;
				}
			}
		}
		if(gridInfo.dlat<0){
			tran=gridInfo.startlat;
			gridInfo.startlat=gridInfo.endlat;
			gridInfo.endlat=tran;
			gridInfo.dlat=-gridInfo.dlat;
			int lastJ=gridInfo.nlat-1;
			for(int i=0;i<gridInfo.nlon;i++){
				for(int j=0;j<gridInfo.nlat/2;j++){
					tran=dat[i][j];
					dat[i][j]=dat[i][lastJ-j];
					dat[i][lastJ-j]=tran;
				}
			}
		}
	}

	
	public void reSetResolution() {
		int nlon1 = (int) ((gridInfo.nlon-1) * gridInfo.dlon /0.5) + 1;
		int nlat1 = (int)((gridInfo.nlat-1) * gridInfo.dlat /0.5) + 1;
		GridData gridData1 =new  GridData(nlon1,nlat1,gridInfo.startlon,gridInfo.startlat,0.5f,0.5f);
		gridData1.linearIntepolatedFrom(this);
		gridInfo = gridData1.gridInfo;
		dat = gridData1.dat;
	}
	
	public void smooth(int smTimes) {
		// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
		GridData c=this.copy();
		for(int k=0;k<smTimes;k++){
			for(int i=1;i<gridInfo.nlon-1;i++){
				for(int j=1;j<gridInfo.nlat-1;j++){	
					c.dat[i][j]=0.25f*dat[i][j]+(dat[i+1][j]+dat[i-1][j]+dat[i][j-1]+dat[i][j+1])/8.0f+
						(dat[i+1][j+1]+dat[i-1][j-1]+dat[i+1][j-1]+dat[i-1][j+1])/16.0f;
					
				}
			}
			for(int i=1;i<gridInfo.nlon-1;i++){
				for(int j=1;j<gridInfo.nlat-1;j++){	
					dat[i][j]=c.dat[i][j];
				}
			}
		}
	}
	public void smooth(float maxRoughness) {
		// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
		boolean hasChange=true;
		int smtime=0;
		while(hasChange&&smtime<200){
				hasChange=false;
				for(int j=0;j<gridInfo.nlat;j++){
					float sr = 2*gridInfo.dlon*(float) Math.cos((gridInfo.startlat+gridInfo.dlat*j)*3.14/180);
					for(int i=1;i<gridInfo.nlon-1;i++){		
						float roughness=Math.abs(dat[i+1][j]+dat[i-1][j]-2*dat[i][j])/sr;
						if(roughness>maxRoughness){
							dat[i][j]=0.5f*dat[i][j]+0.25f*(dat[i+1][j]+dat[i-1][j]);
							hasChange=true;
						}
					}
				}
				for(int j=1;j<gridInfo.nlat-1;j++){
					float sr = 2*gridInfo.dlat;
					for(int i=0;i<gridInfo.nlon;i++){		
						float roughness=Math.abs(dat[i][j+1]+dat[i][j-1]-2*dat[i][j])/sr;
						if(roughness>maxRoughness){
							dat[i][j]=0.5f*dat[i][j]+0.25f*(dat[i][j+1]+dat[i][j-1]);
							hasChange=true;
						}
					}
				}
				smtime++;
			//	System.out.println(smtime);
		}
		return; 
	}
	

	public void setValue(float value){
		for(int i=0;i<gridInfo.nlon;i++){
			for(int j=0;j<gridInfo.nlat;j++){
					dat[i][j]=value;
			}
		}
	}
	
	public void setDefaultValue(float value){
		for(int i=0;i<gridInfo.nlon;i++){
			for(int j=0;j<gridInfo.nlat;j++){
					if(dat[i][j]==9999)dat[i][j]=value;
			}
		}
	}
	
	public GridData mutiply(GridData region) {
		// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
		GridData g=new GridData(gridInfo);
		g.linearIntepolatedFrom(region);
		for(int i=0;i<g.gridInfo.nlon;i++){
			for(int j=0;j<g.gridInfo.nlat;j++){
				g.dat[i][j]=g.dat[i][j]*this.dat[i][j];
				
			}
		}
		return g;
	}
	public GridData mutiply(float f){
		GridData gridF=new GridData(gridInfo);
		gridF.setValue(f);
		return this.mutiply(gridF);		
	}
	
	public GridData add(float addValue) {
		// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
		GridData grida=new GridData(gridInfo);
		grida.setValue(addValue);
		return this.add(grida);		
	}
	
	public GridData add(GridData addGrid) {
		// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
		GridData g=new GridData(gridInfo);
		g.linearIntepolatedFrom(addGrid);
		for(int i=0;i<g.gridInfo.nlon;i++){
			for(int j=0;j<g.gridInfo.nlat;j++){
				g.dat[i][j]=g.dat[i][j]+this.dat[i][j];
				
			}
		}
		return g;
	}
	
	public GridData sub(GridData subGrid)
	{
		// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
		GridData g=new GridData(gridInfo);
		g.linearIntepolatedFrom(subGrid);
		for(int i=0;i<g.gridInfo.nlon;i++){
			for(int j=0;j<g.gridInfo.nlat;j++){
				g.dat[i][j]=this.dat[i][j]-g.dat[i][j];			
			}
		}
		return g;
	}
	
	public float mean()
	{
		float output=0.0f;
		for(int i=0;i<gridInfo.nlon;i++)
		{
			for(int j=0;j<gridInfo.nlat;j++)
			{
				output=output+this.dat[i][j];				
			}
		}
		output=output/(gridInfo.nlon*gridInfo.nlat);
		return output;	
	}
	
	public float max()
	{
		float output=-999999f;
		for(int i=0;i<gridInfo.nlon;i++)
		{
			for(int j=0;j<gridInfo.nlat;j++)
			{
				if(this.dat[i][j]>output)output=this.dat[i][j];				
			}
		}
	
		return output;	
	}
	public float min()
	{
		float output=999999f;
		for(int i=0;i<gridInfo.nlon;i++)
		{
			for(int j=0;j<gridInfo.nlat;j++)
			{
				if(this.dat[i][j]<output)output=this.dat[i][j];				
			}
		}
		return output;	
	}
	
	//纬圈平锟斤拷
	public GridData lonmean()
	{
		GridData output=new GridData(this.gridInfo);
		for(int j=0;j<gridInfo.nlat;j++)
		{
			float datTemp=0.0f;
		    for(int i=0;i<gridInfo.nlon;i++)
		    {
				datTemp=datTemp+dat[i][j];		
			}
		    datTemp=datTemp/gridInfo.nlon;
			for(int i=0;i<gridInfo.nlon;i++)
		    {
				output.dat[i][j]=datTemp;
			}
		}
	    return output;
	}
	
	//纬锟饺达拷平锟斤拷
	public GridData lonmean(float bandWidth)
	{
		GridData output=new GridData(gridInfo);
		int jWidth=(int)(bandWidth/gridInfo.dlat);
		for(int j=0;j<gridInfo.nlat;j++)
		{
		    float datTemp=0.0f;
			int ctNum=0;
			for(int jj=j-jWidth;jj<j+jWidth;jj++)
			{
		        for(int i=0;i<gridInfo.nlon;i++)
		        {
				    datTemp=datTemp+dat[i][jj];	
				    ctNum=ctNum+1;
			    }
		       datTemp=datTemp/ctNum;
			}
			for(int i=0;i<gridInfo.nlon;i++)
		    {
				output.dat[i][j]=datTemp;
			}
		}
	    return output;	
	}
	
	
	public GridData(String fileName){
		// TODO 锟皆讹拷锟斤拷锟缴的癸拷锟届函锟斤拷锟斤拷锟�
		File file=new File(fileName);
		String str;
		String[] strs;
		String zz="\\s+";
		Pattern pat=Pattern.compile(zz);
		try {
			if(!file.exists())throw new Exception("锟侥硷拷"+file.getAbsolutePath()+"锟斤拷锟斤拷锟斤拷");
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
			gridInfo.dlon=Float.parseFloat(strs[9]);
			gridInfo.dlat=Float.parseFloat(strs[10]);
			gridInfo.startlon=Float.parseFloat(strs[11]);
			gridInfo.endlon=Float.parseFloat(strs[12]);
			gridInfo.startlat=Float.parseFloat(strs[13]);
			gridInfo.endlat=Float.parseFloat(strs[14]);		
			gridInfo.nlon=Integer.parseInt(strs[15]);
			gridInfo.nlat=Integer.parseInt(strs[16]);
			InitialData();
			int k=21;
			for (int j=0;j<gridInfo.nlat;j++){
				for(int i=0;i<gridInfo.nlon;i++){
					k=k+1;
					if(k>=strs.length)throw new Exception("锟侥硷拷锟斤拷小锟斤拷锟侥硷拷头锟斤拷锟斤拷锟斤拷锟斤拷");
					dat[i][j]=Float.parseFloat(strs[k]);
				}
			}
			reSetXY();
			reSetResolution();
		} catch (Exception e) {
			// TODO 锟皆讹拷锟斤拷锟缴碉拷 catch 锟斤拷
			System.out.println(file.getAbsolutePath()+"锟侥硷拷锟斤拷式锟届常");
		    
		} 
		
		
	}

	public GridData sign01() {
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
		GridData g=new GridData(gridInfo);
		for(int i=0;i<gridInfo.nlon;i++){
			for(int j=0;j<gridInfo.nlat;j++){
				if(dat[i][j]>0){
					g.dat[i][j]=1;
				}
				else{
					g.dat[i][j]=0;
				}
			}
		}
		return g;
	}
	
	
	public void writeToFile(String fileName,String header) {
		
		File file=new File(fileName);
		
		File dir = file.getParentFile();
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		float vmax=MyMath.max(dat);
		float vmin=MyMath.min(dat);
		
		float[][] out=new float[gridInfo.nlon][gridInfo.nlat];
		for(int i=0;i<gridInfo.nlon;i++){
			
			for (int j=0;j<gridInfo.nlat;j++){
				if(dat[i][j]==9999&&(dat[i][j]>vmax||dat[i][j]<vmin)){
					out[i][j]=2*vmin-vmax;
				}
				else{
					out[i][j]=dat[i][j];
				}
			}
		}
		
		
		if(vmax-vmin<1e-10){
			vmax=vmin+1.1f;
		}
		float dif=(vmax-vmin)/10.0f;
		float inte=(float) Math.pow(10,Math.floor(Math.log10(dif)));

		float r=dif/inte;
		if(r<3&&r>=1.5)			inte=inte*2;
		else if(r<4.5&&r>=3)	inte=inte*4;
		else if(r<5.5&&r>=4.5)  inte=inte*5;
		else if(r<7&&r>=5.5)	inte=inte*6;
		else if(r>=7)			inte=inte*8;     
		vmin=inte*((int)(vmin/inte)-1);
		vmax=inte*((int)(vmax/inte)+1);
		try {
			OutputStreamWriter fos= new OutputStreamWriter(new FileOutputStream(file),"GBK");
			BufferedWriter br=new BufferedWriter(fos);
			String str;
	
				int end=file.getName().length();
				int start=Math.max(0, end-16);
				str="diamond 4 "+ header+"\n2000 01 01 01 0 9999 "
						+gridInfo.dlon+" "+gridInfo.dlat+" "+gridInfo.startlon+" "+gridInfo.endlon+" "
						+gridInfo.startlat+" "+gridInfo.endlat+" "+gridInfo.nlon+" "+gridInfo.nlat+" "
						+inte+" "+vmin+" "+vmax+" 1 0";

			br.write(str);
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance();   
			nf.setGroupingUsed(false);  
			for (int j=0;j<gridInfo.nlat;j++){
				br.write("\n");
				for(int i=0;i<gridInfo.nlon;i++){
					br.write(nf.format(out[i][j])+" ");
				}
			}
			br.flush();
			fos.close();
		} catch (IOException e) {
			System.out.println(file.getAbsolutePath());
		}
	}
	
	public void writeToFile(String fileName) {
			int end=fileName.length();
			int start=Math.max(0, end-32);
			String header =  fileName.substring(start, end);
			writeToFile(fileName,header);
	}
}
