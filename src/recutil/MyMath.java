package recutil;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class MyMath {
	public static float pi = 3.1415926f;
	public static float earthR=6371000.0f;
	public static float earthRotateW =  2*pi/(24*3600);
	public static float oneDegreeDis = earthR*pi/180.0f;
	
	public static float getF(float lat){
		return (float) (2*earthRotateW * Math.sin(lat*pi/180));
	}
	
	public static String getFileNameFromCalendar(Calendar calendar) {
		// TODO Auto-generated method stub
    	Date date=calendar.getTime();
    	String strDate=String.format("%tF", date);
        String strTime=String.format("%tR", date);
        String strDati=strDate.replace("-","")+strTime.replace(":","");
        return strDati;
	}

	public static int cycleIndex(boolean iscycle,int totalCount,int start,int move){
		if(iscycle){
			int i=(start+move+totalCount)%totalCount;
			if (i<0){
				i=i+totalCount;
			}
			return i;
		}
		else{
			int i=start+move;
			if(i>-1&&i<totalCount){
				return i; 
			}
			else if(i<0){
				return 0;
			}
			else{
				return totalCount-1;
			}
		}
	}

	
	public static float dis(float ax,float ay,float bx,float by){
		return (float) Math.sqrt(dis2(ax,ay,bx,by));
	};
	public static float dis2(float ax,float ay,float bx,float by){
		float sr=(float)Math.cos(ay*3.14/180);
		float d1=(ax-bx)*sr;
		float d2=ay-by;
		float dis2=d1*d1+d2*d2;
		return dis2;
	};

	public static float max(float[][] x){
		float vmax=(float) -1e9;
		for (int i=0;i<x.length;i++){
			for(int j=0;j<x[0].length;j++){
				if(vmax<x[i][j]&&x[i][j]!=9999){
					vmax=x[i][j];
				}
			}
		}
		return vmax;
	}
	public static float min(float[][] x){
		float vmin=(float) 1e9;
		for (int i=0;i<x.length;i++){
			for(int j=0;j<x[0].length;j++){
				if(vmin>x[i][j]&&x[i][j]!=9999){
					vmin=x[i][j];
				}
			}
		}
		return vmin;
	}
	
    public static void sort(float[][] ob, final int[] order) {    
        Arrays.sort(ob, new Comparator<Object>() {    
            public int compare(Object o1, Object o2) {    
                float[] one = (float[]) o1;    
                float[] two = (float[]) o2;    
                for (int i = 0; i < order.length; i++) {    
                    int k = order[i];    
                    if (one[k] > two[k]) {    
                        return 1;    
                    } else if (one[k] < two[k]) {    
                        return -1;    
                    } else {    
                        continue;
						/**
						 *如果按一条件比较结果相等，就使用第二个条件进行比较。
						 */

					}
                }    
                return 0;    
            }    
        });   
    }

	public static float getMeanHeight(int level) {
		// TODO Auto-generated method stub
		if(level == 925){
			return 750;
		}
		else if(level == 850){
			return 1500;
		}
		else if(level == 700){
			return 3000;
		}
		else if(level == 500){
			return 5500;
		}
		else if(level == 200){
			return 12000;
		}
		else if(level ==100){
			return 16500;
		}
		else {
			return 100;
		}
	}  
}

