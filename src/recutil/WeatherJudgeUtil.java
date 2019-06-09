package recutil;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: zhangpan@mlogcn.com
 * @Date: 2018/12/10 16:42
 * @Version 1.0
 * @Description
 */
public class WeatherJudgeUtil {

    /**
     * 低空急流判断条件
     * @param p
     * @return
     */
    public static Set<Integer> getCodeByWind850SJET(float [] p)
    {
        Set<Integer> result=new HashSet<>();//获取存在的天气系统，重复的忽略，只要线的一个点在里边就算该系统存在
        if(p[0]>=110&&p[0]<=120&&p[1]>=25&&p[1]<=33)
        {
            result.add(Constants.FRONTAL_SJET);//
        }
        if(p[0]>=110&&p[0]<=120&&p[1]>=25&&p[1]<=30)
        {
            result.add(Constants.SOUTHWEST__SJET);//
        }
        return  result;
    }

    /**
     * 850风场 切变线识别
     * @param p
     * @return
     */
    public static Set<Integer> getCodeByWind850SHEAR(float [] p)
    {
        Set<Integer> result=new HashSet<>();
        if(p[0]>=110&&p[0]<=120&&p[1]>=30&&p[1]<=35)
        {
            result.add(Constants.FRONTAL_SHEAR_01);
            result.add(Constants.SOUTHWEST_SHEAR_01);
        }
//        if(p[0]>=-180&&p[0]<=180&&p[1]>=10&&p[1]<=90)
//        {
//            result.add(Constants.FRONTAL_SHEAR_01);
//            result.add(Constants.SOUTHWEST_SHEAR_01);
//        }
        if(p[0]>=110&&p[0]<=120&&p[1]>=30&&p[1]<=40)
        {
            result.add(Constants.NORTHEAST_SHEAR_02);
            result.add(Constants.SUMMER__SHEAR_03);
        }
        return  result;
    }

    /**
     * 850风场 涡识别
     * @param p
     * @return
     */
    public static Set<Integer> getCodeByWind850Vortex(float [] p)
    {
        Set<Integer> result=new HashSet<>();
        if(p[0]>=110&&p[0]<=115&&p[1]>=28&&p[1]<=32)
        {
            result.add(Constants.SOUTHWEST_JIANGHAN_VORTEX);
        }
        return  result;
    }

    /**
     * 500高度场获取的脊线
     * @param p
     * @return
     */
    public static Set<Integer> getCodeByHGT500_Ridge_Line(float[] p){
        Set<Integer> result=new HashSet<>();
        if(p[1]>=20&&p[1]<=25&&p[0]==120)
        {
            result.add(Constants.FRONTAL_XITAI_1_HIGH_PRESSURE);
            result.add(Constants.SOUTHWEST_XITAI_1_HIGH_PRESSURE);
            result.add(Constants.NORTHEAST_XITAI_1_HIGH_PRESSURE);
        }
        if(p[1]>=25&&p[1]<=28&&p[0]>=102&&p[0]<=112) {
            result.add(Constants.SUMMER_XITAI_1_HIGH_PRESSURE);
        }
        if(p[1]>=35&&p[1]<=40&&p[0]==120) {
            result.add(Constants.TYPHOON_XITAI_3_HIGH_PRESSURE);
        }

        return  result;
    }
    /**
     * 500高度场获取的槽线
     * @param p
     * @return
     */
    public static Set<Integer> getCodeByHGT500_Trough_Line(float[] p){
        Set<Integer> result=new HashSet<>();
        if(p[1]>=30&&p[1]<=40&&p[0]>=105&&p[0]<=115)
        {
            result.add(Constants.FRONTAL_HIGH_TROUGH_01_LINE);
        }
        if(p[1]>=30&&p[1]<=40&&p[0]>=105&&p[0]<=110)
        {
            result.add(Constants.SOUTHWEST_HIGH_TROUGH_01_LINE);
        }
        if(p[1]>=35&&p[1]<=45&&p[0]>=105&&p[0]<=110)
        {
            result.add(Constants.NORTHEAST_HIGH_TROUGH_02_LINE);
            result.add(Constants.SUMMER_HIGH_TROUGH_02_LINE);
        }
        return  result;
    }

