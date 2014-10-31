package com.gecq.gwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/****
 * SwitchButton
 * <br> Copyright (C) 2014 <b>Gechaoqing</b>
 * @author Gechaoqing
 * @since 2014-10-20
 * 
 */
public class SwitchButton extends View {

	private RectF innerRect;
	private Paint mPaintOn, mPaintOff, mPaintThumb,mPaintThumbOn;

	private float mOnProgress;
	private float mLastX;
	private float mDensity = 1.5f;
	private boolean mIsOn = false;
	private boolean mHasMoved = false;
	private boolean mIsMoving = false;
	private OnToggleChangedListener mOnToggleChangedListener;
	
	private static final float WIDTH=45;
	private static final float HEIGHT=25;
	private static final float PADDING=2;
	
	private float width,height,padding;
	private float thumbRadius = 20,thumbRadiusReal;
	private int thumbX;
	private float thumbY;

	private static final String COLOR_GREEN = "#259b24";
	private static final String COLOR_GRAY = "#9e9e9e";
	private Styles style;
	
	private static final int THUMB_STYLE_PADDING=0;

	public SwitchButton(Context context) {
		super(context);
		style=new Styles();
		style.offColor=Color.parseColor(COLOR_GRAY);
		style.onColor=Color.parseColor(COLOR_GREEN);
		style.thumbOnColor=-1;
		style.thumbOffColor=Color.WHITE;
		style.thumbStyle=THUMB_STYLE_PADDING;
		inital();
	}
	
	public SwitchButton(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		getStyles(context,attrs);
		inital();
	}

