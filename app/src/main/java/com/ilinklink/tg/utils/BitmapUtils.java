//package com.ilinklink.tg.utils;
//
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//
//
//import com.ilinklink.tg.communal.AppLoader;
//import com.spc.pose.demo.R;
//
//import java.util.Hashtable;
//
//public class BitmapUtils {
//
//	public static Bitmap decodeSampledBitmapFromResource(Resources res,
//			int resId, int reqWidth, int reqHeight) {
//
//		// First decode with inJustDecodeBounds=true to check dimensions
//		final BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeResource(res, resId, options);
//
//		// Calculate inSampleSize
//		options.inSampleSize = calculateInSampleSize(options, reqWidth,
//				reqHeight);
//
//		// Decode bitmap with inSampleSize set
//		options.inJustDecodeBounds = false;
//		return BitmapFactory.decodeResource(res, resId, options);
//	}
//
//	public static int calculateInSampleSize(BitmapFactory.Options options,
//			int reqWidth, int reqHeight) {
//		// Raw height and width of image
//		final int height = options.outHeight;
//		final int width = options.outWidth;
//		int inSampleSize = 1;
//
//		if (height > reqHeight || width > reqWidth) {
//
//			// Calculate ratios of height and width to requested height and
//			// width
//			final int heightRatio = Math.round((float) height
//					/ (float) reqHeight);
//			final int widthRatio = Math.round((float) width / (float) reqWidth);
//
//			// Choose the smallest ratio as inSampleSize value, this will
//			// guarantee
//			// a final image with both dimensions larger than or equal to the
//			// requested height and width.
//			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//		}
//
//		return inSampleSize;
//	}
//
//	public static Bitmap getCompressedBitmap(String path) {
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(path, options);
//		options.inSampleSize = calculateInSampleSize(options, 480, 800);
//		options.inJustDecodeBounds = false;
//		return BitmapFactory.decodeFile(path, options);
//	}
//
//	/**
//	 * ???????????????????????????
//	 *
//	 * @param str
//	 * @return
//	 * @throws WriterException
//	 */
//	public static Bitmap create2DCode(String str) throws WriterException {
//		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
//		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
//		hints.put(EncodeHintType.MARGIN, 0);
//		// ??????????????????,?????????????????????,??????????????????????????????????????????,?????????????????????????????????
//		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 480, 480, hints);
//		int width = matrix.getWidth();
//		int height = matrix.getHeight();
//		// ????????????????????????????????????,???????????????????????????
//		int[] pixels = new int[width * height];
//		for (int y = 0; y < height; y++) {
//			for (int x = 0; x < width; x++) {
//				if (matrix.get(x, y)) {
//					pixels[y * width + x] = 0xff000000;
//				}
//			}
//		}
//
//		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//		// ????????????????????????bitmap,????????????api
//		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//		return bitmap;
//	}
//
//	/**
//	 * ???????????????????????????
//	 *
//	 * @param str
//	 * @return
//	 * @throws WriterException
//	 */
//	public static Bitmap create2DCode(String str,int width,int height) throws WriterException {
//		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
//		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
//		hints.put(EncodeHintType.MARGIN, 0);
//		// ??????????????????,?????????????????????,??????????????????????????????????????????,?????????????????????????????????
//		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, width, height, hints);
//		int mWidth = matrix.getWidth();
//		int mHeight = matrix.getHeight();
//		// ????????????????????????????????????,???????????????????????????
//		int[] pixels = new int[mWidth * mHeight];
//		for (int y = 0; y < mHeight; y++) {
//			for (int x = 0; x < mWidth; x++) {
//                if (matrix.get(x, y)) {
//                    pixels[y * mWidth + x] = 0xff000000;
//                } else {
//                    pixels[y * mWidth + x] = 0xffffffff;
//                }
//
////                if (matrix.get(x, y)) {
////					pixels[y * mWidth + x] = 0xff000000;
////				}
//			}
//		}
//
//		Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
//		// ????????????????????????bitmap,????????????api
//		bitmap.setPixels(pixels, 0, width, 0, 0, mWidth, mHeight);
//		return bitmap;
//	}
//
//	/**
//	 * ???????????????????????????
//	 *
//	 * @param str
//	 * @return
//	 * @throws WriterException
//	 */
//	public static Bitmap create2DCodeByLogo(String str,int width,int height) throws WriterException {
//
//		Bitmap bitmap_src = create2DCode(str,width,height);
//		Bitmap bitmap_logo = BitmapFactory.decodeResource(AppLoader.getInstance().getResources(), R.mipmap.icon_logo);
//		return addLogo(bitmap_src,bitmap_logo);
//	}
//
//	/**
//	 * ????????????????????????Logo??????
//	 */
//	private static Bitmap addLogo(Bitmap src, Bitmap logo) {
//		if (src == null) {
//			return null;
//		}
//
//		if (logo == null) {
//			return src;
//		}
//
//		//?????????????????????
//		int srcWidth = src.getWidth();
//		int srcHeight = src.getHeight();
//		int logoWidth = logo.getWidth();
//		int logoHeight = logo.getHeight();
//
//		if (srcWidth == 0 || srcHeight == 0) {
//			return null;
//		}
//
//		if (logoWidth == 0 || logoHeight == 0) {
//			return src;
//		}
//
//		//logo?????????????????????????????????1/5
//		float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
//		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
//		try {
//			Canvas canvas = new Canvas(bitmap);
//			canvas.drawBitmap(src, 0, 0, null);
//			canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
//			canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
//
//			canvas.save();
//			canvas.restore();
//		} catch (Exception e) {
//			bitmap = null;
//			e.getStackTrace();
//		}
//
//		return bitmap;
//	}
//
//}
