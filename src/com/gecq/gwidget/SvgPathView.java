package com.gecq.gwidget;

import com.gecq.gwidget.utils.SvgParserHelper;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/****
 * SvgPathView <br>
 * Copyright (C) 2014 <b>Gechaoqing</b>
 * 
 * @author Gechaoqing
 * @since 2014-10-20
 * 
 */
public class SvgPathView extends View {

	protected SvgParserHelper mPareser;

	protected float iconSize;
	protected float mDensity;
	protected final float size = 120;
	protected int scaleType = SCALE_CENTER;
	protected float width, height;


	protected static final int SCALE_CENTER = 0;
	protected static final int SCALE_WITH_WIDTH = 1;
	protected static final int SCALE_WITH_HEIGHT = 2;

	protected class PathDataSet {
		protected Path mPath;
		protected Paint mPaint;
		protected Matrix mMatrix;
		protected RectF mRectF;
		protected float scale = 1.0f;
		protected float mWidth = 0, mHeight = 0;
		protected String icon;
		private ColorStateList iconColor;

		protected PathDataSet() {
			mPaint = new Paint();
			mPaint = roundPaint(mPaint);
			mMatrix = new Matrix();
			mPath = new Path();
			mRectF = new RectF();
		}
		protected void computeDatas() {
			computeDatas(scaleType);
		}
		
		public void setIconColor(ColorStateList iconColor,int defaultColor,int[] state){
			this.iconColor=iconColor;
			mPaint.setColor(this.iconColor.getColorForState(state,defaultColor));
		}
		
		public ColorStateList getIconColor(){
			return this.iconColor;
		}

		protected void computeDatas(int scaleType) {
			if(icon==null){
				return;
			}
			mPath = doPath(mPath, icon);
			if(!mRectF.isEmpty()){
				mRectF.setEmpty();
			}
			mPath.computeBounds(mRectF, true);
			float dwidth = mRectF.width();
			float dheight = mRectF.height();
			switch (scaleType) {
			case SCALE_CENTER:
				scale = Math.min(width / dwidth, height / dheight);
				break;
			case SCALE_WITH_HEIGHT:
				scale = height / dheight;
				width = dwidth * scale;

				break;
			case SCALE_WITH_WIDTH:
				scale = width / dwidth;
				height = dheight * scale;
				break;
			}
			transform();
		}
		
		private void transform(){
			mMatrix.reset();
			mMatrix.setTranslate(-mRectF.left, -mRectF.top);
			mMatrix.postScale(scale, scale);
			mPath.transform(mMatrix);
			mWidth=width;
			mHeight=height;
		}
	}

