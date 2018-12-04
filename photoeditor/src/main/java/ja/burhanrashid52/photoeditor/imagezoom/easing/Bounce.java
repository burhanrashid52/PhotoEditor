package ja.burhanrashid52.photoeditor.imagezoom.easing;

public class Bounce implements Easing {

	@Override
	public double easeOut( double t, double b, double c, double d ) {
		if ( ( t /= d ) < ( 1.0 / 2.75 ) ) {
			return c * ( 7.5625 * t * t ) + b;
		} else if ( t < ( 2.0 / 2.75 ) ) {
			return c * ( 7.5625 * ( t -= ( 1.5 / 2.75 ) ) * t + .75 ) + b;
		} else if ( t < ( 2.5 / 2.75 ) ) {
			return c * ( 7.5625 * ( t -= ( 2.25 / 2.75 ) ) * t + .9375 ) + b;
		} else {
			return c * ( 7.5625 * ( t -= ( 2.625 / 2.75 ) ) * t + .984375 ) + b;
		}
	}

	@Override
	public double easeIn( double t, double b, double c, double d ) {
		return c - easeOut( d - t, 0, c, d ) + b;
	}

	@Override
	public double easeInOut( double t, double b, double c, double d ) {
		if ( t < d / 2.0 )
			return easeIn( t * 2.0, 0, c, d ) * .5 + b;
		else
			return easeOut( t * 2.0 - d, 0, c, d ) * .5 + c * .5 + b;
	}
}
