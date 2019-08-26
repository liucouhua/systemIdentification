package recutil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

public class SHighLowPressure {
	public static WeatherSystems getHLCentres(GridData grid, int level, float scale,float grade_set) {
	
		float maxValue = grid.max()+1;
		float minValue = grid.min()-1;
		GridData pgrid = grid.add(-minValue);  //  
		GridData mgrid = grid.mutiply(-1).add(maxValue);  
		//pgrid.writeToFile("G:/data/systemIdentify/pgrid.txt");
		//mgrid.writeToFile("G:/data/systemIdentify/mgrid.txt");

		GridData pid0 = SystemIdentification.getCuttedRegion(pgrid);  // �Ը�ѹ���ķ���
		GridData mid0 = SystemIdentification.getCuttedRegion(mgrid);  // �Ե�ѹ���ķ���
		pid0.writeToFile("D:/develop/java/201905-weahter_identification/output/low_surface/pid0.txt");

		
		GridData pid1 = mergeSmallCentre(pgrid,pid0,scale,grade_set);  // ��С�߶ȸ�ѹ���ϲ�

		GridData mid1 = mergeSmallCentre(mgrid,mid0,scale,grade_set);  // ��С�߶ȵ�ѹ���ϲ�
		

		
		GridData pid2 = getCentreArea(pgrid, pid1);         //��ø�ѹ�����1/3 ������
		GridData mid2 = getCentreArea(mgrid, mid1);          // ��ȡ��ѹ����1/3������

	
		GridData tId = seprateHighLow(pgrid,pid2,mid2);       // ���ߵ�ѹ���غϲ��ַָ�

		//mergeStrongConnecting(tId,3f);                           //�����ܿ����ĸߣ��ͣ�ѹ���ϲ�
		
		WeatherSystems hl= new WeatherSystems("HLPresure",level);   //��������ϵͳ
		hl.setValue(grid);  										//����ϵͳ��value��������Ϊ�߶ȳ�
		hl.setIds(tId);												// ������ϵͳ�ķ�����������Ϊids
		hl.reset();													// ��������ϵͳ�ķ���ids������ÿ���ߵ�ѹ�������ԣ���������λ�ã�ȡֵ��
		
		GridData lonMean = grid.lonmean();
		float mean,strenght;
		int jg,sjg,ejg,j;
		Set<Integer> keys = hl.features.keySet();
		for(Integer i:keys){
			SystemFeature sf = hl.features.get(i);
			jg = (int) ((sf.centrePoint.ptLat-grid.gridInfo.startlat)/grid.gridInfo.dlat);
			sjg = Math.max(0, jg-5);
			ejg = Math.min(jg+5,grid.gridInfo.nlat-1)+1;
			mean = 0;
			for(j=sjg;j<ejg;j++){
				mean += lonMean.dat[0][j]; 
			}
			mean /=(ejg-sjg);
			sf.features.put("strenght1", sf.centrePoint.ptVal-mean); //����ǿ��ָ��1,�Ըߣ��ͣ�����ֵ��ͬγ�ȴ���ƽ��ֵ��
			strenght = sf.centrePoint.ptVal*sf.features.get("area")-sf.features.get("strenght");
			sf.features.put("strenght",strenght);  // ����ǿ��ָ�������ߣ��ͣ�ѹ����ֵ-ȥ�ߣ��ͣ�ѹ����ƽ��ֵ��*�����  
			//sf.centrePoint.ptVal = strenght;    //Ϊ����鿴���㣬�����ĵ�ֵȡΪstrength
		}
		hl.type = "高低压";
		return hl;
	}
	
	

