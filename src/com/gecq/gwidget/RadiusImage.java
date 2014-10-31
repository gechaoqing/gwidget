package com.gecq.gwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.widget.ImageView;
/****
 * RadiusImage
 * <br> Copyright (C) 2014 <b>Gechaoqing</b>
 * @author Gechaoqing
 * @since 2014-10-20
 * 
 */
public class RadiusImage extends ImageView {

	private Styles style;
	private Shader mBitmapShader;
	private RectF mRectF, mBorderRectF;
	private RoundRectShape mRoundRectShape;
	private ShapeDrawable mShapeDrawable, mShapeDrawableBorder;
	private ScaleType mScaleType;
	private Matrix mDrawMatrix, mMatrix;
	private float[] radius;

	private static final ScaleType[] sScaleTypeArray = { ScaleType.CENTER_CROP,
			ScaleType.LEFT_CENTER, ScaleType.RIGHT_CENTER,
			ScaleType.TOP_CENTER, ScaleType.BOTTOM_CENTER, ScaleType.TOP_LEFT,
			ScaleType.TOP_RIGHT, ScaleType.BOTTOM_LEFT, ScaleType.BOTTOM_RIGHT,
			ScaleType.CENTER_INSIDE };

	public static final int BORDER_STYLE_OUTSIDE = 0;
	public static final int BORDER_STYLE_OVER = 1;

	public RadiusImage(Context context) {
		super(context);
	}

