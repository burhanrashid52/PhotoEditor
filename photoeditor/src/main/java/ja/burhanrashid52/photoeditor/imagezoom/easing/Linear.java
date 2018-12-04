package ja.burhanrashid52.photoeditor.imagezoom.easing;

public class Linear implements Easing {

	public double easeNone( double time, double start, double end, double duration ) {
		return end * time / duration + start;
	}

	@Override
	public double easeOut( double time, double start, double end, double duration ) {
		return end * time / duration + start;
	}

	@Override
	public double easeIn( double time, double start, double end, double duration ) {
		return end * time / duration + start;
	}

	@Override
	public double easeInOut( double time, double start, double end, double duration ) {
		return end * time / duration + start;
	}

}