	static GridData mergeSmallCentre(GridData grid0, GridData id, float scale,float grade_set) {
		// TODO Auto-generated method stub
		//����ϲ����Ӷȳ�����ֵ��Ŀ��
		GridData ids = id.copy();
		int oldId,newId,startI,startJ;
		boolean changged =true;
		float mindis=9999;
		int minId=0;
		while(true){
			//��ȡ���ĵ�����Ե���ֵ��ľ���
			mindis = 9999;
			minId=0;
			Map<Integer,Map<String,Float>> map = getCentreAndEdgeMax(grid0,ids);		
			for (Map.Entry<Integer,Map<String,Float>> entry : map.entrySet()){
				Map<String,Float> map1 = entry.getValue();
				//dis = map1.get("scale");
				if(map1.get("scale") < mindis){
					mindis = map1.get("scale");
					minId= entry.getKey();
				}
				
			}
			if(mindis<scale){
				oldId = minId;
				newId = map.get(oldId).get("edgeId").intValue();
				startI= map.get(oldId).get("centreI").intValue();
				startJ= map.get(oldId).get("centreJ").intValue();
				if(newId ==0){
					System.out.println(oldId);
				}
				SystemIdentification.resetId(ids,oldId,newId,startI,startJ);
			}
			else{
				break;
			}
		}
		
		
		float min_dvalue = 9999;
		float dvalue;
		float value1 = 0,value2 = 0;
		int grade1,grade2;
		while(true){
			//��ȡ���ĵ�����Ե���ֵ��ľ���
			min_dvalue = 9999;
			minId=0;
			Map<Integer,Map<String,Float>> map = getCentreAndEdgeMax(grid0,ids);
			for (Map.Entry<Integer,Map<String,Float>> entry : map.entrySet()){
				Map<String,Float> map1 = entry.getValue();			
				value1 = map1.get("edgeMax") ;
				value2 = map1.get("centreMax");
				grade1 = (int)(value1/grade_set);
				grade2 = (int)(value2/grade_set);
				
				if(grade1 == grade2) {
					dvalue = Math.abs(value2 - value1);
					if(dvalue < min_dvalue){
						min_dvalue = dvalue;
						minId= entry.getKey();
					}
				}
			}
			if(min_dvalue<grade_set){
				value1 = map.get(minId).get("centreMax");
				int id2 = map.get(minId).get("edgeId").intValue();
			
				value2 = map.get(id2).get("centreMax");
				
				if(value1 < value2) {		
					oldId = minId;
					newId = id2;
				}
				else {
					oldId = id2;
					newId = minId;
				}

				startI= map.get(oldId).get("centreI").intValue();
				startJ= map.get(oldId).get("centreJ").intValue();
				
				if(newId ==0){
					System.out.println(oldId);
				}
				SystemIdentification.resetId(ids,oldId,newId,startI,startJ);
			}
			
			else{
				break;
			}
		}
		
		
		SystemIdentification.smoothIds(ids);
		return ids;
	}