	public RadiusImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		getStyles(context, attrs);
		init();
	}

	public RadiusImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		getStyles(context, attrs);
		init();
	}

	public enum ScaleType {
		CENTER_INSIDE(9), LEFT_CENTER(1), RIGHT_CENTER(2), TOP_CENTER(3), BOTTOM_CENTER(
				4), TOP_LEFT(5), TOP_RIGHT(6), BOTTOM_LEFT(7), BOTTOM_RIGHT(8), CENTER_CROP(
				0);
		ScaleType(int ni) {
			nativeInt = ni;
		}

		final int nativeInt;
	}

	private void getStyles(Context context, AttributeSet attrs) {
		mScaleType = ScaleType.CENTER_CROP;
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.radiusImage);
		style = new Styles();
		style.radius = typedArray.getDimension(R.styleable.radiusImage_radius,
				0);
		style.borderColor = typedArray.getColor(
				R.styleable.radiusImage_borderColor, 0);
		style.borderWidth = typedArray.getInt(
				R.styleable.radiusImage_borderWidth, 0);
		style.radiusBottomLeft = typedArray.getDimension(
				R.styleable.radiusImage_radiusBottomLeft, 0);
		style.radiusBottomRight = typedArray.getDimension(
				R.styleable.radiusImage_radiusBottomRight, 0);
		style.radiusTopLeft = typedArray.getDimension(
				R.styleable.radiusImage_radiusTopLeft, 0);
		style.radiusTopRight = typedArray.getDimension(
				R.styleable.radiusImage_radiusTopRight, 0);
		style.borderStyle = typedArray.getInt(
				R.styleable.radiusImage_borderStyle, 0);
		int index = typedArray.getInt(R.styleable.radiusImage_radiusScaleType,
				0);
		setScaleType(sScaleTypeArray[index]);
		style.src = getDrawable();
		style.alpha = typedArray.getFloat(R.styleable.radiusImage_imageAlpha, 1);
		typedArray.recycle();
	}

	private void setScaleType(ScaleType scaleType) {
		if (scaleType == null) {
			throw new NullPointerException();
		}
		if (mScaleType != scaleType) {
			mScaleType = scaleType;
			requestLayout();
			invalidate();
		}
	}

	private void init() {
		mRectF = new RectF();
		mBorderRectF = new RectF();
		mMatrix = new Matrix();
		mShapeDrawable = new ShapeDrawable();
		if (style.borderWidth > 0) {
			mShapeDrawableBorder = new ShapeDrawable();
		}
		int bw = style.borderWidth;
		int bs = style.borderStyle;
		bw = (bs == BORDER_STYLE_OUTSIDE ? bw : 0);
		if (style.radius != 0) {
			style.radius -= bw;
			style.radiusTopLeft = (style.radiusTopLeft == 0 ? style.radius
					: style.radiusTopLeft - bw);
			style.radiusTopRight = (style.radiusTopRight == 0 ? style.radius
					: style.radiusTopRight - bw);
			style.radiusBottomLeft = (style.radiusBottomLeft == 0 ? style.radius
					: style.radiusBottomLeft - bw);
			style.radiusBottomRight = (style.radiusBottomRight == 0 ? style.radius
					: style.radiusBottomRight - bw);
		}
		radius = new float[] { style.radiusTopLeft, style.radiusTopLeft,
				style.radiusTopRight, style.radiusTopRight,
				style.radiusBottomLeft, style.radiusBottomLeft,
				style.radiusBottomRight, style.radiusBottomRight };

		if (mShapeDrawableBorder != null) {
			float[] borderRadius = new float[radius.length];
			if (bs == BORDER_STYLE_OUTSIDE) {
				for (int br = 0; br < radius.length; br++) {
					borderRadius[br] = radius[br] + bw;
				}
			} else {
				bw = style.borderWidth;
				for (int br = 0; br < radius.length; br++) {
					borderRadius[br] = radius[br] - bw / 2;
				}
			}
			RoundRectShape mRoundRectShapeBorder = new RoundRectShape(
					borderRadius, mBorderRectF, null);
			mShapeDrawableBorder.setShape(mRoundRectShapeBorder);
			if (style.borderWidth > 0) {
				mShapeDrawableBorder.getPaint().setColor(style.borderColor);
				if (bs == BORDER_STYLE_OVER) {
					mShapeDrawableBorder.getPaint().setStyle(Style.STROKE);
					mShapeDrawableBorder.getPaint().setStrokeWidth(
							style.borderWidth);
				}
			}
		}

		if (style.src != null) {
			try {
				Bitmap bitmap = ((BitmapDrawable) style.src).getBitmap();
				style.srcHeight = bitmap.getHeight();
				style.srcWidth = bitmap.getWidth();
				mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
						Shader.TileMode.CLAMP);
				Paint mPaint = mShapeDrawable.getPaint();
				mPaint.setShader(mBitmapShader);
				mRoundRectShape = new RoundRectShape(radius, mRectF, radius);
				mShapeDrawable.setShape(mRoundRectShape);
				mShapeDrawable.setAlpha((int) (style.alpha * 255));
			} catch (Exception e) {

			}
		}
	}

	private class Styles {
		float radius, radiusTopLeft, radiusTopRight, radiusBottomLeft,
				radiusBottomRight, alpha;
		int srcWidth, srcHeight, borderColor, borderWidth, borderStyle;
		Drawable src;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		configureBounds();
		if (style.borderStyle == BORDER_STYLE_OUTSIDE
				&& mShapeDrawableBorder != null) {
			mShapeDrawableBorder.draw(canvas);
		}
		canvas.save();
		int bw = style.borderWidth;
		if (style.borderStyle == BORDER_STYLE_OUTSIDE
				&& mShapeDrawableBorder != null) {
			canvas.translate(bw, bw);
		}
		mShapeDrawable.draw(canvas);
		if (style.borderStyle == BORDER_STYLE_OVER
				&& mShapeDrawableBorder != null) {
			bw /= 2;
			canvas.translate(bw, bw);
			mShapeDrawableBorder.draw(canvas);
		}
		canvas.restore();

	}

	private void configureBounds() {
		if (style.src == null) {
			return;
		}
		int dwidth = style.srcWidth;
		int dheight = style.srcHeight;

		int vwidth = getWidth() - getPaddingLeft() - getPaddingRight();
		int vheight = getHeight() - getPaddingTop() - getPaddingBottom();
		int bw = style.borderWidth;
		if (mShapeDrawableBorder != null) {
			mBorderRectF.set(0, 0, vwidth, vheight);
			mShapeDrawableBorder.setBounds(0, 0, vwidth, vheight);
		}
		if (style.borderStyle == BORDER_STYLE_OUTSIDE) {
			vwidth -= (int) (bw * 2);
			vheight -= (int) (bw * 2);
		} else {
			float wi = vwidth - bw;
			float he = vheight - bw;
			mBorderRectF.set(0, 0, wi, he);
			mShapeDrawableBorder.setBounds(0, 0, (int) (wi), (int) (he));
		}

		float scale = 1.0f;
		float dx = 0;
		float dy = 0;

		boolean fits = (dwidth < 0 || vwidth == dwidth)
				&& (dheight < 0 || vheight == dheight);

		if (dwidth <= 0 || dheight <= 0) {
			style.src.setBounds(0, 0, vwidth, vheight);
			mDrawMatrix = null;
		} else {
			style.src.setBounds(0, 0, dwidth, dheight);
			if (fits) {
				mDrawMatrix = null;
			} else if (ScaleType.CENTER_INSIDE == mScaleType) {
				mDrawMatrix = mMatrix;

				if (dwidth <= vwidth && dheight <= vheight) {
					scale = 1.0f;
				} else {
					scale = Math.min((float) vwidth / (float) dwidth,
							(float) vheight / (float) dheight);
				}
				dx = (int) ((vwidth - dwidth * scale) * 0.5f + 0.5f);
				dy = (int) ((vheight - dheight * scale) * 0.5f + 0.5f);
				mDrawMatrix.setScale(scale, scale);
				mDrawMatrix.postTranslate(dx, dy);
			} else if (ScaleType.CENTER_CROP == mScaleType) {
				mDrawMatrix = mMatrix;

				if (dwidth * vheight > vwidth * dheight) {
					scale = (float) vheight / (float) dheight;
					dx = (vwidth - dwidth * scale) * 0.5f;
				} else {
					scale = (float) vwidth / (float) dwidth;
					dy = (vheight - dheight * scale) * 0.5f;
				}
				mDrawMatrix.setScale(scale, scale);
				mDrawMatrix.postTranslate((int) (dx + 0.5f), (int) dy);
			} else if (ScaleType.BOTTOM_LEFT == mScaleType) {
				mDrawMatrix = mMatrix;
				setScale(dwidth, dheight, vwidth, vheight, scale);
				dy = (vheight - dheight * scale);
				mDrawMatrix.postTranslate(0, (int) dy);
			} else if (ScaleType.BOTTOM_RIGHT == mScaleType) {
				mDrawMatrix = mMatrix;
				setScale(dwidth, dheight, vwidth, vheight, scale);
				dx = (vwidth - dwidth * scale);
				dy = (vheight - dheight * scale);
				mDrawMatrix.postTranslate((int) dx, (int) dy);
			} else if (ScaleType.TOP_LEFT == mScaleType) {
				mDrawMatrix = mMatrix;
				setScale(dwidth, dheight, vwidth, vheight, scale);
			} else if (ScaleType.TOP_RIGHT == mScaleType) {
				mDrawMatrix = mMatrix;
				setScale(dwidth, dheight, vwidth, vheight, scale);
				dx = (vwidth - dwidth * scale);
				mDrawMatrix.postTranslate((int) dx, (int) dy);
			} else if (ScaleType.BOTTOM_CENTER == mScaleType) {
				mDrawMatrix = mMatrix;
				setScale(dwidth, dheight, vwidth, vheight, scale);
				dx = (vwidth - dwidth * scale) * 0.5f;
				dy = (vheight - dheight * scale);
				mDrawMatrix.postTranslate((int) dx, (int) dy);
			} else if (ScaleType.TOP_CENTER == mScaleType) {
				mDrawMatrix = mMatrix;
				setScale(dwidth, dheight, vwidth, vheight, scale);
				dx = (vwidth - dwidth * scale) * 0.5f;
				mDrawMatrix.postTranslate((int) dx, (int) dy);
			} else if (ScaleType.LEFT_CENTER == mScaleType) {
				mDrawMatrix = mMatrix;
				setScale(dwidth, dheight, vwidth, vheight, scale);
				dy = (vheight - dheight * scale) * 0.5f;
				mDrawMatrix.postTranslate((int) dx, (int) dy);
			} else if (ScaleType.RIGHT_CENTER == mScaleType) {
				mDrawMatrix = mMatrix;
				setScale(dwidth, dheight, vwidth, vheight, scale);
				dx = (vwidth - dwidth * scale);
				dy = (vheight - dheight * scale) * 0.5f;
				mDrawMatrix.postTranslate((int) dx, (int) dy);
			}
		}
		mBitmapShader.setLocalMatrix(mDrawMatrix);
		mRectF.set(0, 0, vwidth, vheight);
		mShapeDrawable.setBounds(0, 0, (int) (vwidth), (int) (vheight));
	}

	private void setScale(int dw, int dh, int vw, int vh, float scale) {
		if (dw < vw || dh < vh) {
			scale = Math.max((float) vw / (float) dw, (float) vh / (float) dh);
			mDrawMatrix.setScale(scale, scale);
		}
	}

}
