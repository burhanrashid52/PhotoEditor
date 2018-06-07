package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

/**
 * Wrapper for MediaScanner to simply trigger a scan for a newly added file
 */
public class MediaScannerWrapper implements
        MediaScannerConnection.MediaScannerConnectionClient {
    private MediaScannerConnection mConnection;
    private String                 mPath;
    private String                 mMimeType;

    // filePath - where to scan; 
    // mime type of media to scan i.e. "image/jpeg". 
    // use "*/*" for any media
    public MediaScannerWrapper(Context ctx, String filePath, String mime) {

        mPath = filePath;
        mMimeType = mime;
        mConnection = new MediaScannerConnection( ctx, this );
    }

    // do the scanning
    public void scan() {

        mConnection.connect();
    }

    // start the scan when scanner is ready
    public void onMediaScannerConnected() {

        mConnection.scanFile( mPath, mMimeType );
        Log.d( "MediaScannerWrapper", "Connected" );
    }

    public void onScanCompleted(String path, Uri uri) {
        // when scan is completes, update media file tags
        Log.d( "MediaScannerWrapper", "media file scanned: " + mPath );
    }
}
