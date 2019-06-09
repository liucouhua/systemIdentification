import java.util.ArrayList;

public class SSubtropicalHigh{
	public static WeatherSystems getSubtropicalHigh(GridData height, int level, float scale) {
		VectorData tranDirection=new VectorData(height.gridInfo);
		tranDirection.v.setValue(1.0f); 
		GridData marker = height.add(-5880).sign01();
		ArrayList<Line> ridge = SystemIdentification.HighValueAreaRidge(tranDirection,height,level);
		ridge = LineDealing.cutLines(ridge, marker);
		LineDealing.smoothLines(ridge, 10);
		ridge = SystemIdentification.getLongLine(scale,ridge);
		GridData ids = SystemIdentification.getCuttedRegion(height.mutiply(marker));
		LineDealing.writeToFile("G:/data/systemIdentify/ridge.txt",ridge);
		ids.writeToFile("G:/data/systemIdentify/ids0.txt");
		ids = SystemIdentification.combineStrongConnectingRegion_2d(ids, 0.1f);
		ids.writeToFile("G:/data/systemIdentify/ids1.txt");
		WeatherSystems sh = new  WeatherSystems("SubtropicalHigh",level);
		sh.setAxes(ridge);
		sh.setValue(height);
		sh.reset();
		return sh;
	}
}
