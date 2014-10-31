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

	public IconView(Context context) {
		super(context);
	}

	public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		draw();
	}

	public IconView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
	
	private void draw(){
		mPareser.load(icon, 0);
		pathDataSet= computeDatas(pathDataSet, icon);
		pathDataSet.mPaint.setColor(this.iconColor.getColorForState(getDrawableState(), Color.BLACK));
		pathDataSet.mMatrix.setScale(pathDataSet.scale, pathDataSet.scale);
		pathDataSet.mMatrix.postTranslate(pathDataSet.dx, pathDataSet.dy);
		pathDataSet.mPath.transform(pathDataSet.mMatrix);
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
		canvas.drawPath(pathDataSet.mPath, pathDataSet.mPaint);
	}
	

	@Override
	protected void onMeasure(int wms, int hms) {
		setMeasuredDimension((int)width+1, (int)height+1);
	}

}