	private static void mergeStrongConnecting(GridData tId, float f) {
		// ���������ڵĸ�ѹ����ѹ�����кϲ����ж�������������ѹ����ѹ��֮��ı߽���^2/�����С�� > ��ֵ
		//
		GridData id2 = new GridData(tId.gridInfo);
		GridData mid2 = new GridData(tId.gridInfo);
		for(int i=0 ;i<tId.gridInfo.nlon;i++){
			for(int j=0; j<tId.gridInfo.nlat;j++){
				if(tId.dat[i][j]>0) id2.dat[i][j] = tId.dat[i][j];
				if(tId.dat[i][j]<0) mid2.dat[i][j]= -tId.dat[i][j];
			}
		}
		GridData id3 = SystemIdentification.combineStrongConnectingRegion_2d(id2, f);
		GridData mid3 = SystemIdentification.combineStrongConnectingRegion_2d(mid2, f);
	
		for(int i=0 ;i<tId.gridInfo.nlon;i++){
			for(int j=0; j<tId.gridInfo.nlat;j++){
				tId.dat[i][j] = id3.dat[i][j] -mid3.dat[i][j];
			}
		}
	}

	
	private static Map<Integer, Map<String, Float>> getCentreAndEdgeMax(GridData grid, GridData ids) {
		// ��һ���ߣ��ͣ�ѹ���ľ�����һ���ߣ��ͣ�ѹ���������<scale ʱ���轫�䲢�������ߣ��ͣ�ѹ����Ϊ�������øߣ��ͣ�ѹ���ľ���߽���������
		Map<Integer,Map<String,Float>> idPros = new HashMap<Integer,Map<String,Float>>();
		int id,i1,j1,id1;
		int nlon = ids.gridInfo.nlon;
		int nlat = ids.gridInfo.nlat;
		float slat = grid.gridInfo.startlat;
		float slon = grid.gridInfo.startlon;
		float dlat = grid.gridInfo.dlat;
		float dlon = grid.gridInfo.dlon;
		float dis = 0,cx,cy,ex,ey;
		Map<String,Float> idPro =null;
		for(int i=0;i<nlon;i++){
			for(int j=0;j<nlat;j++){
				id = (int) ids.dat[i][j];
				if(id!=0){
					idPro =null;				
					if(idPros.containsKey(id)){
						idPro = idPros.get(id);
					}
					else{
						idPro=new HashMap<String,Float>();
						idPro.put("centreMax", 0.0f);
						idPro.put("centreI", 0.0f);
						idPro.put("centreJ", 0.0f);
						idPro.put("edgeMax",0.0f);
						idPro.put("edgeId", 0.0f);
						idPro.put("edgeI", 0.0f);
						idPro.put("edgeJ", 0.0f);
						idPro.put("scale", 9999.0f);
						idPros.put(id, idPro);
					}
					
					if(grid.dat[i][j]>idPro.get("centreMax")){
						idPro.put("centreMax", grid.dat[i][j]);
						idPro.put("centreI", (float)i);
						idPro.put("centreJ", (float)j);
					}
					//map_line.put(0,area+grid0.dat[i][j]);
					
				    for(int p=-1;p<2;p++){
			        	for(int q=-1;q<2;q++){
			        		if(Math.abs(p)+Math.abs(q)!=1) continue;
			        		i1= MyMath.cycleIndex(false, nlon, i, p);
			        		j1= MyMath.cycleIndex(false, nlat, j, q);
			        		id1 = (int) ids.dat[i1][j1];
			        		if(id1 !=0 && id1 != id){
			        			if(grid.dat[i1][j1]>idPro.get("edgeMax")){
			        				idPro.put("edgeMax",grid.dat[i1][j1]);
									idPro.put("edgeId", (float)id1);
									idPro.put("edgeI", (float)i1);
									idPro.put("edgeJ", (float)j1);
			        			}
			        		}
			        	}
				    }
				}
			}
		}	
		Set<Integer> keys = idPros.keySet();
		for ( Integer key : keys){
			idPro = idPros.get(key);
			cx = slon + dlon * idPro.get("centreI");
			cy = slat + dlat * idPro.get("centreJ");
			ex = slon + dlon * idPro.get("edgeI");
			ey = slat + dlat * idPro.get("edgeJ");
			dis = MyMath.dis(cx, cy, ex, ey);
			idPro.put("scale", dis);	
		}
		
		return idPros;
	}

	
	public static Map<Integer,Map<Integer,Float>> getRegionAreaAndConnectingId_2d(GridData grid0,GridData ids){
		Map<Integer,Map<Integer,Float>> map_area = new HashMap<Integer,Map<Integer,Float>>();
		int id,i1,j1,id1;
		int nlon = ids.gridInfo.nlon;
		int nlat = ids.gridInfo.nlat;
	
		for(int i=0;i<nlon;i++){
			for(int j=0;j<nlat;j++){
				id = (int) ids.dat[i][j];
				if(id!=0){
					Map<Integer,Float> map_line =new HashMap<Integer,Float>();
					if(map_area.containsKey(id)){
					    map_line = map_area.get(id);
					}
					float area = 0;
					if(map_line.containsKey(0))area = map_line.get(0);
					map_line.put(0,area+1.0f);
					
					if(map_line.containsKey(-1)){
						map_line.put(-1, Math.max(grid0.dat[i][j],map_line.get(-1)));
						map_line.put(-3, (float)i);
						map_line.put(-2, (float)j);
					}
					else{
						map_line.put(-1, grid0.dat[i][j]);
						map_line.put(-3, (float)i);
						map_line.put(-2, (float)j);
					}
					
					
					//map_line.put(0,area+grid0.dat[i][j]);
					Map<Integer,Integer> map_point=new HashMap<Integer,Integer>();
				    for(int p=-1;p<2;p++){
			        	for(int q=-1;q<2;q++){
			        		if(Math.abs(p)+Math.abs(q)!=1) continue;
			        		i1= MyMath.cycleIndex(false, nlon, i, p);
			        		j1= MyMath.cycleIndex(false, nlat, j, q);
			        		id1 = (int) ids.dat[i1][j1];
			        		if(id1 !=0 && id1 != id){
			        			if(map_point.containsKey(id1)){
			        				map_point.put(id1, map_point.get(id1)+1);
			        			}
			        			else{
			        				map_point.put(id1,1);
			        			}
			        		}
			        	}
			        }
				    for (Map.Entry<Integer,Integer> entry : map_point.entrySet()){
				    	id1 = entry.getKey();
				    	float lenght =0;
				    	if(entry.getValue() == 1){
				    		lenght = 1.0f; //*grid0.dat[i][j];
				    	}
				    	else if(entry.getValue() == 2){
				    		lenght =1.414f; //*grid0.dat[i][j];
				    	}
				    	else {
				    		lenght = 2.0f; //*grid0.dat[i][j];
				    	}
				    	if(map_line.containsKey(id1)){
				    		map_line.put(id1, map_line.get(id1)+lenght);
				    	}
				    	else{
				    		map_line.put(id1,lenght);
				    	}
				    }
				    
				    map_area.put(id, map_line);
				}
			}
		}
		return map_area;
	}
	
	
	public static GridData getCentreArea(GridData grid, GridData id){
		int nlat=grid.gridInfo.nlat;
		int nlon=grid.gridInfo.nlon;

		GridData tId = new GridData (id.gridInfo);
		GridData sed = new GridData (id.gridInfo);
		ArrayList <float[]> ids = new ArrayList<float[]>();
		int pkey,i,j,k;
		int[] order = new int[]{2};
		for(i=0;i<id.gridInfo.nlon;i++){
			for(j=0;j<id.gridInfo.nlat;j++){
				if(sed.dat[i][j] == 0){
					pkey = (int) id.dat[i][j];
					ids = new ArrayList<float[]>();
					
					class GrowPoint{
						int i,j;
						public GrowPoint(int i0,int j0){
							i=i0;j=j0;
						}
					}
					Queue<GrowPoint> queue=new LinkedList<GrowPoint>();
					float[] onePoint = new float[3];
    				onePoint[0] = i;
    				onePoint[1] = j;
    				onePoint[2] = grid.dat[i][j];
    				ids.add(onePoint);
					queue.offer(new GrowPoint(i,j));
					GrowPoint gp;
					int i1,j1;
			        while((gp=queue.poll())!=null){
			        	//�Ӷ�����ȡ����һ�����������Ϊ�����ж���Χ8����
			        	for(int p=-1;p<2;p++){
			        		i1=MyMath.cycleIndex(false, nlon,gp.i,p);
			        		for(int q=-1;q<2;q++){
			        			j1=MyMath.cycleIndex(false, nlat,gp.j,q);
			        			if((int)id.dat[i1][j1]==pkey&& sed.dat[i1][j1]==0){
			        				//�����Χ8��������δ���޸ĵĵ㣬�����������������Χ����ӵ�������
			        				float[] onePoint1 = new float[3];
			        				onePoint1[0] = i1;
			        				onePoint1[1] = j1;
			        				onePoint1[2] = grid.dat[i1][j1];
			        				sed.dat[i1][j1] =1;
			        				ids.add(onePoint1);
			        				queue.offer(new GrowPoint(i1,j1));
			        			}
			        		}
			        	}
			        }
			        float[][] sortIds = new float[ids.size()][];
			        for(k = 0;k<sortIds.length; k++){
			        	sortIds[k] = ids.get(k);
			        }
			        MyMath.sort(sortIds, order);
					for(k = 0; k < sortIds.length; k++){
						i1 = (int)sortIds[k][0];
						j1 = (int)sortIds[k][1];
						if(k>2*sortIds.length/3) {
							tId.dat[i1][j1] = id.dat[i1][j1];
						}
					}
					//����һƬ��ֵ
				}
			}
		}
		
		
		GridData ccId = new GridData (id.gridInfo);
		sed = new GridData (id.gridInfo);
		ids = new ArrayList<float[]>();

		for(i=0;i<id.gridInfo.nlon;i++){
			for(j=0;j<id.gridInfo.nlat;j++){
				if(sed.dat[i][j] == 0){
					ids = new ArrayList<float[]>();
					
					class GrowPoint{
						int i,j;
						public GrowPoint(int i0,int j0){
							i=i0;j=j0;
						}
					}
					Queue<GrowPoint> queue=new LinkedList<GrowPoint>();
					float[] onePoint = new float[3];
    				onePoint[0] = i;
    				onePoint[1] = j;
    				onePoint[2] = grid.dat[i][j];
    				ids.add(onePoint);
					queue.offer(new GrowPoint(i,j));
					GrowPoint gp;
					int i1,j1;
			        while((gp=queue.poll())!=null){
			        	//�Ӷ�����ȡ����һ�����������Ϊ�����ж���Χ8����
			        	pkey = (int) id.dat[gp.i][gp.j];
			        	for(int p=-1;p<2;p++){
			        		i1=MyMath.cycleIndex(false, nlon,gp.i,p);
			        		for(int q=-1;q<2;q++){
			        			j1=MyMath.cycleIndex(false, nlat,gp.j,q);
			        			if(((int)id.dat[i1][j1]==pkey||(tId.dat[gp.i][gp.j]>0&&tId.dat[i1][j1]>0))&& sed.dat[i1][j1]==0){
			        				//�����Χ8��������δ���޸ĵĵ㣬�����������������Χ����ӵ�������
			        				float[] onePoint1 = new float[3];
			        				onePoint1[0] = i1;
			        				onePoint1[1] = j1;
			        				onePoint1[2] = grid.dat[i1][j1];
			        				sed.dat[i1][j1] =1;
			        				ids.add(onePoint1);
			        				queue.offer(new GrowPoint(i1,j1));
			        			}
			        		}
			        	}
			        }
			        float[][] sortIds = new float[ids.size()][];
			        for(k = 0;k<sortIds.length; k++){
			        	sortIds[k] = ids.get(k);
			        }
			        MyMath.sort(sortIds, order);
					for(k = 0; k < sortIds.length; k++){
						i1 = (int)sortIds[k][0];
						j1 = (int)sortIds[k][1];
						if(k>2*sortIds.length/3) {
							ccId.dat[i1][j1] = id.dat[i1][j1];
						}
					}
					//����һƬ��ֵ
				}
			}
		}
		
		
		return ccId;
	}
	
