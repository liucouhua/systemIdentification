import java.io.File;  
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
public class MainTest {
	public static void main(String[] args) throws Exception
	{
		//hLCentreTest();
		vortexCentreTest();
		//troughTest();
		//ridgeTest();
		//shearLineTest();
		//jetLineTest();
		//SubtropicalHighTest();
	}
	private static void SubtropicalHighTest() {
		// TODO Auto-generated method stub
		int[] level = new int[]{500};
		Calendar start = Calendar.getInstance();
		start.set(2017, 9, 20,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2017, 9, 30,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				GridData grid=new GridData("Z:/data/newecmwf_grib/height/"+level[i]+"/"+fileName.substring(2,10)+".000");
				grid = grid.mutiply(10.0f);
				grid.smooth(50);
				grid.writeToFile("G:/data/systemIdentify/height/"+level[i]+"/"+fileName.substring(2,10)+".000");
				WeatherSystems ridges = SSubtropicalHigh.getSubtropicalHigh(grid,level[i],200.0f);
				ridges.writeIds("G:/data/systemIdentify/ids/subtropicalhigh/"+level[i]+"/"+fileName.substring(2,10)+".000");
				ridges.writeFeatures("G:/data/systemIdentify/features/subtropicalhigh/"+level[i]+"/"+fileName.substring(2,10)+".000");
				System.out.println(fileName);
			}
		}
	}
	private static void hLCentreTest() {
		int[] level = new int[]{1000,850,700};
		Calendar start = Calendar.getInstance();
		start.set(2017, 9, 13,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2017, 9, 20,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				GridData grid=new GridData("Z:/data/newecmwf_grib/height/"+level[i]+"/"+fileName.substring(2,10)+".000");
				grid = grid.mutiply(10);
				grid.smooth(20);
				grid.writeToFile("G:/data/systemIdentify/height/"+level[i]+"/"+fileName.substring(2,10)+".000");
				WeatherSystems hlCentres = SHighLowPressure.getHLCentres(grid,level[i],200.0f);
				hlCentres.writeIds("G:/data/systemIdentify/ids/hlCentre/"+level[i]+"/"+fileName.substring(2,10)+".000");
				hlCentres.writeFeatures("G:/data/systemIdentify/features/hlCentre/"+level[i]+"/"+fileName.substring(2,10)+".000");
				System.out.println(fileName);
			}
		}
	}

	private static void vortexCentreTest() {
		// TODO Auto-generated method stub
		int[] level = new int[]{850,700,500};
		Calendar start = Calendar.getInstance();
		start.set(2017, 9, 20,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2017, 9, 30,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				VectorData wind=new VectorData("Z:/data/newecmwf_grib/stream/"+level[i]+"/"+fileName.substring(2,10)+".000");
				wind.u.smooth(5); wind.v.smooth(5);
				wind.writeToFile("G:/data/systemIdentify/wind/"+level[i]+"/"+fileName.substring(2,10)+".000");
				WeatherSystems vorCentres = SVortex.getVortexCentres(wind,level[i],300.0);
				vorCentres.writeIds("G:/data/systemIdentify/ids/vortexCentre/"+level[i]+"/"+fileName.substring(2,10)+".000");
				vorCentres.writeFeatures("G:/data/systemIdentify/features/vortexCentre/"+level[i]+"/"+fileName.substring(2,10)+".000");
				vorCentres.writeValues("G:/data/systemIdentify/values/vortexCentre/"+level[i]+"/"+fileName.substring(2,10)+".000");
				System.out.println(fileName);
			}
		}
	}

	private static void ridgeTest() {
		// TODO Auto-generated method stub
		int[] level = new int[]{500,200,100};
		Calendar start = Calendar.getInstance();
		start.set(2017, 9, 1,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2017, 9, 10,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				GridData grid=new GridData("Z:/data/newecmwf_grib/height/"+level[i]+"/"+fileName.substring(2,10)+".000");
				grid = grid.mutiply(10.0f);
				grid.smooth(20);
				grid.writeToFile("G:/data/systemIdentify/height/"+level[i]+"/"+fileName.substring(2,10)+".000");
				WeatherSystems ridges = SRidge.getRidge(grid,level[i],200.0f);
				ridges.writeIds("G:/data/systemIdentify/ids/ridge/"+level[i]+"/"+fileName.substring(2,10)+".000");
				ridges.writeFeatures("G:/data/systemIdentify/features/ridge/"+level[i]+"/"+fileName.substring(2,10)+".000");
				System.out.println(fileName);
			}
		}
	}
	
	private static void troughTest() {
		// TODO Auto-generated method stub
		int[] level = new int[]{500,200,100};
		Calendar start = Calendar.getInstance();
		start.set(2017, 9, 1,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2017, 9, 10,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				GridData grid=new GridData("Z:/data/newecmwf_grib/height/"+level[i]+"/"+fileName.substring(2,10)+".000");
				grid = grid.mutiply(10);
				grid.smooth(20);
				grid.writeToFile("G:/data/systemIdentify/height/"+level[i]+"/"+fileName.substring(2,10)+".000");
				WeatherSystems troughs = STrough.getTrough(grid,level[i],200.0f);
				troughs.writeIds("G:/data/systemIdentify/ids/trough/"+level[i]+"/"+fileName.substring(2,10)+".000");
				troughs.writeFeatures("G:/data/systemIdentify/features/trough/"+level[i]+"/"+fileName.substring(2,10)+".000");
				System.out.println(fileName);
			}
		}
	}
	
	private static void jetLineTest() {
		// TODO Auto-generated method stub
		int[] level = new int[]{850,700,500,200,100};
		Calendar start = Calendar.getInstance();
		start.set(2017, 9, 1,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2017, 9, 20,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				VectorData wind=new VectorData("Z:/data/newecmwf_grib/stream/"+level[i]+"/"+fileName.substring(2,10)+".000");
				wind.u.smooth(5); wind.v.smooth(5);
				wind.writeToFile("G:/data/systemIdentify/wind/"+level[i]+"/"+fileName.substring(2,10)+".000");
				WeatherSystems jetLines = SJet.getJet(wind,level[i],300.0f);
				jetLines.writeIds("G:/data/systemIdentify/ids/jetLine/"+level[i]+"/"+fileName.substring(2,10)+".000");
				jetLines.writeFeatures("G:/data/systemIdentify/features/jetLine/"+level[i]+"/"+fileName.substring(2,10)+".000");
				System.out.println(fileName);
			//	break;
			}
		}
	}

	private static void shearLineTest() throws Exception {
		// TODO Auto-generated method stub
		int[] level = new int[]{850,700};
		Calendar start = Calendar.getInstance();
		start.set(2017, 9,1,8,0);
		Calendar end =Calendar.getInstance();
		end.set(2017, 9, 20,20,0);
		for(int i=0; i< level.length;i++){
			Calendar time= (Calendar) start.clone();
			while(time.before(end)){
				String fileName =MyMath.getFileNameFromCalendar(time);
				time.add(Calendar.HOUR, 12);
				VectorData wind=new VectorData("Z:/data/newecmwf_grib/stream/"+level[i]+"/"+fileName.substring(2,10)+".000");
			    wind=new VectorData("G:/data/systemIdentify/wind/"+level[i]+"/"+fileName.substring(2,10)+".000");
				wind.u.smooth(5); wind.v.smooth(5);
			//	wind.writeToFile("G:/data/systemIdentify/wind/"+level[i]+"/"+fileName.substring(2,10)+".000");
				WeatherSystems shearLines = SShear.getShear(wind,level[i],300.0f);
				shearLines.writeIds("G:/data/systemIdentify/ids/shearLine/"+level[i]+"/"+fileName.substring(2,10)+".000");
				shearLines.writeFeatures("G:/data/systemIdentify/features/shearLine/"+level[i]+"/"+fileName.substring(2,10)+".000");
				shearLines.writeValues("G:/data/systemIdentify/values/shearLine/"+level[i]+"/"+fileName.substring(2,10)+".000");
				System.out.println(fileName);
			//	break;
			}
		}
	}

	
}
