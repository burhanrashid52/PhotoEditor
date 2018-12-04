package ja.burhanrashid52.photoeditor.imagezoom.easing;

public class Expo implements Easing {

	@Override
	public double easeOut( double time, double start, double end, double duration ) {
		return ( time == duration ) ? start + end : end * ( -Math.pow( 2.0, -10.0 * time / duration ) + 1 ) + start;
	}

	@Override
	public double easeIn( double time, double start, double end, double duration ) {
		return ( time == 0 ) ? start : end * Math.pow( 2.0, 10.0 * ( time / duration - 1.0 ) ) + start;
	}

	@Override
	public double easeInOut( double time, double start, double end, double duration ) {
		if ( time == 0 ) return start;
		if ( time == duration ) return start + end;
		if ( ( time /= duration / 2.0 ) < 1.0 ) return end / 2.0 * Math.pow( 2.0, 10.0 * ( time - 1.0 ) ) + start;
		return end / 2.0 * ( -Math.pow( 2.0, -10.0 * --time ) + 2.0 ) + start;
	}

}
