package ja.burhanrashid52.photoeditor.imagezoom.utils;

/**
 * 3x3
 * 
 * @author panyi
 * 
 */
public class Matrix3 {
	private float[] data;

	public Matrix3() {
		data = new float[9];
	}

	public Matrix3(float[] values) {
		this();
		setValues(values);
	}

	public void setValues(float[] values) {
		for (int i = 0, len = values.length; i < len; i++) {
			data[i] = values[i];
		}// end for i
	}

	public float[] getValues() {
		float[] retValues = new float[9];
		System.arraycopy(data, 0, retValues, 0, 9);
		return retValues;
	}

	public Matrix3 copy() {
		return new Matrix3(getValues());
	}

	/**
	 * 两矩阵相乘
	 * 
	 * @param m
	 */
	public void multiply(Matrix3 m) {
		float[] ma = this.copy().getValues();
		float[] mb = m.copy().getValues();

		data[0] = ma[0] * mb[0] + ma[1] * mb[3] + ma[2] * mb[6];
		data[1] = ma[0] * mb[1] + ma[1] * mb[4] + ma[2] * mb[7];
		data[2] = ma[0] * mb[2] + ma[1] * mb[5] + ma[2] * mb[8];

		data[3] = ma[3] * mb[0] + ma[4] * mb[3] + ma[5] * mb[6];
		data[4] = ma[3] * mb[1] + ma[4] * mb[4] + ma[5] * mb[7];
		data[5] = ma[3] * mb[2] + ma[4] * mb[5] + ma[5] * mb[8];

		data[6] = ma[6] * mb[0] + ma[7] * mb[3] + ma[8] * mb[6];
		data[7] = ma[6] * mb[1] + ma[7] * mb[4] + ma[8] * mb[7];
		data[8] = ma[6] * mb[2] + ma[7] * mb[5] + ma[8] * mb[8];
	}

	/**
	 * 求当前矩阵的逆矩阵
	 *
	 * @return
	 */
	public Matrix3 inverseMatrix() {
		float[] m = this.copy().getValues();
		float sx = m[0];
		float sy = m[4];
		m[0] = 1 / sx;
		m[1] = 0;
		m[2] = (-1) * (data[2] / sx);
		m[3] = 0;
		m[4] = 1 / sy;
		m[5] = (-1) * (data[5] / sy);
		m[6] = 0;
		m[7] = 0;
		m[8] = 1;
		return new Matrix3(m);
	}

	public void println() {
		System.out.println("data--->" + data[0] + "  " + data[1] + "  "
				+ data[2]);
		System.out.println("              " + data[3] + "  " + data[4] + "  "
				+ data[5]);
		System.out.println("              " + data[6] + "  " + data[7] + "  "
				+ data[8]);
	}
}// end class