    /**
     * 500高度场获取的高低压
     * @param p
     * @return
     */
    public static Set<Integer> getCodeByHGT500_Point(float[] p){
        Set<Integer> result=new HashSet<>();
        if(p[1]>=20&&p[1]<=25&&p[0]>=110&&p[0]<=120)
        {
            result.add(Constants.TYPHOON);//这里如何判断是台风 还需要商榷
        }
        return  result;
    }

    /**
     * 850高度场获取的脊线
     * @param p
     * @return
     */
    public static Set<Integer> getCodeByHGT850_Ridge_Line(float[] p){
        Set<Integer> result=new HashSet<>();
        if(p[1]>=20&&p[1]<=25&&p[0]==120)
        {

        }

        return  result;
    }
    /**
     * 850高度场获取的槽线
     * @param p
     * @return
     */
    public static Set<Integer> getCodeByHGT850_Trough_Line(float[] p){
        Set<Integer> result=new HashSet<>();
        if(p[1]>=30&&p[1]<=40&&p[0]>=105&&p[0]<=115)
        {
            result.add(Constants.FRONTAL_HIGH_TROUGH_01_LINE);
        }
        return  result;
    }

    /**
     * 850高度场获取的点
     * @param p
     * @return
     */
    public static Set<Integer> getCodeByHGT850_Point(float[] p){
        Set<Integer> result=new HashSet<>();
        if(p[1]>=30&&p[1]<=35&&p[0]>=110&&p[0]<=120)
        {
            result.add(Constants.FRONTAL_JIANGHUAI_CYCLONE);
        }
        if(p[1]>=35&&p[1]<=40&&p[0]>=110&&p[0]<=120)
        {
            result.add(Constants.SOUTHWEST_HUABEI_HIGH_PRESSURE);
            result.add(Constants.SUMMER_HUABEI_HIGH_PRESSURE);
        }
        if(p[1]>=40&&p[1]<=50&&p[0]>=115&&p[0]<=120)
        {
            result.add(Constants.NORTHEAST_CYCLONE);
        }
        if(p[1]>=35&&p[1]<=45&&p[0]>=110&&p[0]<=120)
        {
            result.add(Constants.SUMMER_NORTH_CYCLONE);
        }
        if(p[1]>=35&&p[1]<=40&&p[0]>=105&&p[0]<=115)
        {
            result.add(Constants.TYPHOON_DALU_HIGH_PRESSURE);
        }
        return  result;
    }

    /**
     * 700高度场获取的点
     * @param p
     * @return
     */
    public static Set<Integer> getCodeByHGT8700_Point(float[] p){
        Set<Integer> result=new HashSet<>();
        if(p[1]>=30&&p[1]<=32&&p[0]>=105&&p[0]<=115)
        {
            result.add(Constants.SOUTHWEST_VORTEX);
        }
        return  result;
    }

    /**
     * 1000高度场获取的槽线
     * @param p
     * @return
     */
    public static Set<Integer> getCodeByHGT1000_Trough_Line(float[] p){
        Set<Integer> result=new HashSet<>();
        if(p[1]>=25&&p[1]<=35&&p[0]>=110&&p[0]<=120)
        {
            result.add(Constants.FRONTAL_INVERTED_TROUGH_LINE);
            result.add(Constants.SOUTHWEST_INVERTED_TROUGH_LINE);
            result.add(Constants.NORTHEAST_INVERTED_TROUGH_LINE);
        }
        if(p[1]>=30&&p[1]<=35&&p[0]>=110&&p[0]<=120) {
            result.add(Constants.SUMMER_INVERTED_TROUGH_LINE);
        }

        return  result;
    }

}
