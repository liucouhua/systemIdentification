package recutil;

public class GridDataModel implements Cloneable {

	private int lev;

	private String casname; // cas文件名

	private String var;

	private String directory;

	private double endLon;

	private double endLat;

	private double startLon;

	private double startLat;

	private double lonstep;

	private double latstep;

	private int lonCount;

	private int latCount;

	private float[] datas;

	private float[] verctor;

	private String time; // yyyyMMddHH

	private int offset; //

	private String apptime; // 起报时间


	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getDirectory() {
		return directory;
	}

	public int getLev() {
		return lev;
	}

	public void setLev(int lev) {
		this.lev = lev;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getCasname() {
		return casname;
	}

	public void setCasname(String casname) {
		this.casname = casname;
	}


	public double getEndLon() {
		return endLon;
	}

	public void setEndLon(double endLon) {
		this.endLon = endLon;
	}

	public double getEndLat() {
		return endLat;
	}

	public void setEndLat(double endLat) {
		this.endLat = endLat;
	}

	public double getStartLon() {
		return startLon;
	}

	public void setStartLon(double startLon) {
		this.startLon = startLon;
	}

	public double getStartLat() {
		return startLat;
	}

	public void setStartLat(double startLat) {
		this.startLat = startLat;
	}

	public double getLonstep() {
		return lonstep;
	}

	public void setLonstep(double lonstep) {
		this.lonstep = lonstep;
	}

	public double getLatstep() {
		return latstep;
	}

	public void setLatstep(double latstep) {
		this.latstep = latstep;
	}

	public int getLonCount() {
		return lonCount;
	}

	public void setLonCount(int lonCount) {
		this.lonCount = lonCount;
	}

	public int getLatCount() {
		return latCount;
	}

	public void setLatCount(int latCount) {
		this.latCount = latCount;
	}

	public float[] getDatas() {
		return datas;
	}

	public void setDatas(float[] datas) {
		this.datas = datas;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public float[] getVerctor() {
		return verctor;
	}

	public void setVerctor(float[] verctor) {
		this.verctor = verctor;
	}

	public String getApptime() {
		return apptime;
	}

	public void setApptime(String apptime) {
		this.apptime = apptime;
	}

	public GridDataModel clone() {
		GridDataModel stu = null;
		try {
			stu = (GridDataModel) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return stu;
	}
}
