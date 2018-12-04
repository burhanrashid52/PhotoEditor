package ja.burhanrashid52.photoeditor.imagezoom.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore.Images.ImageColumns;

import java.io.Closeable;

/**
 * Various I/O utilities
 * 
 * @author alessandro
 * 
 */
public class IOUtils {

	/**
	 * Close a {@link Closeable} stream without throwing any exception
	 * 
	 * @param c
	 */
	public static void closeSilently( final Closeable c ) {
		if ( c == null ) return;
		try {
			c.close();
		} catch ( final Throwable t ) {}
	}

	public static void closeSilently( final ParcelFileDescriptor c ) {
		if ( c == null ) return;
		try {
			c.close();
		} catch ( final Throwable t ) {}
	}

	public static void closeSilently( Cursor cursor ) {
		if ( cursor == null ) return;
		try {
			if ( cursor != null ) cursor.close();
		} catch ( Throwable t ) {}
	}

	/**
	 * Try to return the absolute file path from the given Uri
	 * 
	 * @param context
	 * @param uri
	 * @return the file path or null
	 */
	public static String getRealFilePath(final Context context, final Uri uri ) {

		if ( null == uri ) return null;

		final String scheme = uri.getScheme();
		String data = null;

		if ( scheme == null )
			data = uri.getPath();
		else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
			data = uri.getPath();
		} else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
			Cursor cursor = context.getContentResolver().query( uri, new String[] { ImageColumns.DATA }, null, null, null );
			if ( null != cursor ) {
				if ( cursor.moveToFirst() ) {
					int index = cursor.getColumnIndex( ImageColumns.DATA );
					if ( index > -1 ) {
						data = cursor.getString( index );
					}
				}
				cursor.close();
			}
		}
		return data;
	}
}
