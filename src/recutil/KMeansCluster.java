package recutil;


import java.util.ArrayList;
import java.util.List;

public class KMeansCluster
{
    // 聚类中心数
    public int k = 5;

    // 迭代最大次数
    public int maxIter = 50;

    // 测试点集
    public ArrayList<float[]> points;

    // 中心点
    public ArrayList<float[]> centers;

    public static final double MINDISTANCE = 100000000.00;

    public KMeansCluster(int k, int maxIter, ArrayList<float[]> points0) {
        this.k = k;
        this.maxIter = maxIter;
        this.points = new ArrayList<float[]>();
        for(int i=0;i<points0.size();i++) {
        	float[] point = new float[points0.get(i).length+1];
        	point[0] = 0;
        	for(int j=1;j<point.length;j++) {
        		point[j] = points0.get(i)[j-1];
        	}
        	this.points.add(point);
        }
        

        //初始化中心点
        initCenters();
    }

    /*
     * 初始化聚类中心
     * 这里的选取策略是，从点集中按序列抽取K个作为初始聚类中心
     */
    public void initCenters()
    {
        centers = new ArrayList<float[]>();
        
        int num = points.size()/k;
        num = 2;
        for (int i = 0; i < k; i++)
        {
            float[] tmPoint = points.get(i * num);
            float[] center = new float[tmPoint.length];
            center[0] = i;
            for(int j=1;j<tmPoint.length;j++) {
            	center[j] = tmPoint[j];
            }
            centers.add(center);
        }
    }


    /*
     * 停止条件是满足迭代次数
     */
    public void runKmeans()
    {
        // 已迭代次数
        int count = 1;

        while (count++ <= maxIter)
        {
            // 遍历每个点，确定其所属簇
            for (float[] point : points)
            {
                assignPointToCluster(point);
            }

            //调整中心点
            adjustCenters();
            
            float mdis = meanDisToCenters();
            System.out.println("第"+count+"次迭代后特征矢量距离分类中心的平均距离:" + mdis);
        }
    }



    /*
     * 调整聚类中心，按照求平衡点的方法获得新的簇心
     */
    public void adjustCenters()
    {
    
        // 更新簇心坐标
        for (int i = 0; i < k; i++)
        {
        	float[] tmpPoint = new float[centers.get(0).length];
        	tmpPoint[0] = i;
        	int count = 0;
        	for(float[] point : points) {
        		int clusterID = (int)point[0];
        		if(clusterID == i) {
        			for(int j = 2;j< tmpPoint.length;j++) {
        				tmpPoint[j] += point[j];
        			}
        			count++;
        		}
        	}
    		for(int j = 2;j< tmpPoint.length;j++) {
				tmpPoint[j] /= count++;
			}
            centers.set(i, tmpPoint);
        }
    }


    /*划分点到某个簇中，欧式距离标准
     * 对传入的每个点，找到与其最近的簇中心点，将此点加入到簇
     */
    public void assignPointToCluster(float[] point)
    {
        double minDistance = MINDISTANCE;

        float clusterID = -1;

        for (float[] center : centers)
        {
            double dis = EurDistance(point, center);
            if (dis < minDistance)
            {
                minDistance = dis;
                clusterID = center[0];
            }
        }
        point[0] = clusterID;

    }

    //欧式距离，计算两点距离
    public double EurDistance(float[] point, float[] center)
    {
    	float dis2 = 0;
    	for(int j= 2;j<point.length;j++) {
    		dis2 += (point[j] - center[j]) * (point[j] - center[j]);
    	}
        return Math.sqrt(dis2);
    }
    
    
    
    /* 
     * 计算点集到中心点的平均距离
     */
    public float meanDisToCenters() {
    	float mdis = 0;
    	
    	for(float[] point : points) {
    		float dis2 = 0;
    		int clusterID = (int)point[0];
    		float[] center = centers.get(clusterID);
			for(int j = 2;j< point.length;j++) {
				dis2 += (point[j] - center[j]) * (point[j] - center[j]);
			}
			dis2 /= (point.length-2);
    		mdis += dis2;
    	}
    	mdis /= points.size();
    	mdis = (float) Math.sqrt(mdis);
       
    	return mdis;
    }
}
