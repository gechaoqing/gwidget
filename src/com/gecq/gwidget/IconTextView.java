package com.gecq.gwidget;


import com.gecq.gwidget.utils.SizeUtils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
/****
 * IconTextView
 * <br> Copyright (C) 2014 <b>Gechaoqing</b>
 * @author Gechaoqing
 * @since 2014-10-20
 * 
 */
public class IconTextView extends SvgPathView {

	private float iconPadding;
	private PathDataSet iconLeft, iconRight, iconTop, iconBottom;
	private String left, right, top, bottom;
	private float iconPaddingLeft,iconPaddingTop,iconPaddingRight,iconPaddingBottom;
	private String text;
	private ColorStateList textColor;
	private Rect textBounds;
	private float textSize;
	private Paint textPaint;
	private float height, width;
	private float offsetY, offsetX;
	private float textHeight, textWidth;
	private float iconLeftWidth,iconTopHeight;
	

	public IconTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		create(context, attrs);
	}
	
	public void setText(String text){
		this.text=text;
		getTextBounds();
		computeSize();
		invalidate();
		requestLayout();
	}
	
	public void setText(int resid){
		setText(getResources().getString(resid));
	}

	private void create(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.iconTextView);
		left = ta.getString(R.styleable.iconTextView_iconLeft);
		right = ta.getString(R.styleable.iconTextView_iconRight);
		top = ta.getString(R.styleable.iconTextView_iconTop);
		bottom = ta.getString(R.styleable.iconTextView_iconBottom);
		text = ta.getString(R.styleable.iconTextView_text);
		iconPadding = ta.getDimension(
				R.styleable.iconTextView_iconPadding, SizeUtils.getInstance(context).getDip2Int(5));
		textColor = ta.getColorStateList(R.styleable.iconTextView_textColor);
		if(textColor==null){
			textColor=ColorStateList.valueOf(Color.BLACK);
		}
		textSize =ta.getDimension(R.styleable.iconTextView_textSize, SizeUtils.getInstance(context).getSp2Int(14));
		ta.recycle();
		textPaint = new Paint();
		textPaint.setTypeface(Typeface.DEFAULT);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(textSize);
		textPaint.setColor(textColor.getColorForState(
				getDrawableState(), Color.BLACK));
		textBounds= new Rect();
		getTextBounds();
		initIcons();
		computeSize();
	}
	
	private void getTextBounds(){
		textPaint.getTextBounds(text, 0, text.length(), textBounds);
		textHeight = textBounds.height();
		textWidth = textPaint.measureText(text);
	}

	private void initDataSet(PathDataSet dataSet, String path) {
		dataSet.mPaint.setColor(this.iconColor.getColorForState(
				getDrawableState(), Color.BLACK));
		dataSet.computeDatas(path, SCALE_CENTER);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (left != null) {
			canvas.save();
			canvas.translate(getPaddingLeft(),iconTopHeight
					+ (offsetY > 0 ? offsetY : 0)+ getPaddingTop()+iconPaddingTop);
			canvas.drawPath(iconLeft.mPath, iconLeft.mPaint);
			canvas.restore();
		}
		if (right != null) {
			canvas.save();
			canvas.translate(getPaddingLeft()+textWidth+iconPaddingLeft+iconPaddingRight+iconLeftWidth, iconTopHeight
					+ (offsetY > 0 ? offsetY : 0)+ getPaddingTop()+iconPaddingTop);
			canvas.drawPath(iconRight.mPath, iconRight.mPaint);
			canvas.restore();
		}
		if(top!=null){
			canvas.save();
			canvas.translate(iconLeftWidth+getPaddingLeft()+ (offsetX > 0 ? offsetX : 0)+iconPaddingLeft, getPaddingTop());
			canvas.drawPath(iconTop.mPath, iconTop.mPaint);
			canvas.restore();
		}
		if(bottom!=null){
			canvas.save();
			canvas.translate(iconLeftWidth+getPaddingLeft()+ (offsetX > 0 ? offsetX : 0)+iconPaddingLeft, getPaddingTop()+iconPaddingTop+iconPaddingBottom+iconTopHeight+textHeight);
			canvas.drawPath(iconBottom.mPath, iconBottom.mPaint);
			canvas.restore();
		}
		canvas.drawText(text, iconLeftWidth+getPaddingLeft()+ (offsetX < 0 ? offsetX : 0)+iconPaddingLeft, iconTopHeight+ getPaddingTop()+(offsetY < 0 ? offsetY : 0)+iconPaddingTop+(textHeight-textPaint.descent() - textPaint.ascent()) / 2, textPaint);
	}
	
	private void initIcons(){
		if (left != null) {
			iconLeft = new PathDataSet();
			initDataSet(iconLeft, left);
			iconPaddingLeft=iconPadding;
		}
		if (right != null) {
			iconRight = new PathDataSet();
			initDataSet(iconRight, right);
			iconPaddingRight=iconPadding;
		}
		if (top != null) {
			iconTop = new PathDataSet();
			initDataSet(iconTop, top);
			iconPaddingTop=iconPadding;
		}
		if (bottom != null) {
			iconBottom = new PathDataSet();
			initDataSet(iconBottom, bottom);
			iconPaddingBottom=iconPadding;
		}
	}

	private void computeSize() {
		height = textHeight+ getPaddingTop()+ getPaddingBottom()+iconPaddingTop+iconPaddingBottom;
		width = textWidth+getPaddingLeft()+getPaddingRight()+iconPaddingLeft+iconPaddingRight;
		if (left != null) {
			iconLeftWidth = iconLeft.mRectTransformed.width();
			offsetY = (textHeight - iconLeft.mRectTransformed.height())/2;
			width += iconLeftWidth;
		}
		if (right != null) {
			width += iconRight.mRectTransformed.width() ;
			offsetY = (textHeight - iconRight.mRectTransformed.height())/2;
		}
		if (top != null) {
			iconTopHeight = iconTop.mRectTransformed.height() ;
			offsetX = (textWidth - iconTop.mRectTransformed.width())/2;
			height += iconTopHeight;
		}
		if (bottom != null) {
			height += iconBottom.mRectTransformed.height() ;
			offsetX = (textWidth - iconBottom.mRectTransformed.width())/2;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension((int)width, (int) height);
	}
	
	@Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if(iconColor!=null&&iconColor.isStateful())
        {
        	if(iconLeft!=null)
        		iconLeft.mPaint.setColor(this.iconColor.getColorForState(getDrawableState(), Color.BLACK));
        	if(iconRight!=null)
        		iconRight.mPaint.setColor(this.iconColor.getColorForState(getDrawableState(), Color.BLACK));
        	if(iconTop!=null)
        		iconTop.mPaint.setColor(this.iconColor.getColorForState(getDrawableState(), Color.BLACK));
        	if(iconBottom!=null)
        		iconBottom.mPaint.setColor(this.iconColor.getColorForState(getDrawableState(), Color.BLACK));
        	invalidate();
        }
        if(textColor!=null&&textColor.isStateful()){
        	textPaint.setColor(this.textColor.getColorForState(getDrawableState(), Color.BLACK));
        	invalidate();
        }
	}

}
