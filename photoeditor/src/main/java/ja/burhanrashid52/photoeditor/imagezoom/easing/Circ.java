package ja.burhanrashid52.photoeditor.imagezoom.easing;

public class Circ implements Easing {

	@Override
	public double easeOut( double time, double start, double end, double duration ) {
		return end * Math.sqrt( 1.0 - ( time = time / duration - 1.0 ) * time ) + start;
	}

	@Override
	public double easeIn( double time, double start, double end, double duration ) {
		return -end * ( Math.sqrt( 1.0 - ( time /= duration ) * time ) - 1.0 ) + start;
	}

	@Override
	public double easeInOut( double time, double start, double end, double duration ) {
		if ( ( time /= duration / 2 ) < 1 ) return -end / 2.0 * ( Math.sqrt( 1.0 - time * time ) - 1.0 ) + start;
		return end / 2.0 * ( Math.sqrt( 1.0 - ( time -= 2.0 ) * time ) + 1.0 ) + start;
	}

}
