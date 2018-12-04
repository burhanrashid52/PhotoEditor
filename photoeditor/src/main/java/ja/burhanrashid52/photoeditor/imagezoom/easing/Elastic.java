package ja.burhanrashid52.photoeditor.imagezoom.easing;

public class Elastic implements Easing {

	@Override
	public double easeIn( double time, double start, double end, double duration ) {
		return easeIn( time, start, end, duration, start + end, duration );
	}

	public double easeIn( double t, double b, double c, double d, double a, double p ) {
		double s;
		if ( t == 0 ) return b;
		if ( ( t /= d ) == 1 ) return b + c;
		if ( !( p > 0 ) ) p = d * .3;
		if ( !( a > 0 ) || a < Math.abs( c ) ) {
			a = c;
			s = p / 4;
		} else
			s = p / ( 2 * Math.PI ) * Math.asin( c / a );
		return -( a * Math.pow( 2, 10 * ( t -= 1 ) ) * Math.sin( ( t * d - s ) * ( 2 * Math.PI ) / p ) ) + b;
	}

	@Override
	public double easeOut( double time, double start, double end, double duration ) {
		return easeOut( time, start, end, duration, start + end, duration );
	}

	public double easeOut( double t, double b, double c, double d, double a, double p ) {
		double s;
		if ( t == 0 ) return b;
		if ( ( t /= d ) == 1 ) return b + c;
		if ( !( p > 0 ) ) p = d * .3;
		if ( !( a > 0 ) || a < Math.abs( c ) ) {
			a = c;
			s = p / 4;
		} else
			s = p / ( 2 * Math.PI ) * Math.asin( c / a );
		return ( a * Math.pow( 2, -10 * t ) * Math.sin( ( t * d - s ) * ( 2 * Math.PI ) / p ) + c + b );
	}

	@Override
	public double easeInOut( double t, double b, double c, double d ) {
		return easeInOut( t, b, c, d, b + c, d );
	}

	public double easeInOut( double t, double b, double c, double d, double a, double p ) {
		double s;

		if ( t == 0 ) return b;
		if ( ( t /= d / 2 ) == 2 ) return b + c;
		if ( !( p > 0 ) ) p = d * ( .3 * 1.5 );
		if ( !( a > 0 ) || a < Math.abs( c ) ) {
			a = c;
			s = p / 4;
		} else
			s = p / ( 2 * Math.PI ) * Math.asin( c / a );
		if ( t < 1 ) return -.5 * ( a * Math.pow( 2, 10 * ( t -= 1 ) ) * Math.sin( ( t * d - s ) * ( 2 * Math.PI ) / p ) ) + b;
		return a * Math.pow( 2, -10 * ( t -= 1 ) ) * Math.sin( ( t * d - s ) * ( 2 * Math.PI ) / p ) * .5 + c + b;
	}
}