	public SwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		getStyles(context,attrs);
		inital();
	}
	
	public void setOffColor(int color){
		getStyles().offColor=color;
	}
	
	public void setOnColor(int color){
		getStyles().onColor=color;
	}
	
	public void setThumbOffColor(int color){
		getStyles().thumbOffColor=color;
	}
	
	public void setThumbOnColor(int color){
		getStyles().thumbOnColor=color;
	}
	
	public void setThumbStyle(int style){
		getStyles().thumbStyle=style;
	}
	
	private Styles getStyles(){
		return style==null?new Styles():style;
	}
	
	private void getStyles(Context context,AttributeSet attrs){
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.switchButton);
		style=new Styles();
		style.offColor=typedArray.getColor(R.styleable.switchButton_offColor, Color.parseColor(COLOR_GRAY));
		style.onColor=typedArray.getColor(R.styleable.switchButton_onColor, Color.parseColor(COLOR_GREEN));
		style.thumbOnColor=typedArray.getColor(R.styleable.switchButton_thumbOnColor, -1);
		style.thumbOffColor=typedArray.getColor(R.styleable.switchButton_thumbOffColor, Color.WHITE);
		style.thumbStyle=typedArray.getInt(R.styleable.switchButton_thumbStyle, 0);
		typedArray.recycle();
	}
	
	private class Styles{
		int onColor,offColor,thumbOnColor,thumbOffColor,thumbStyle;
	}

	private void inital() {
		mPaintOn = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintOff = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintThumb = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintThumbOn=new Paint(Paint.ANTI_ALIAS_FLAG);
		mDensity = getContext().getResources().getDisplayMetrics().density;
		mPaintOn.setColor(style.onColor);
		mPaintOff.setColor(style.offColor);
		mPaintThumb.setColor(style.thumbOffColor);
		if(style.thumbOnColor!=-1){
			mPaintThumbOn.setColor(style.thumbOnColor);
		}
		width=(WIDTH+PADDING*2)*mDensity;
		height=(HEIGHT+PADDING*2)*mDensity;
		padding=PADDING*mDensity;
		
		innerRect = new RectF();
		innerRect.set(0, 0, (WIDTH)*mDensity,(HEIGHT)*mDensity);
		thumbRadius = (int) (innerRect.height()/ 2+0.5*mDensity);
		thumbX=getLeftEdge();
		thumbY=(float)(thumbRadius+innerRect.top-0.25*mDensity);
		if(style.thumbStyle==THUMB_STYLE_PADDING){
			thumbRadiusReal=(float) (thumbRadius-3);
		}else{
			thumbRadiusReal=thumbRadius+1;
		}
		
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaintOn.setAlpha((int) (255 * mOnProgress));
//		mPaintOff.setAlpha((int)(255*(1-mOnProgress)));
		canvas.save();
		canvas.translate(padding, padding);
		canvas.drawRoundRect(innerRect, thumbRadius , thumbRadius ,
				mPaintOff);
		canvas.drawRoundRect(innerRect, thumbRadius , thumbRadius ,
				mPaintOn);
		if(style.thumbOnColor!=-1){
			mPaintThumb.setAlpha((int)(255*(1-mOnProgress)));
			canvas.drawCircle(thumbX,thumbY ,thumbRadiusReal , mPaintThumb);
			mPaintThumbOn.setAlpha((int)(255*mOnProgress));
			canvas.drawCircle(thumbX, thumbY,thumbRadiusReal , mPaintThumbOn);
		}else{
			canvas.drawCircle(thumbX,thumbY,thumbRadiusReal , mPaintThumb);
		}
		canvas.restore();
	}
	
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		setMeasuredDimension((int)width, (int)height);
	}

	private void updateOnProgress() {
		this.mOnProgress = (float) (thumbX-getLeftEdge()) /(getRightEdge()-getLeftEdge());
		if (this.mOnProgress < 0) {
			this.mOnProgress = 0;
		}
		if (this.mOnProgress > 1) {
			this.mOnProgress = 1;
		}
	}

	private void thumbMoveBy(float deltX) {
		this.thumbX += deltX;
		if(mIsMoving&&isCollision()){
			mIsMoving = false;
			mHasMoved = false;
			mIsOn = !mIsOn;
			if (mOnToggleChangedListener != null) {
				mOnToggleChangedListener.onToggleChanged(mIsOn, this);
			}
		}
		if(this.thumbX<getLeftEdge()){
			this.thumbX=getLeftEdge();
		}
		if(this.thumbX>getRightEdge()){
			this.thumbX=getRightEdge();
		}
	}
	
	private boolean isCollision() {
		return this.thumbX<getLeftEdge() || this.thumbX>getRightEdge();
	}

	private boolean thumbIsOnLeftEdge() {
		return this.thumbX == getLeftEdge();
	}

	private int getLeftEdge() {
		return (int) (thumbRadius - 1 / 20 * thumbRadius+innerRect.left);
	}

	private boolean thumbIsOnRightEdge() {
		return this.thumbX == getRightEdge();
	}

	private int getRightEdge() {
		return (int) (innerRect.right- thumbRadius + (float)(1 / 20 * thumbRadius));
	}
	
	public void setIsOn(boolean isOn) {
		if (isOn == !this.mIsOn) {
			mIsMoving=true;
			postInvalidate();
			if (isOn) {
				goingOn();
			} else {
				goingOff();
			}
		}
	}
	
	private void goingOn() {
		postDelayed(new Runnable() {

			@Override
			public void run() {
				if (mIsMoving) {
					thumbMoveBy(2 * mDensity);
					postInvalidate();
					updateOnProgress();
					goingOn();
				}
			}
		}, 15);
	}

	private void goingOff() {
		postDelayed(new Runnable() {

			@Override
			public void run() {
				if (mIsMoving) {
					thumbMoveBy(-2 * mDensity);
					postInvalidate();
					updateOnProgress();
					goingOff();
				}
			}
		}, 15);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.mIsMoving) {
			return true;
		}
		int action = event.getAction();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastX = event.getX();
			break;

		case MotionEvent.ACTION_MOVE:

			float x = event.getX();
			thumbMoveBy(x - mLastX);
			mLastX = x;

			if (!thumbIsOnLeftEdge() && !thumbIsOnRightEdge()) {
				mHasMoved = true;
			}

			updateOnProgress();
			break;

		case MotionEvent.ACTION_UP:
			if (!this.mHasMoved) {
				mIsMoving = true;
				if (mIsOn) {
					 goingOff();
				} else {
					 goingOn();
				}
			} else {
				if (mIsOn) {
					if (thumbIsOnRightEdge()) {
						mHasMoved = false;
						 goingOn();
						return true;
					} else {
						mIsMoving = true;
						 goingOff();
					}
				} else {

					if (thumbIsOnLeftEdge()) {
						mHasMoved = false;
						return true;
					} else {
						mIsMoving = true;
						 goingOn();
					}
				}
			}

			break;

		default:
			break;
		}
		invalidate();
		return true;
	}

	public boolean isOn() {
		return this.mIsOn;
	}

	interface OnToggleChangedListener {
		public void onToggleChanged(boolean on, SwitchButton view);
	}

	public void setOnToggleChangedListener(
			OnToggleChangedListener onToggleChangedListener) {
		this.mOnToggleChangedListener = onToggleChangedListener;
	}

}