	public static GridData seprateHighLow(GridData grid, GridData id, GridData mid) {
		// TODO Auto-generated method stub
		int nlat=grid.gridInfo.nlat;
		int nlon=grid.gridInfo.nlon;
		GridData tId = new GridData (id.gridInfo);
		GridData sed = new GridData (id.gridInfo);
		ArrayList <float[]> ids = new ArrayList<float[]>();
		int pkey,mkey,i,j,k;
		int[] order = new int[]{2};
		for(i=0;i<id.gridInfo.nlon;i++){
			for(j=0;j<id.gridInfo.nlat;j++){
				if(sed.dat[i][j] == 0){
					pkey = (int) id.dat[i][j];
					mkey = (int) mid.dat[i][j];
					if(pkey==0 ){
						sed.dat[i][j] = 1;
						tId.dat[i][j] = -mid.dat[i][j];
						continue;
					}
					if(mkey==0 ){
						sed.dat[i][j] = 1;
						tId.dat[i][j] = id.dat[i][j];
						continue;
					}
					ids = new ArrayList<float[]>();
					
					class GrowPoint{
						int i,j;
						public GrowPoint(int i0,int j0){
							i=i0;j=j0;
						}
					}
					Queue<GrowPoint> queue=new LinkedList<GrowPoint>();
					float[] onePoint = new float[3];
    				onePoint[0] = i;
    				onePoint[1] = j;
    				onePoint[2] = grid.dat[i][j];
    				ids.add(onePoint);
					queue.offer(new GrowPoint(i,j));
					GrowPoint gp;
					int i1,j1;
			        while((gp=queue.poll())!=null){
			        	//�Ӷ�����ȡ����һ�����������Ϊ�����ж���Χ26����
			        	for(int p=-1;p<2;p++){
			        		i1=MyMath.cycleIndex(false, nlon,gp.i,p);
			        		for(int q=-1;q<2;q++){
			        			j1=MyMath.cycleIndex(false, nlat,gp.j,q);
			        			if((int)id.dat[i1][j1]==pkey &&(int)mid.dat[i1][j1]==mkey && sed.dat[i1][j1]==0){
			        				//�����Χ8��������δ���޸ĵĵ㣬�����������������Χ����ӵ�������
			        				float[] onePoint1 = new float[3];
			        				onePoint1[0] = i1;
			        				onePoint1[1] = j1;
			        				onePoint1[2] = grid.dat[i1][j1];
			        				sed.dat[i1][j1] =1;
			        				ids.add(onePoint1);
			        				queue.offer(new GrowPoint(i1,j1));
			        			}
			        		}
			        	}
			        }
			        float[][] sortIds = new float[ids.size()][];
			        for(k = 0;k<sortIds.length; k++){
			        	sortIds[k] = ids.get(k);
			        }
			        MyMath.sort(sortIds, order);
					for(k = 0; k < sortIds.length; k++){
						i1 = (int)sortIds[k][0];
						j1 = (int)sortIds[k][1];
						if(k<sortIds.length/2){
							tId.dat[i1][j1] = -mid.dat[i1][j1];
						}
						else  {
							tId.dat[i1][j1] = id.dat[i1][j1];
						}
					}
					//����һƬ��ֵ
				}
			}
		}
		//�������е㸳ֵ
	    //smooth id
		int i1,j1;
	    for (k=0;k<2;k++){
		    GridData smId = new GridData(tId.gridInfo);
		    for(i=0;i<nlon;i++){
				for(j=0;j<nlat;j++){
					Map<Integer,Integer> idNum=new HashMap<Integer,Integer>();
					 for(int p=-4;p<5;p++){
				        	for(int q=-4;q<5;q++){
				        		i1= MyMath.cycleIndex(false, nlon, i, p);
				        		j1= MyMath.cycleIndex(false, nlat, j, q);
				        		int key =(int)tId.dat[i1][j1];
				        		if(idNum.containsKey(key)){
				        			idNum.put(key, idNum.get(key)+1);
				        		}
				        		else{
				        			idNum.put(key, 1);
				        		}
				        	}
				    }
					int maxNum = 1;
					
					for (Map.Entry<Integer,Integer> entry : idNum.entrySet()){
						if(entry.getValue()>maxNum){
							maxNum = entry.getValue();
							smId.dat[i][j] =entry.getKey();
						}
					}
					 
				}
		    }
		    tId = smId.copy();
	    }
		
		return tId;
	}
}
