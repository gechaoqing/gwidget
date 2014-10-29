package com.gwsoft.globalLibrary.gwidget;




import com.gwsoft.globalLibrary.gwidget.utils.SvgParserHelper;
import com.imusic.iting.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SvgPathView extends View {

	private Path mPath;
	private Paint mPaint;
	private SvgParserHelper mPareser;

	private String icon;
	private float iconSize;
	private ColorStateList iconColor;
	private RectF mRectF;
	private float mDensity;
	private final float size=120;
	private float width,height;
	private int scaleType=SCALE_WITH_HEIGHT;
	
	private static final int SCALE_CENTER=0;
	private static final int SCALE_WITH_WIDTH=1;
	private static final int SCALE_WITH_HEIGHT=2;

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
		this.icon=path;
		draw();
	}

	public void setIconPath(int resid) {
		this.icon=getResources().getString(resid);
		draw();
	}
	
	public void setIconColor(int resid){
		ColorStateList colors=getResources().getColorStateList(resid);
		mPaint.setColor(colors.getColorForState(getDrawableState(), Color.BLACK));
		invalidate();
	}
	
	public void setIconColor(String color){
		mPaint.setColor(Color.parseColor(color));
		invalidate();
	}
	
	private void draw(){
		mPaint.setColor(this.iconColor.getColorForState(getDrawableState(), Color.BLACK));
		doPath();
		mPath.computeBounds(mRectF, true);
		float dwidth=mRectF.width();
		float dheight=mRectF.height();
		width=height=size*iconSize;
		float scale=1.0f;
		float dx=0,dy=0;
		switch (scaleType) {
		case SCALE_CENTER:
			if(width<dwidth||height<dheight)
			{
				scale=Math.min(width/dwidth,height/dheight);
				getDxDyBigger(dx, dy, dwidth, dheight, scale);
			}else{
				scale=Math.min(dwidth/width,dheight/height);
				getDxDySmaller(dx,dy,dwidth,dheight,scale);
			}
			break;
		case SCALE_WITH_HEIGHT:
			if(height<dheight)
			{
				scale=height/dheight;
				width=dwidth*scale;
				getDxDyBigger(dx, dy, dwidth, dheight, scale);
			}else{
				scale=dheight/height;
				width=dwidth/scale;
				getDxDySmaller(dx,dy,dwidth,dheight,scale);
			}
			
			break;
		case SCALE_WITH_WIDTH:
			if(width<dwidth)
			{
				scale=width/dwidth;
				height=dheight*scale;
				getDxDyBigger(dx, dy, dwidth, dheight, scale);
				
			}else{
				scale=dwidth/width;
				height=dheight/scale;
				getDxDySmaller(dx,dy,dwidth,dheight,scale);
				
			}
			break;
		}
		mMatrix.setScale(scale, scale);
		mMatrix.postTranslate(dx, dy);
		mPath.transform(mMatrix);
		requestLayout();
		invalidate();
	}

	private void init() {
		mPaint = new Paint();
		mPath = new Path();
		mRectF = new RectF();
		mPareser = new SvgParserHelper(icon, 0);
		mPaint.setDither(true);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setAntiAlias(true);
		mMatrix = new Matrix();
		draw();
	}
	@Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if(iconColor!=null&&iconColor.isStateful())
        {
        	mPaint.setColor(this.iconColor.getColorForState(getDrawableState(), Color.BLACK));
        	invalidate();
        }
	}
	
	private void getDxDySmaller(float dx,float dy,float dwidth,float dheight,float scale){
		dx = (int) ((dwidth - width * scale) * 0.5f + 0.5f);
		dy = (int) ((dheight - height * scale) * 0.5f + 0.5f);
	}
	private void getDxDyBigger(float dx,float dy,float dwidth,float dheight,float scale){
		dx = (int) ((width - dwidth * scale) * 0.5f + 0.5f);
		dy = (int) ((height - dheight * scale) * 0.5f + 0.5f);
	}

	private void getAttrs(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.iconView);
		mDensity = context.getResources().getDisplayMetrics().density;
		this.icon = typedArray.getString(R.styleable.iconView_icon);
		this.iconSize = typedArray.getDimension(R.styleable.iconView_iconSize,
				12.0f * mDensity);
		this.iconSize *= 0.01f;
//		this.iconColor = typedArray.getColor(R.styleable.iconView_iconColor,
//				Color.BLACK);
		this.iconColor =typedArray.getColorStateList(R.styleable.iconView_iconColor);
		this.scaleType=typedArray.getInt(R.styleable.iconView_iconScaleType, SCALE_WITH_HEIGHT);
		typedArray.recycle();
	}

	Matrix mMatrix;

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPath(mPath, mPaint);
	}

	private void doPath() {
		doPath(this.icon);
	}

	private void doPath(String s) {
		if (s == null) {
			return;
		}
//		System.out.println("==============================================");
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
//					System.out.println("mPath.rMoveTo(" + x + "f," + y + "f);");
					lastX += x;
					lastY += y;
				} else {
					subPathStartX = x;
					subPathStartY = y;
					mPath.moveTo(x, y);
//					System.out.println("mPath.moveTo(" + x + "f," + y + "f);");
					lastX = x;
					lastY = y;
				}
				break;
			}
			case 'Z':
			case 'z': {
				mPath.close();
				mPath.moveTo(subPathStartX, subPathStartY);
//				System.out.println("mPath.close();");
//				System.out.println("mPath.moveTo(" + subPathStartX + "f,"
//						+ subPathStartY + "f);");

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
//					System.out.println("mPath.rLineTo(" + x + "f," + y + "f);");
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
//					System.out.println("mPath.rLineTo(" + x + "f," + "0);");
					lastX += x;
				} else {
					mPath.lineTo(x, lastY);
//					System.out.println("mPath.lineTo(" + x + "f," + lastY
//							+ "f);");
					lastX = x;
				}
				break;
			}
			case 'V':
			case 'v': {
				float y = mPareser.nextFloat();
				if (cmd == 'v') {
					mPath.rLineTo(0, y);
//					System.out.println("mPath.rLineTo(0," + y + "f);");
					lastY += y;
				} else {
					mPath.lineTo(lastX, y);
//					System.out.println("mPath.lineTo(" + lastX + "f," + y
//							+ "f);");
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
//				System.out.println("mPath.cubicTo(" + x1 + "f," + y1 + "f,"
//						+ x2 + "f," + y2 + "f," + x + "f," + y + "f);");
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
//				System.out.println("mPath.cubicTo(" + x1 + "f," + y1 + "f,"
//						+ x2 + "f," + y2 + "f," + x + "f," + y + "f);");
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
					return;
				}
				if (cmd == 'q') {
					x += lastX;
					y += lastY;
					x1 += lastX;
					y1 += lastY;
				}
				mPath.quadTo(x1, y1, x, y);
//				System.out.println("mPath.quadTo(" + x1 + "f," + y1 + "f," + x
//						+ "f," + y + "f);");
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
					return;
				}
				if (cmd == 't') {
					x += lastX;
					y += lastY;
				}
				mPath.quadTo(x1, y1, x, y);
//				System.out.println("mPath.quadTo(" + x1 + "f," + y1 + "f," + x
//						+ "f," + y + "f);");
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
	}

	private static void drawArc(Path p, float lastX, float lastY, float x,
			float y, float rx, float ry, float theta, int largeArc, int sweepArc) {
		// todo - not implemented yet, may be very hard to do using Android
		// drawing facilities.
	}

	@Override
	protected void onMeasure(int wms, int hms) {
		setMeasuredDimension((int)width+1, (int)height+1);
	}

}
