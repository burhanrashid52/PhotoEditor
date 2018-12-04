package ja.burhanrashid52.photoeditor.imagezoom.utils;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;

import java.io.IOException;

public class ExifUtils {

	public static final String[] EXIF_TAGS = { "FNumber",
			ExifInterface.TAG_DATETIME, "ExposureTime",
			ExifInterface.TAG_FLASH, ExifInterface.TAG_FOCAL_LENGTH,
			"GPSAltitude", "GPSAltitudeRef", ExifInterface.TAG_GPS_DATESTAMP,
			ExifInterface.TAG_GPS_LATITUDE, ExifInterface.TAG_GPS_LATITUDE_REF,
			ExifInterface.TAG_GPS_LONGITUDE,
			ExifInterface.TAG_GPS_LONGITUDE_REF,
			ExifInterface.TAG_GPS_PROCESSING_METHOD,
			ExifInterface.TAG_GPS_TIMESTAMP, ExifInterface.TAG_IMAGE_LENGTH,
			ExifInterface.TAG_IMAGE_WIDTH, "ISOSpeedRatings",
			ExifInterface.TAG_MAKE, ExifInterface.TAG_MODEL,
			ExifInterface.TAG_WHITE_BALANCE, };

	/**
	 * Return the rotation of the passed image file
	 * 
	 * @param filepath
	 *            image absolute file path
	 * @return image orientation
	 */
	public static int getExifOrientation(final String filepath) {
		if (null == filepath)
			return 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException e) {
			return 0;
		}
		return getExifOrientation(exif);
	}

	public static int getExifOrientation(final ExifInterface exif) {
		int degree = 0;

		if (exif != null) {
			final int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
			}
		}
		return degree;
	}

	/**
	 * Load the exif tags into the passed Bundle
	 * 
	 * @param filepath
	 * @param out
	 * @return true if exif tags are loaded correctly
	 */
	public static boolean loadAttributes(final String filepath, Bundle out) {
		ExifInterface e;
		try {
			e = new ExifInterface(filepath);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}

		for (String tag : EXIF_TAGS) {
			out.putString(tag, e.getAttribute(tag));
		}
		return true;
	}

	/**
	 * Store the exif attributes in the passed image file using the TAGS stored
	 * in the passed bundle
	 * 
	 * @param filepath
	 * @param bundle
	 * @return true if success
	 */
	public static boolean saveAttributes(final String filepath, Bundle bundle) {
		ExifInterface exif;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		for (String tag : EXIF_TAGS) {
			if (bundle.containsKey(tag)) {
				exif.setAttribute(tag, bundle.getString(tag));
			}
		}
		try {
			exif.saveAttributes();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Return the string representation of the given orientation
	 * 
	 * @param orientation
	 * @return
	 */
	public static String getExifOrientation(int orientation) {
		switch (orientation) {
		case 0:
			return String.valueOf(ExifInterface.ORIENTATION_NORMAL);
		case 90:
			return String.valueOf(ExifInterface.ORIENTATION_ROTATE_90);
		case 180:
			return String.valueOf(ExifInterface.ORIENTATION_ROTATE_180);
		case 270:
			return String.valueOf(ExifInterface.ORIENTATION_ROTATE_270);
		default:
			throw new AssertionError("invalid: " + orientation);
		}
	}

	/**
	 * Try to get the exif orientation of the passed image uri
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	public static int getExifOrientation(Context context, Uri uri) {

		final String scheme = uri.getScheme();

		ContentProviderClient provider = null;
		if (scheme == null || ContentResolver.SCHEME_FILE.equals(scheme)) {
			return getExifOrientation(uri.getPath());
		} else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
			try {
				provider = context.getContentResolver()
						.acquireContentProviderClient(uri);
			} catch (SecurityException e) {
				return 0;
			}

			if (provider != null) {
				Cursor result;
				try {
					result = provider.query(uri, new String[] {
							Images.ImageColumns.ORIENTATION,
							Images.ImageColumns.DATA }, null, null, null);
				} catch (Exception e) {
					e.printStackTrace();
					return 0;
				}

				if (result == null) {
					return 0;
				}

				int orientationColumnIndex = result
						.getColumnIndex(Images.ImageColumns.ORIENTATION);
				int dataColumnIndex = result
						.getColumnIndex(Images.ImageColumns.DATA);

				try {
					if (result.getCount() > 0) {
						result.moveToFirst();

						int rotation = 0;

						if (orientationColumnIndex > -1) {
							rotation = result.getInt(orientationColumnIndex);
						}

						if (dataColumnIndex > -1) {
							String path = result.getString(dataColumnIndex);
							rotation |= getExifOrientation(path);
						}
						return rotation;
					}
				} finally {
					result.close();
				}
			}
		}
		return 0;
	}
}
