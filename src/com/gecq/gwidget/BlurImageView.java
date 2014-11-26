package com.gecq.gwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class BlurImageView extends ImageView {

	private boolean DEBUG = false;
	private Paint mPaint;
	private Bitmap bit;
	private float radius = 40;

	/** 水平方向模糊度 */
	private float hRadius = radius;
	/** 竖直方向模糊度 */
	private float vRadius = radius;
	/** 模糊迭代度 */
	private int iterations = 2;

	private BitmapBlurTask blurTask;

	public BlurImageView(Context context) {
		super(context);
		init();
	}

	public BlurImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		getAttrs(context,attrs);
		init();
	}

	public BlurImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getAttrs(context,attrs);
		init();
	}
	
	private void getAttrs(Context context, AttributeSet attrs){
		TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.blurImageView);
		this.radius=ta.getDimension(R.styleable.blurImageView_blurRadius, 40);
		this.hRadius=this.radius;
		this.vRadius=this.radius;
		ta.recycle();
	}

	private void init() {
		mPaint = new Paint();
		imageChanged();
	}

	@Override
	public void setImageBitmap(final Bitmap bm) {
		super.setImageBitmap(bm);
		this.bit = bm;
		if (DEBUG) {
			bit = BoxBlurFilter(bm);
			postInvalidate();
		} else {
			if(blurTask!=null){
				blurTask.cancel(true);
			}
			blurTask=new BitmapBlurTask(bit);
			blurTask.execute(0);
		}
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		imageChanged();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		imageChanged();
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		imageChanged();
	}

	public void setBlurRadius(float radius) {
		this.hRadius = radius;
		this.vRadius = radius;
		imageChanged();
	}

	private void imageChanged() {
		Drawable d = getDrawable();
		if (d != null&&d instanceof BitmapDrawable) {
			bit = ((BitmapDrawable) d).getBitmap();
			if (DEBUG) {
				bit = BoxBlurFilter(bit);
				postInvalidate();
			} else {
				if(blurTask!=null){
					blurTask.cancel(true);
				}
				blurTask=new BitmapBlurTask(bit);
				blurTask.execute(0);
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (bit != null) {
			canvas.save();
			Matrix matrix = getImageMatrix();
			if (matrix != null) {
				canvas.concat(matrix);
			}
			canvas.drawBitmap(bit, getPaddingLeft(), getPaddingTop(), mPaint);
			canvas.restore();
		}
	}
	
	private void blur(int[] in, int[] out, int width, int height,
			float radius) {
		int widthMinus1 = width - 1;
		int r = (int) radius;
		int tableSize = 2 * r + 1;
		int divide[] = new int[256 * tableSize];

		for (int i = 0; i < 256 * tableSize; i++)
			divide[i] = i / tableSize;

		int inIndex = 0;

		for (int y = 0; y < height; y++) {
			int outIndex = y;
			int ta = 0, tr = 0, tg = 0, tb = 0;

			for (int i = -r; i <= r; i++) {
				int rgb = in[inIndex + clamp(i, 0, width - 1)];
				ta += (rgb >> 24) & 0xff;
				tr += (rgb >> 16) & 0xff;
				tg += (rgb >> 8) & 0xff;
				tb += rgb & 0xff;
			}

			for (int x = 0; x < width; x++) {
				out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
						| (divide[tg] << 8) | divide[tb];

				int i1 = x + r + 1;
				if (i1 > widthMinus1)
					i1 = widthMinus1;
				int i2 = x - r;
				if (i2 < 0)
					i2 = 0;
				int rgb1 = in[inIndex + i1];
				int rgb2 = in[inIndex + i2];

				ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
				tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
				tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
				tb += (rgb1 & 0xff) - (rgb2 & 0xff);
				outIndex += height;
			}
			inIndex += width;
		}
	}

	private void blurFractional(int[] in, int[] out, int width, int height,
			float radius) {
		radius -= (int) radius;
		float f = 1.0f / (1 + 2 * radius);
		int inIndex = 0;

		for (int y = 0; y < height; y++) {
			int outIndex = y;

			out[outIndex] = in[0];
			outIndex += height;
			for (int x = 1; x < width - 1; x++) {
				int i = inIndex + x;
				int rgb1 = in[i - 1];
				int rgb2 = in[i];
				int rgb3 = in[i + 1];

				int a1 = (rgb1 >> 24) & 0xff;
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = rgb1 & 0xff;
				int a2 = (rgb2 >> 24) & 0xff;
				int r2 = (rgb2 >> 16) & 0xff;
				int g2 = (rgb2 >> 8) & 0xff;
				int b2 = rgb2 & 0xff;
				int a3 = (rgb3 >> 24) & 0xff;
				int r3 = (rgb3 >> 16) & 0xff;
				int g3 = (rgb3 >> 8) & 0xff;
				int b3 = rgb3 & 0xff;
				a1 = a2 + (int) ((a1 + a3) * radius);
				r1 = r2 + (int) ((r1 + r3) * radius);
				g1 = g2 + (int) ((g1 + g3) * radius);
				b1 = b2 + (int) ((b1 + b3) * radius);
				a1 *= f;
				r1 *= f;
				g1 *= f;
				b1 *= f;
				out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
				outIndex += height;
			}
			out[outIndex] = in[width - 1];
			inIndex += width;
		}
	}

	private int clamp(int x, int a, int b) {
		return (x < a) ? a : (x > b) ? b : x;
	}

	protected Bitmap BoxBlurFilter(Bitmap bmp) {
		long start = System.currentTimeMillis();
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < iterations; i++) {
			blur(inPixels, outPixels, width, height, hRadius);
			blur(outPixels, inPixels, height, width, vRadius);
		}
		blurFractional(inPixels, outPixels, width, height, hRadius);
		blurFractional(outPixels, inPixels, height, width, vRadius);
		bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
		inPixels = null;
		outPixels = null;
		System.gc();
		long end = System.currentTimeMillis();
		Log.d("timecust", "===================>>" + (end - start));
		return bitmap;
	}

	class BitmapBlurTask extends AsyncTask<Integer, Integer, Bitmap> {
		private Bitmap taskBit;

		public BitmapBlurTask(Bitmap bit) {
			this.taskBit = bit;
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {
			return BoxBlurFilter(taskBit);
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			bit=bitmap;
			postInvalidate();
		}
	}
}
