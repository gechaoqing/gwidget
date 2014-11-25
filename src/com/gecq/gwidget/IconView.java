package com.gecq.gwidget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
/****
 * IconView
 * <br> Copyright (C) 2014 <b>Gechaoqing</b>
 * @author Gechaoqing
 * @since 2014-10-20
 * 
 */
public class IconView extends SvgPathView {

	private PathDataSet pathDataSet;
	public IconView(Context context) {
		super(context);
		pathDataSet= new PathDataSet();
	}

	public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		pathDataSet= new PathDataSet();
		draw();
	}

	public IconView(Context context, AttributeSet attrs) {
		super(context, attrs);
		pathDataSet= new PathDataSet();
		draw();
	}

	public void setIconPath(String path) {
		icon=path;
		draw();
	}

	public void setIconPath(int resid) {
		icon=getResources().getString(resid);
		draw();
	}
	
	public void setIconColor(int resid){
		ColorStateList colors=getResources().getColorStateList(resid);
		pathDataSet.mPaint.setColor(colors.getColorForState(getDrawableState(), Color.BLACK));
		invalidate();
	}
	
	public void setIconColor(String color){
		pathDataSet.mPaint.setColor(Color.parseColor(color));
		invalidate();
	}
	
	public void setScaleType(int scaleType){
		this.scaleType=scaleType;
		draw();
	}
	
	private void draw(){
		pathDataSet.computeDatas(icon);
		pathDataSet.mPaint.setColor(this.iconColor.getColorForState(getDrawableState(), Color.BLACK));
		requestLayout();
		invalidate();
	}

	@Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if(iconColor!=null&&iconColor.isStateful())
        {
        	pathDataSet.mPaint.setColor(this.iconColor.getColorForState(getDrawableState(), Color.BLACK));
        	invalidate();
        }
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.translate(getPaddingLeft()+dx, getPaddingTop()+dy);
		canvas.drawPath(pathDataSet.mPath, pathDataSet.mPaint);
		canvas.restore();
	}
	
	private float dx=0,dy=0;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
        	if(width<widthSize)
        	{
        		dx=(widthSize-width)/2;
        	}
            width = widthSize;
        }
        if(heightMode == MeasureSpec.EXACTLY){
        	if(height<heightSize)
        	{
        		dy=(heightSize-height)/2;
        	}
        	height=heightSize;
        }
        
        width+=getPaddingLeft()+getPaddingRight();
        height+=getPaddingTop()+getPaddingBottom();
		setMeasuredDimension((int)(width+0.5f), (int)height+1);
	}

}
