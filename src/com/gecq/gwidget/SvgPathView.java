package com.gecq.gwidget;

import com.gecq.gwidget.utils.SvgParserHelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class SvgPathView extends View {

	private Path mPath;
	private Paint mPaint;
	private SvgParserHelper mPareser;

	private String icon;
	private float iconSize;
	private int iconColor;
	private float width=3,height=3;

	public SvgPathView(Context context) {
		super(context);
	}

	public SvgPathView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		getAttrs(context, attrs);
		init();
	}

	public SvgPathView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getAttrs(context, attrs);
		init();
	}

	public void setIconPath(String path) {
		doPath(path);
		requestLayout();
		invalidate();
	}

	public void setIconPath(int resid) {
		doPath(resid);
		requestLayout();
		invalidate();
	}

	private void init() {
		mPaint = new Paint();
		mPath = new Path();
		mPareser = new SvgParserHelper(icon, 0);
		mPaint.setColor(this.iconColor);
		mPaint.setDither(true);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setPathEffect(new CornerPathEffect(5));
		mPaint.setAntiAlias(true);
		mMatrix=new Matrix();
		jugXY();
		mPareser.pos=0;
		doPath();
		
	}

	private void getAttrs(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.iconView);
		this.icon = typedArray.getString(R.styleable.iconView_icon);
		this.icon = this.icon.replace('$', '-');
		this.iconSize = typedArray.getDimension(R.styleable.iconView_iconSize,
				12);
		this.iconColor = typedArray.getColor(R.styleable.iconView_iconColor,
				Color.BLACK);
		typedArray.recycle();
	}

	float minX, minY, maxX, maxY, scale = 1.0f;
	Matrix mMatrix;

	@Override
	protected void onDraw(Canvas canvas) {
		float tw = (maxX - minX);
		float th = (maxY - minY);
		if (tw <= width && th <= height) {
			scale = 1.0f;
		} else {
			scale = (float) ((Math
					.min((float) (width-getPaddingLeft()-getPaddingRight()) / (float) tw, (float) (height-getPaddingBottom()-getPaddingTop()) / (float) th)));
		}
		canvas.save();
		if(mMatrix!=null){
			int dx = (int) ((width - tw * scale+getPaddingLeft()) * 0.5f + 0.5f);
			int dy = (int) ((height - th * scale) * 0.5f + 0.5f);
			mMatrix.setScale(scale, scale);
			mMatrix.postTranslate(dx, dy);
			mPath.transform(mMatrix);
		}
		canvas.drawPath(mPath, mPaint);
		canvas.restore();
	}

	private void doPath(int resid) {
		doPath(getResources().getString(resid));
	}

	private void jugXY() {
		doPath(icon, true);
	}

	private void doPath() {
		doPath(this.icon);
	}
	
	private void doPath(String s) {
		doPath(s, false);
	}

	private void doPath(String s, boolean jug) {
		if (s == null) {
			return;
		}
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
				float x = mPareser.nextFloat() ;
				float y = mPareser.nextFloat() ;
				if (cmd == 'm') {
					subPathStartX += x;
					subPathStartY += y;
					if (!jug)
						mPath.rMoveTo(x, y);
					lastX += x;
					lastY += y;
				} else {
					subPathStartX = x;
					subPathStartY = y;
					if (!jug)
						mPath.moveTo(x, y);
					lastX = x;
					lastY = y;
				}
				break;
			}
			case 'Z':
			case 'z': {
				if (!jug) {
					mPath.close();
					mPath.moveTo(subPathStartX, subPathStartY);
				}

				lastX = subPathStartX;
				lastY = subPathStartY;
				lastX1 = subPathStartX;
				lastY1 = subPathStartY;
				wasCurve = true;
				break;
			}
			case 'L':
			case 'l': {
				float x = mPareser.nextFloat() ;
				float y = mPareser.nextFloat() ;
				if (cmd == 'l') {
					if (!jug)
						mPath.rLineTo(x, y);
					lastX += x;
					lastY += y;
				} else {
					if (!jug)
						mPath.lineTo(x, y);
					lastX = x;
					lastY = y;
				}
				break;
			}
			case 'H':
			case 'h': {
				float x = mPareser.nextFloat() ;
				if (cmd == 'h') {
					if (!jug)
						mPath.rLineTo(x, 0);
					lastX += x;
				} else {
					if (!jug)
						mPath.lineTo(x, lastY);
					lastX = x;
				}
				break;
			}
			case 'V':
			case 'v': {
				float y = mPareser.nextFloat() ;
				if (cmd == 'v') {
					if (!jug)
						mPath.rLineTo(0, y);
					lastY += y;
				} else {
					if (!jug)
						mPath.lineTo(lastX, y);
					lastY = y;
				}
				break;
			}
			case 'C':
			case 'c': {
				wasCurve = true;
				float x1 = mPareser.nextFloat() ;
				float y1 = mPareser.nextFloat() ;
				float x2 = mPareser.nextFloat() ;
				float y2 = mPareser.nextFloat() ;
				float x = mPareser.nextFloat() ;
				float y = mPareser.nextFloat() ;
				if (cmd == 'c') {
					x1 += lastX;
					x2 += lastX;
					x += lastX;
					y1 += lastY;
					y2 += lastY;
					y += lastY;
				}
				if (!jug)
					mPath.cubicTo(x1, y1, x2, y2, x, y);
				lastX1 = x2;
				lastY1 = y2;
				lastX = x;
				lastY = y;
				break;
			}
			case 'S':
			case 's': {
				wasCurve = true;
				float x2 = mPareser.nextFloat() ;
				float y2 = mPareser.nextFloat() ;
				float x = mPareser.nextFloat() ;
				float y = mPareser.nextFloat() ;
				if (cmd == 's') {
					x2 += lastX;
					x += lastX;
					y2 += lastY;
					y += lastY;
				}
				float x1 = 2 * lastX - lastX1;
				float y1 = 2 * lastY - lastY1;
				if (!jug)
					mPath.cubicTo(x1, y1, x2, y2, x, y);
				lastX1 = x2;
				lastY1 = y2;
				lastX = x;
				lastY = y;
				break;
			}
			case 'A':
			case 'a': {
				float rx = mPareser.nextFloat() ;
				float ry = mPareser.nextFloat() ;
				float theta = mPareser.nextFloat() ;
				int largeArc = (int) (mPareser.nextFloat() );
				int sweepArc = (int) (mPareser.nextFloat() );
				float x = mPareser.nextFloat();
				float y = mPareser.nextFloat();
				if (!jug)
					drawArc(mPath, lastX, lastY, x, y, rx, ry, theta, largeArc,
							sweepArc);
				lastX = x;
				lastY = y;
				break;
			}
			case 'Q':
			case 'q':
				float x1 = mPareser.nextFloat() ;
				float y1 = mPareser.nextFloat() ;
				float x = mPareser.nextFloat() ;
				float y = mPareser.nextFloat() ;
				if (Float.isNaN(y)) {
					// Log.e(TAG,
					// "Bad path coords for "+((char)cmd)+" path segment");
					return;
				}
				if (cmd == 'q') {
					x += lastX;
					y += lastY;
					x1 += lastX;
					y1 += lastY;
				}
				if (!jug)
					mPath.quadTo(x1, y1, x, y);
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
				x = mPareser.nextFloat() ;
				y = mPareser.nextFloat() ;
				if (Float.isNaN(y)) {
					// Log.e(TAG,
					// "Bad path coords for "+((char)cmd)+" path segment");
					return;
				}
				if (cmd == 't') {
					x += lastX;
					y += lastY;
				}
				if (!jug)
					mPath.quadTo(x1, y1, x, y);
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
			if(jug){
				if (lastX1 <= minX || lastX <= minX) {
					minX = lastX1 < lastX ? lastX1 : lastX;
				}
				if (lastX1 >= maxX || lastX >= maxX) {
					maxX = lastX1 > lastX ? lastX1 : lastX;
				}

				if (lastY1 <= minY || lastY <= minY) {
					minY = lastY1 < lastY ? lastY1 : lastY;
				}

				if (lastY1 >= maxY || lastY >= maxY) {
					maxY = lastY1 > lastY ? lastY1 : lastY;
				}
			}
			
		}
	}

	private static void drawArc(Path p, float lastX, float lastY, float x,
			float y, float rx, float ry, float theta, int largeArc, int sweepArc) {
		// todo - not implemented yet, may be very hard to do using Android
		// drawing facilities.
	}

	@Override
	protected void onMeasure(int wms, int hms) {
		width*=iconSize;
		height*=iconSize;
		setMeasuredDimension((int)(width),(int)height);
	}

}
