package ja.burhanrashid52.photoeditor.imagezoom.easing;

public class Sine implements Easing {

	@Override
	public double easeOut( double t, double b, double c, double d ) {
		return c * Math.sin( t / d * ( Math.PI / 2 ) ) + b;
	}

	@Override
	public double easeIn( double t, double b, double c, double d ) {
		return -c * Math.cos( t / d * ( Math.PI / 2 ) ) + c + b;
	}

	@Override
	public double easeInOut( double t, double b, double c, double d ) {
		return -c / 2 * ( Math.cos( Math.PI * t / d ) - 1 ) + b;
	}

}
