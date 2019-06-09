package recutil;

public class WindUtils {

	public static void main(String args[]) {
		System.out.println(WindUtils.getWindLevel(5.5d));
		System.out.println(WindUtils.getWindDireInDeg(90, 9));
		float[] uv = getUV(90, 45);
		System.out.println(uv[0]+"   "+uv[1]);
	}

	
	
	public static final double[] criValOfWindInten = { 0.0, 0.3, 1.6, 3.4, 5.5, 8.0, 10.8, 13.9, 17.2, 20.8, 24.5, 28.5,
			32.6, 37.0, 41.5, 46.2, 51.0, 56.1, 61.3 };

	public static final String[] windDire = { "北风", "偏北风", "东北风", "偏东风", "东风", "偏东风", "东南风", "偏南风", "南风", "偏南风", "西南风",
			"偏西风", "西风", "偏西风", "西北风", "偏北风" };

	public static float[] getUV(double windSpeed,double windDir){
		return new float[]{(float) (-1.0*Math.sin(windDir*Math.PI/180)*windSpeed),(float) (-1.0*Math.cos(windDir*Math.PI/180)*windSpeed)};
	}
	
	// 根据风向度数计算出风向中文描述
	public static String getWindDire(double windDireInDeg) {
		int windIndex = -1;
		if (windDireInDeg == -1) {
			return "缺测";
		}

		if (windDireInDeg >= 348.75 || windDireInDeg <= 11.25) {
			windIndex = 0;
		} else {
			double startDeg;
			double endDeg;
			for (int i = 1; i < windDire.length; i++) {
				startDeg = 11.25 + 22.5 * (i - 1);
				endDeg = 11.25 + 22.5 * (i);
				if (windDireInDeg >= startDeg && windDireInDeg < endDeg) {
					windIndex = i;
					break;
				}
			}
		}
		return windDire[windIndex];
	}

	// 根据风向度数计算出风向中文描述---8风向
	public static String getWindDireIn8(double windDireInDeg) {
		int windIndex = -1;
		if (windDireInDeg == -1) {
			return "缺测";
		}

		if (windDireInDeg >= 337.5 || windDireInDeg <= 22.5) {
			windIndex = 0;
		} else {
			double startDeg;
			double endDeg;
			for (int i = 1; i < windDire.length; i++) {
				startDeg = 22.5 + 45 * (i - 1);
				endDeg = 22.5 + 45 * (i);
				if (windDireInDeg >= startDeg && windDireInDeg < endDeg) {
					windIndex = i;
					break;
				}
			}
		}
		return windDire[windIndex * 2];
	}

	// 根据UV计算风向(英文风向表示)
	public static String getWindDire(double uWind, double vWind) {
		double windDireInDeg = getWindDireInDeg(uWind, vWind);
		return getWindDire(windDireInDeg);
	}

	// 根据UV计算风向
	public static String getWindDireIn8(double uWind, double vWind) {
		double windDireInDeg = getWindDireInDeg(uWind, vWind);
		return getWindDireIn8(windDireInDeg);
	}

	// 根据UV计算风向(度数表示)
	public static double getWindDireInDeg(double uWind, double vWind) {
		double atanUV;
		if (uWind == 0 && vWind == 0) {
			return 0;// 表示无风
		}
		if (uWind == 999999.0 || uWind == 999999.0) {
			return -1;// -1表示无数据
		}
		if (uWind * vWind != 0) {
			atanUV = Math.atan(uWind / vWind) / Math.PI * 180;
			// 区分一二象限风向和三四象限风向转换
			double degree = vWind > 0 ? 180 : 360;
			return accurate((degree + atanUV) % 360, 2);
		} else {
			// 判断u,v中有一个为0时的风向
			return accurate(uWind == 0 ? vWind > 0 ? 180 : 0 : uWind > 0 ? 270 : 90, 2);// 包含了保留两位小数操作
		}
	}

	/**
	 * 
	 * @Title: windLevel
	 * @Description: 3级风和以下的都是微风
	 * @param uWind
	 * @param vWind
	 * @return
	 */
	public static String getWindLevel(double uWind, double vWind) {
		double windSpeed = Math.sqrt(uWind * uWind + vWind * vWind);

		if (windSpeed == 0) {
			return "无风";
		}

		int idx = 0;
		for (; idx < criValOfWindInten.length - 1; idx++) {
			if (windSpeed >= criValOfWindInten[idx] && windSpeed < criValOfWindInten[idx + 1])
				break;
		}
		if (idx > 3) {
			return (idx) + " 级风";
		} else
			return "微风";

	}

	/**
	 * 
	 * @Title: windLevel
	 * @Description: 3级风和以下的都是微风
	 * @param uWind
	 * @param vWind
	 * @return
	 */
	public static String getWindLevel(double windSpeed) {

		if (windSpeed == 0) {
			return "无风";
		}

		int idx = 0;
		for (; idx < criValOfWindInten.length - 1; idx++) {
			if (windSpeed >= criValOfWindInten[idx] && windSpeed < criValOfWindInten[idx + 1])
				break;
		}
		if (idx > 3) {
			return (idx) + " 级风";
		}
		return "微风";// getWindCNClass(idx+"");

	}

	/**
	 * 
	 * @Title: windLevel
	 * @Description: 3级风和以下的都是微风
	 * @param uWind
	 * @param vWind
	 * @return
	 */
	public static int getWindLevelInt(double windSpeed) {
		if (windSpeed == 0) {
			return 0;
		}
		int idx = 0;
		for (; idx < criValOfWindInten.length - 1; idx++) {
			if (windSpeed >= criValOfWindInten[idx] && windSpeed < criValOfWindInten[idx + 1])
				break;
		}
		return idx;

	}

	public static double getWindSpeed(double uWind, double vWind) {
		return Math.sqrt(uWind * uWind + vWind * vWind);
	}

	// 四舍五入保留n位小数的方法
	public static double accurate(double input, int decimalDigits) {
		double power = Math.pow(10, decimalDigits);
		return Math.round(input * power) / power;
	}
}