	public SvgPathView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getAttrs(context, attrs);
	}

	public SvgPathView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		getAttrs(context, attrs);
	}

	public SvgPathView(Context context) {
		super(context);
	}

	private void getAttrs(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.svgView);
		mDensity = context.getResources().getDisplayMetrics().density;
		this.iconSize = typedArray.getDimension(R.styleable.svgView_iconSize,
				12.0f * mDensity);
		this.iconSize *= 0.01f;
		this.scaleType = typedArray.getInt(R.styleable.svgView_iconScaleType,
				SCALE_WITH_HEIGHT);
		typedArray.recycle();
		init();
	}

	private void init() {
		mPareser = new SvgParserHelper();
		width = height = size * iconSize;
	}

	protected Paint roundPaint(Paint mPaint) {
		mPaint.setDither(true);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setAntiAlias(true);
		return mPaint;
	}

	protected int C(String str) {
		return Color.parseColor(str);
	}

	protected Path doPath(Path mPath, String s) {
		if(mPath!=null){
			mPath.reset();
		}
		if (s == null) {
			return mPath;
		}
		mPareser.load(s, 0);
		// System.out.println("==============================================");
		int n = s.length();
		mPareser.skipWhitespace();
		float lastX = 0;
		float lastY = 0;
		float lastX1 = 0;
		float lastY1 = 0;
		float subPathStartX = 0;
		float subPathStartY = 0;
		char prevCmd = 0;
		while (mPareser.pos < n) {
			char cmd = s.charAt(mPareser.pos);
			switch (cmd) {
			case '-':
			case '+':
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				if (prevCmd == 'm' || prevCmd == 'M') {
					cmd = (char) (((int) prevCmd) - 1);
					break;
				} else if (prevCmd == 'c' || prevCmd == 'C') {
					cmd = prevCmd;
					break;
				} else if (prevCmd == 'l' || prevCmd == 'L') {
					cmd = prevCmd;
					break;
				}
			default: {
				mPareser.advance();
				prevCmd = cmd;
			}
			}

			boolean wasCurve = false;
			switch (cmd) {
			case 'M':
			case 'm': {
				float x = mPareser.nextFloat();
				float y = mPareser.nextFloat();
				if (cmd == 'm') {
					subPathStartX += x;
					subPathStartY += y;
					mPath.rMoveTo(x, y);
					// System.out.println("mPath.rMoveTo(" + x + "f," + y +
					// "f);");
					lastX += x;
					lastY += y;
				} else {
					subPathStartX = x;
					subPathStartY = y;
					mPath.moveTo(x, y);
					// System.out.println("mPath.moveTo(" + x + "f," + y +
					// "f);");
					lastX = x;
					lastY = y;
				}
				break;
			}
			case 'Z':
			case 'z': {
				mPath.close();
				mPath.moveTo(subPathStartX, subPathStartY);
				// System.out.println("mPath.close();");
				// System.out.println("mPath.moveTo(" + subPathStartX + "f,"
				// + subPathStartY + "f);");

				lastX = subPathStartX;
				lastY = subPathStartY;
				lastX1 = subPathStartX;
				lastY1 = subPathStartY;
				wasCurve = true;
				break;
			}
			case 'L':
			case 'l': {
				float x = mPareser.nextFloat();
				float y = mPareser.nextFloat();
				if (cmd == 'l') {
					mPath.rLineTo(x, y);
					// System.out.println("mPath.rLineTo(" + x + "f," + y +
					// "f);");
					lastX += x;
					lastY += y;
				} else {
					mPath.lineTo(x, y);
					lastX = x;
					lastY = y;
				}
				break;
			}
			case 'H':
			case 'h': {
				float x = mPareser.nextFloat();
				if (cmd == 'h') {
					mPath.rLineTo(x, 0);
					// System.out.println("mPath.rLineTo(" + x + "f," + "0);");
					lastX += x;
				} else {
					mPath.lineTo(x, lastY);
					// System.out.println("mPath.lineTo(" + x + "f," + lastY
					// + "f);");
					lastX = x;
				}
				break;
			}
			case 'V':
			case 'v': {
				float y = mPareser.nextFloat();
				if (cmd == 'v') {
					mPath.rLineTo(0, y);
					// System.out.println("mPath.rLineTo(0," + y + "f);");
					lastY += y;
				} else {
					mPath.lineTo(lastX, y);
					// System.out.println("mPath.lineTo(" + lastX + "f," + y
					// + "f);");
					lastY = y;
				}
				break;
			}
			case 'C':
			case 'c': {
				wasCurve = true;
				float x1 = mPareser.nextFloat();
				float y1 = mPareser.nextFloat();
				float x2 = mPareser.nextFloat();
				float y2 = mPareser.nextFloat();
				float x = mPareser.nextFloat();
				float y = mPareser.nextFloat();
				if (cmd == 'c') {
					x1 += lastX;
					x2 += lastX;
					x += lastX;
					y1 += lastY;
					y2 += lastY;
					y += lastY;
				}
				mPath.cubicTo(x1, y1, x2, y2, x, y);
				// System.out.println("mPath.cubicTo(" + x1 + "f," + y1 + "f,"
				// + x2 + "f," + y2 + "f," + x + "f," + y + "f);");
				lastX1 = x2;
				lastY1 = y2;
				lastX = x;
				lastY = y;
				break;
			}
			case 'S':
			case 's': {
				wasCurve = true;
				float x2 = mPareser.nextFloat();
				float y2 = mPareser.nextFloat();
				float x = mPareser.nextFloat();
				float y = mPareser.nextFloat();
				if (cmd == 's') {
					x2 += lastX;
					x += lastX;
					y2 += lastY;
					y += lastY;
				}
				float x1 = 2 * lastX - lastX1;
				float y1 = 2 * lastY - lastY1;
				mPath.cubicTo(x1, y1, x2, y2, x, y);
				// System.out.println("mPath.cubicTo(" + x1 + "f," + y1 + "f,"
				// + x2 + "f," + y2 + "f," + x + "f," + y + "f);");
				lastX1 = x2;
				lastY1 = y2;
				lastX = x;
				lastY = y;
				break;
			}
			case 'A':
			case 'a': {
				float rx = mPareser.nextFloat();
				float ry = mPareser.nextFloat();
				float theta = mPareser.nextFloat();
				int largeArc = (int) (mPareser.nextFloat());
				int sweepArc = (int) (mPareser.nextFloat());
				float x = mPareser.nextFloat();
				float y = mPareser.nextFloat();
				drawArc(mPath, lastX, lastY, x, y, rx, ry, theta, largeArc,
						sweepArc);
				lastX = x;
				lastY = y;
				break;
			}
			case 'Q':
			case 'q':
				float x1 = mPareser.nextFloat();
				float y1 = mPareser.nextFloat();
				float x = mPareser.nextFloat();
				float y = mPareser.nextFloat();
				if (Float.isNaN(y)) {
					// Log.e(TAG,
					// "Bad path coords for "+((char)cmd)+" path segment");
					return mPath;
				}
				if (cmd == 'q') {
					x += lastX;
					y += lastY;
					x1 += lastX;
					y1 += lastY;
				}
				mPath.quadTo(x1, y1, x, y);
				// System.out.println("mPath.quadTo(" + x1 + "f," + y1 + "f," +
				// x
				// + "f," + y + "f);");
				lastX1 = x1;
				lastY1 = y1;
				lastX = x;
				lastY = y;
				break;

			// Smooth quadratic bezier
			case 'T':
			case 't':
				x1 = 2 * lastX - lastX1;
				y1 = 2 * lastY - lastY1;
				x = mPareser.nextFloat();
				y = mPareser.nextFloat();
				if (Float.isNaN(y)) {
					// Log.e(TAG,
					// "Bad path coords for "+((char)cmd)+" path segment");
					return mPath;
				}
				if (cmd == 't') {
					x += lastX;
					y += lastY;
				}
				mPath.quadTo(x1, y1, x, y);
				// System.out.println("mPath.quadTo(" + x1 + "f," + y1 + "f," +
				// x
				// + "f," + y + "f);");
				lastX1 = x1;
				lastY1 = y1;
				lastX = x;
				lastY = y;
				break;
			}
			if (!wasCurve) {
				lastX1 = lastX;
				lastY1 = lastY;
			}
			mPareser.skipWhitespace();

		}
		return mPath;
	}

	private static void drawArc(Path p, float lastX, float lastY, float x,
			float y, float rx, float ry, float theta, int largeArc, int sweepArc) {
		// todo - not implemented yet, may be very hard to do using Android
		// drawing facilities.
	}

}
