package com.dvdprime.android.app.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.dvdprime.android.app.ContextHolder;
import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.MessageHelper;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * 이미지와 관련된 유틸
 * 
 * @author Kwang-myung, Choi (frantik@gmail.com)
 */
public class ImageUtil {

	public static final String TAG = "Util";

	private static Uri sStorageURI = Images.Media.EXTERNAL_CONTENT_URI;

    private static int sIconWidth = -1;
    private static int sIconHeight = -1;

    private static final Paint sPaint = new Paint();
    private static final Rect sBounds = new Rect();
    private static final Rect sOldBounds = new Rect();
    private static Canvas sCanvas = new Canvas();

    static {
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
                									Paint.FILTER_BITMAP_FLAG));
    }

	public static Uri constructUri(String url) {
		Uri uri = Uri.parse(url);
		return uri;
	}

    /**
     * Returns a Bitmap representing the thumbnail of the specified Bitmap.
     * The size of the thumbnail is defined by the dimension
     * android.R.dimen.launcher_application_icon_size.
     *
     * This method is not thread-safe and should be invoked on the UI thread only.
     *
     * @param bitmap The bitmap to get a thumbnail of.
     * @param context The application's context.
     *
     * @return A thumbnail for the specified bitmap or the bitmap itself if the
     *         thumbnail could not be created.
     */
    public static Bitmap createBitmapThumbnail(Bitmap bitmap, Context context) {
    	synchronized(sCanvas) {
	        if (sIconWidth == -1) {
	            final Resources resources = context.getResources();
	            sIconWidth = sIconHeight = (int) resources.getDimension(
	                    android.R.dimen.app_icon_size);
	        }
	
	        int width = sIconWidth;
	        int height = sIconHeight;
	
	        final int bitmapWidth = bitmap.getWidth();
	        final int bitmapHeight = bitmap.getHeight();
	
	        if (width > 0 && height > 0) {
	            if (width < bitmapWidth || height < bitmapHeight) {
	                final float ratio = (float) bitmapWidth / bitmapHeight;
	    
	                if (bitmapWidth > bitmapHeight) {
	                    height = (int) (width / ratio);
	                } else if (bitmapHeight > bitmapWidth) {
	                    width = (int) (height * ratio);
	                }
	    
	                final Bitmap.Config c = (width == sIconWidth && height == sIconHeight) ?
	                        bitmap.getConfig() : Bitmap.Config.ARGB_8888;
	                final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
	                final Canvas canvas = sCanvas;
	                final Paint paint = sPaint;
	                canvas.setBitmap(thumb);
	                paint.setDither(false);
	                paint.setFilterBitmap(true);
	                sBounds.set((sIconWidth - width) / 2, (sIconHeight - height) / 2, width, height);
	                sOldBounds.set(0, 0, bitmapWidth, bitmapHeight);
	                canvas.drawBitmap(bitmap, sOldBounds, sBounds, paint);
	                return thumb;
	            } else if (bitmapWidth < width || bitmapHeight < height) {
	                final Bitmap.Config c = Bitmap.Config.ARGB_8888;
	                final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
	                final Canvas canvas = sCanvas;
	                final Paint paint = sPaint;
	                canvas.setBitmap(thumb);
	                paint.setDither(false);
	                paint.setFilterBitmap(true);
	                canvas.drawBitmap(bitmap, (sIconWidth - bitmapWidth) / 2,
	                        (sIconHeight - bitmapHeight) / 2, paint);
	                return thumb;
	            }
	        }
    	}

        return bitmap;
    }
    
	/**
	 * Bitmap을 최대 길이로 비교하여 최대 길이가 크면 해당 인자값의 길이로 맞춰서 비례적인 크기 조절을 한다.
	 * (말이 쫌 어렵네... )
	 * 
	 * @param bitmap
	 * @param maxDimension
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap bitmap, final int maxDimension) {
		if (bitmap == null)
			return bitmap;

		// final int maxDimension = 70;
		final int bitmapWidth = bitmap.getWidth();
		final int bitmapHeight = bitmap.getHeight();

		if (bitmapWidth > maxDimension || bitmapHeight > maxDimension) {
			final float scale = Math.min((float) maxDimension
					/ (float) bitmapWidth, (float) maxDimension
					/ (float) bitmapHeight);
			final int scaledWidth = (int) (bitmapWidth * scale);
			final int scaledHeight = (int) (bitmapHeight * scale);

			if (MessageHelper.DEBUG)
				Log.d(TAG,
						"Resizing from: " + String.valueOf(bitmapWidth) + "x"
								+ String.valueOf(bitmapHeight) + " to "
								+ String.valueOf(scaledWidth) + "x"
								+ String.valueOf(scaledHeight));

			return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
		} else {
			return bitmap;
		}
	}

	/**
	 * 파라미터 길이에 맞춰서 비례적으로 죽이거나 늘린다.
	 * 
	 * @param bitmap
	 * @param newWidth
	 * @return
	 */
	public static Bitmap ResizeBitmap(Bitmap bitmap, int newWidth) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float temp = ((float) height) / ((float) width);
		int newHeight = (int) ((newWidth) * temp);
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();

		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return resizedBitmap;

	}

	/**
	 * 가로,세로 값을 입력 받아서 강제로 크기를 변경한다.
	 * 
	 * @param bitmap
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap AbsResizeBitmap(Bitmap bitmap, int newWidth,
			int newHeight) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// float temp = ((float) height) / ((float) width);

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();

		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return resizedBitmap;
	}

	/**
	 * 가장 큰 길이로 리사이즈를 한 후에 가운데를 중심으로 파라미터 길이만큼 크롭한다.
	 * 
	 * @param bitmap
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap ResizeCenterInBitmap(Bitmap bitmap, int newWidth,
			int newHeight) {
		int sourceWidth = bitmap.getWidth();
		int sourceHeight = bitmap.getHeight();
		int sourceX = 0;
		int sourceY = 0;

		if (sourceWidth > sourceHeight) {
			bitmap = resizeBitmap(bitmap, sourceHeight);
			sourceWidth = bitmap.getWidth();
			sourceHeight = bitmap.getHeight();
		} else if (sourceHeight > sourceWidth && sourceWidth > newHeight) {
			bitmap = resizeBitmap(bitmap, newHeight);
			sourceWidth = bitmap.getWidth();
			sourceHeight = bitmap.getHeight();
		}

		if (sourceWidth > newWidth)
			sourceX = (int) ((sourceWidth - newWidth) / 2);

		if (sourceHeight > newHeight)
			sourceY = (int) ((sourceHeight - newHeight) / 2);

		if (sourceWidth < newWidth)
			newWidth = sourceWidth;
		if (sourceHeight < newHeight)
			newHeight = sourceHeight;

		Bitmap cropedBitmap = Bitmap.createBitmap(bitmap, sourceX, sourceY,
				newWidth, newHeight);

		return cropedBitmap;
	}

	/**
	 * byte[] 을 bitmap 으로 얻어옴.
	 * 
	 * @param b
	 * @return
	 */
	public static Bitmap Bytes2Bimap(byte[] b) {
		try {
			if (b.length != 0) {
				return BitmapFactory.decodeByteArray(b, 0, b.length);
			} else {
				return null;
			}
		} catch (OutOfMemoryError e) {
			Toast.makeText(ContextHolder.getInstance().getContext(), 
					ContextHolder.getInstance().getContext().getString(R.string.out_of_memory_error_message), 
					Toast.LENGTH_SHORT).show();
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * bitmap 을 byte[] 로 얻어옴.
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			return baos.toByteArray();
		} catch (OutOfMemoryError e) {
			Toast.makeText(ContextHolder.getInstance().getContext(), 
					ContextHolder.getInstance().getContext().getString(R.string.out_of_memory_error_message), 
					Toast.LENGTH_SHORT).show();
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 비트맵을 파일로 작성한다.
	 * 
	 * @param bm
	 * @param outFile
	 */
	public static void WriteFileFromBitmap(Bitmap bm, File outFile) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(outFile);
			bm.compress(Bitmap.CompressFormat.JPEG, 70, out);
		} catch (Exception e) {
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 라운드된 bitmap 생성.
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	/**
	 * 기본 그림자값으로 bitmap 생성
	 * 
	 * @param bitmap
	 * @return
	 */
	@Deprecated
	public static Bitmap getDropShadowBitmap(Bitmap bitmap) {
		return getDropShadowBitmap(bitmap, 1, 1);
	}
	
	/**
	 * 그림자 효과를 추가한 Bitmap 생성
	 * 
	 * @param bitmap
	 * @param offsetX
	 * @param offsetY
	 * @return
	 */
	@Deprecated
	public static Bitmap getDropShadowBitmap(Bitmap bitmap, int offsetX, int offsetY) {
//		BlurMaskFilter blurFilter = new BlurMaskFilter(5, BlurMaskFilter.Blur.OUTER);
//		Paint shadowPaint = new Paint();
//		shadowPaint.setMaskFilter(blurFilter);
//		
//		int[] offsetXY = {offsetX, offsetY};
//		Bitmap shadowBitmap = bitmap.extractAlpha(shadowPaint, offsetXY);
//		
//		Canvas c = new Canvas(shadowBitmap);
//		c.drawBitmap(bitmap, offsetXY[0], offsetXY[1], null);
		
//		Canvas c = new Canvas(bitmap);
//		Paint mShadow = new Paint(); 
//		mShadow.setAntiAlias(true);
		// radius=10, y-offset=2, color=black 
//		mShadow.setShadowLayer(10.0f, 0.0f, 2.0f, 0xFF000000); 
		// in onDraw(Canvas) 
//		c.drawBitmap(bitmap, 0.0f, 0.0f, mShadow);
		
		return bitmap;
	}
	
	public static Bitmap getArtworkBitmap(Bitmap tempBitmap ) {

		Bitmap bitmap = null;
        BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
        sBitmapOptionsCache.inPreferredConfig = Bitmap.Config.ARGB_8888;
        sBitmapOptionsCache.inDither = false;
        try {
            if(tempBitmap!=null){
            	bitmap =  Bitmap.createBitmap(tempBitmap.getWidth(), tempBitmap.getHeight(), Bitmap.Config.ARGB_8888);

            	Canvas tempCanvas = new Canvas(bitmap);
            	
            	tempCanvas.drawBitmap(tempBitmap, 0, 0, new Paint());
            	
    			Paint line = new Paint();
    			line.setColor(Color.argb(0, 114, 114, 114));
    			tempCanvas.drawLine(0, 0, tempBitmap.getWidth()-1, 0, line);
    			tempCanvas.drawLine(0, 0, 0, tempBitmap.getHeight()-1, line);
    			tempCanvas.drawLine(tempBitmap.getWidth()-1, 0, tempBitmap.getWidth()-1, tempBitmap.getHeight()-1, line);
    			tempCanvas.drawLine(0, tempBitmap.getHeight()-1, tempBitmap.getWidth(), tempBitmap.getHeight()-1, line);

    			tempBitmap.recycle();
    			tempBitmap = null;
            }
        }
        catch (OutOfMemoryError e) {
        	e.printStackTrace();
        	return null;
        }
        return bitmap;
    }

	/**
	 * 물에 반사된 효과 주기
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getReflectionBitmap(Bitmap bitmap) {
		//The gap we want between the reflection and the original image
        final int reflectionGap = 4;
        
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		//This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        
        //Create a Bitmap with the flip matix applied to it.
        //We only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height/2, width, height/2, matrix, false);
        
            
        //Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width 
        								, (height + height/2), Config.ARGB_8888);
      
        //Create a new Canvas with the bitmap that's big enough for
        //the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        //Draw in the original image
        canvas.drawBitmap(bitmap, 0, 0, null);
        //Draw in the gap
        Paint deafaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
        //Draw in the reflection
        canvas.drawBitmap(reflectionImage,0, height + reflectionGap, null);
        
        //Create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, 
    		   							bitmapWithReflection.getHeight() + reflectionGap, 
    		   							0x70ffffff, 0x00ffffff,
    		   							TileMode.CLAMP);
        //Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        //Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        //Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, 
    		   		bitmapWithReflection.getHeight() + reflectionGap, paint); 
       
       return bitmapWithReflection;
	}

	/**
	 * 원하는 사이즈의 썸네일 이미지 생성.
	 * 
	 * @param uri
	 * @param size
	 * @return
	 */
	public static Bitmap createThumbnailBitmap(Uri uri, int size) {
		InputStream input = null;

		try {
			input = ContextHolder.getInstance().getContext()
					.getContentResolver().openInputStream(uri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, options);
			input.close();

			// Compute the scale.
			int scale = 1;
			while ((options.outWidth / scale > size)
					|| (options.outHeight / scale > size)) {
				scale *= 2;
			}

			options.inJustDecodeBounds = false;
			options.inSampleSize = scale;

			input = ContextHolder.getInstance().getContext()
					.getContentResolver().openInputStream(uri);

			return BitmapFactory.decodeStream(input, null, options);
		} catch (IOException e) {
			if (MessageHelper.DEBUG)
				Log.w(TAG, e);

			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					if (MessageHelper.DEBUG)
						Log.w(TAG, e);
				}
			}
		}
	}

	public static File createTempCopy(InputStream input) throws IOException {
		File tempFile = File.createTempFile("tmpbmp", ".jpg", ContextHolder
				.getInstance().getContext().getFilesDir());
		byte[] buffer = new byte[16 * 1024];
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(tempFile), buffer.length);
		BufferedInputStream in = new BufferedInputStream(input, buffer.length);
		int readBytes = 0;
		while ((readBytes = in.read(buffer)) != -1) {
			out.write(buffer, 0, readBytes);
		}
		out.flush();
		out.close();
		input.close();
		in.close();
		return tempFile;
	}

	public static Uri getContentUriFromFile(File imageFile) {
		Uri uri = null;
		ContentResolver cr = ContextHolder.getInstance().getContext()
				.getContentResolver();
		String[] projection = { Images.Media._ID, Images.Media.DATA };
		String selection = Images.Media.DATA + " = ?";
		String[] selArgs = { imageFile.toString() };

		Cursor cursor = cr.query(sStorageURI, projection, selection, selArgs,
				null);

		if (cursor.moveToFirst()) {

			String id;
			int idColumn = cursor.getColumnIndex(Images.Media._ID);
			id = cursor.getString(idColumn);

			uri = Uri.withAppendedPath(sStorageURI, id);
		}
		cursor.close();
		if (uri != null) {
			if (MessageHelper.DEBUG)
				Log.d(TAG,
						"Found picture in MediaStore : " + imageFile.toString()
								+ " is " + uri.toString());
		} else {
			if (MessageHelper.DEBUG)
				Log.d(TAG,
						"Did not find picture in MediaStore : "
								+ imageFile.toString());
		}

		return uri;
	}

	/**
	 * 이미지의 EXIF 정보에서 Rotation 정보 획득.
	 * 
	 * @param imgPath
	 * @return
	 */
	public static int getExifRotation(String imgPath) {
		try {
			ExifInterface exif = new ExifInterface(imgPath);
			String rotationAmount = exif
					.getAttribute(ExifInterface.TAG_ORIENTATION);
			if (!TextUtils.isEmpty(rotationAmount)) {
				int rotationParam = Integer.parseInt(rotationAmount);
				switch (rotationParam) {
				case ExifInterface.ORIENTATION_NORMAL:
					return 0;
				case ExifInterface.ORIENTATION_ROTATE_90:
					return 90;
				case ExifInterface.ORIENTATION_ROTATE_180:
					return 180;
				case ExifInterface.ORIENTATION_ROTATE_270:
					return 270;
				default:
					return 0;
				}
			} else {
				return 0;
			}
		} catch (Exception ex) {
			return 0;
		}
	}

	/** Get Bitmap's Width **/
	public static int getBitmapOfWidth(String fileName) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileName, options);
			return options.outWidth;
		} catch (Exception e) {
			return 0;
		}
	}

	/** Get Bitmap's height **/
	public static int getBitmapOfHeight(String fileName) {

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileName, options);

			return options.outHeight;
		} catch (Exception e) {
			return 0;
		}
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int target) {
		int w = options.outWidth;
		int h = options.outHeight;

		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);

		if (candidate == 0)
			return 1;

		if (candidate > 1) {
			if ((w > target) && (w / candidate) < target)
				candidate -= 1;
		}

		if (candidate > 1) {
			if ((h > target) && (h / candidate) < target)
				candidate -= 1;
		}

		if (MessageHelper.DEBUG)
			Log.d(TAG, "for w/h " + w + "/" + h + " returning " + candidate
					+ "(" + (w / candidate) + " / " + (h / candidate));

		return candidate;
	}

	// Clear bitmap
	public static void clearBitmap(Bitmap bm) {
		bm.recycle();
		bm = null;
		System.gc();
	}
}
