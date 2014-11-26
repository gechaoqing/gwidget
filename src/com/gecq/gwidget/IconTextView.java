package com.gecq.gwidget;

import com.gecq.gwidget.utils.SizeUtils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.util.AttributeSet;

/****
 * IconTextView <br>
 * Copyright (C) 2014 <b>Gechaoqing</b>
 * 
 * @author Gechaoqing
 * @since 2014-10-20
 * 
 */
public class IconTextView extends IconView {

	private float iconPadding;
	private PathDataSet iconLeft, iconRight, iconTop, iconBottom;
	private String left, right, top, bottom;
	private float iconPaddingLeft, iconPaddingTop, iconPaddingRight,
			iconPaddingBottom;
	private String text;
	private ColorStateList textColor;
	private FontMetrics mFontMetrics;
	private float textSize;
	private Paint textPaint;
	private float height, width;
	private float offsetY, offsetX;
	private float textHeight, textWidth;
	private float iconLeftWidth, iconTopHeight;
	private float maxWidth, maxHeight;
	private ColorStateList iconColor;

	public IconTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		create(context, attrs);
	}

	public void setText(String text) {
		this.text = text;
		getTextBounds();
		computeSize();
		invalidate();
		requestLayout();
	}

	public void setText(int resid) {
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
		iconPadding = ta.getDimension(R.styleable.iconTextView_iconPadding,
				SizeUtils.getInstance(context).getDip2Int(5));
		textColor = ta.getColorStateList(R.styleable.iconTextView_textColor);
		if (textColor == null) {
			textColor = ColorStateList.valueOf(Color.BLACK);
		}
		textSize = ta.getDimension(R.styleable.iconTextView_textSize, SizeUtils
				.getInstance(context).getSp2Int(14));
		ta.recycle();
		textPaint = new Paint();
		textPaint.setTypeface(Typeface.DEFAULT);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(textSize);
		textPaint.setColor(textColor.getColorForState(getDrawableState(),
				Color.BLACK));
		this.iconColor = getIconColor();
		getTextBounds();
		initIcons();
	}

	private void getTextBounds() {
		mFontMetrics = textPaint.getFontMetrics();
		textHeight = mFontMetrics.descent - mFontMetrics.ascent;
		textWidth = textPaint.measureText(text);
	}

	private void initDataSet(PathDataSet dataSet, String path, int scale) {
		dataSet.icon = path;
		dataSet.setIconColor(this.iconColor, Color.BLACK, getDrawableState());
		dataSet.computeDatas(SCALE_CENTER);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.translate(dx, dy);
		if (left != null) {
			canvas.save();
			canvas.translate(getPaddingLeft(), iconTopHeight
					+ (offsetY > 0 ? offsetY : 0) + getPaddingTop()
					+ iconPaddingTop);
			canvas.drawPath(iconLeft.mPath, iconLeft.mPaint);
			canvas.restore();
		}
		if (right != null) {
			canvas.save();
			canvas.translate(getPaddingLeft() + mxw() + iconPaddingLeft
					+ iconPaddingRight + iconLeftWidth, iconTopHeight
					+ (offsetY > 0 ? offsetY : 0) + getPaddingTop()
					+ iconPaddingTop);
			canvas.drawPath(iconRight.mPath, iconRight.mPaint);
			canvas.restore();
		}
		if (top != null) {
			canvas.save();
			canvas.translate(iconLeftWidth + getPaddingLeft()
					+ (offsetX > 0 ? offsetX : 0) + iconPaddingLeft,
					getPaddingTop());
			canvas.drawPath(iconTop.mPath, iconTop.mPaint);
			canvas.restore();
		}
		if (bottom != null) {
			canvas.save();
			canvas.translate(iconLeftWidth + getPaddingLeft()
					+ (offsetX > 0 ? offsetX : 0) + iconPaddingLeft,
					getPaddingTop() + iconPaddingTop + iconPaddingBottom
							+ iconTopHeight + mxh());
			canvas.drawPath(iconBottom.mPath, iconBottom.mPaint);
			canvas.restore();
		}
		canvas.drawText(text, iconLeftWidth + getPaddingLeft()
				+ (offsetX < 0 ? -offsetX : 0) + iconPaddingLeft, iconTopHeight
				+ getPaddingTop() + (offsetY < 0 ? -offsetY : 0)
				+ iconPaddingTop + (textHeight) / 2 + mFontMetrics.descent,
				textPaint);
	}

	private void initIcons() {
		if (left != null) {
			iconLeft = new PathDataSet();
			initDataSet(iconLeft, left, SCALE_WITH_WIDTH);
			iconPaddingLeft = iconPadding;
			maxHeight = iconLeft.mHeight;
		}
		if (right != null) {
			iconRight = new PathDataSet();
			initDataSet(iconRight, right, SCALE_WITH_WIDTH);
			iconPaddingRight = iconPadding;
			float rh = iconRight.mHeight;
			maxHeight = rh > maxHeight ? rh : maxHeight;
		}
		if (top != null) {
			iconTop = new PathDataSet();
			initDataSet(iconTop, top, SCALE_WITH_HEIGHT);
			iconPaddingTop = iconPadding;
			maxWidth = iconTop.mWidth;
		}
		if (bottom != null) {
			iconBottom = new PathDataSet();
			initDataSet(iconBottom, bottom, SCALE_WITH_HEIGHT);
			iconPaddingBottom = iconPadding;
			float bw = iconBottom.mWidth;
			maxWidth = bw > maxWidth ? bw : maxWidth;
		}
		computeSize();
	}

	private float mxh() {
		return (textHeight < maxHeight ? maxHeight : textHeight);
	}

	private float mxw() {
		return (textWidth < maxWidth ? maxWidth : textWidth);
	}

	private void computeSize() {
		height = mxh() + getPaddingTop() + getPaddingBottom() + iconPaddingTop
				+ iconPaddingBottom;
		width = mxw() + getPaddingLeft() + getPaddingRight() + iconPaddingLeft
				+ iconPaddingRight;
		if (maxHeight > 0)
			offsetY = (textHeight - maxHeight) / 2;
		if (maxWidth > 0)
			offsetX = (textWidth - maxWidth) / 2;
		if (left != null) {
			iconLeftWidth = iconLeft.mWidth;
			width += iconLeftWidth;
		}
		if (right != null) {
			width += iconRight.mWidth;
		}
		if (top != null) {
			iconTopHeight = iconTop.mHeight;
			height += iconTopHeight;
		}
		if (bottom != null) {
			height += iconBottom.mHeight;
		}

		// height = mxh()
		// + getPaddingTop() + getPaddingBottom() + iconPaddingTop
		// + iconPaddingBottom;
		// width = mxw()
		// + getPaddingLeft() + getPaddingRight() + iconPaddingLeft
		// + iconPaddingRight;
	}

	private float dx, dy;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthMode == MeasureSpec.EXACTLY) {
			if (width < widthSize) {
				dx = (widthSize - width) / 2;
			}
			width = widthSize;
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			if (height < heightSize) {
				dy = (heightSize - height) / 2;
			}
			height = heightSize;
		}
		setMeasuredDimension((int) (width + 0.5f), (int) (height + 0.5f));
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		if ((iconColor != null && iconColor.isStateful())
				|| (textColor != null && textColor.isStateful())) {
			if (iconColor != null && iconColor.isStateful()) {
				if (iconLeft != null)
					iconLeft.mPaint.setColor(this.iconColor.getColorForState(
							getDrawableState(), Color.BLACK));
				if (iconRight != null)
					iconRight.mPaint.setColor(this.iconColor.getColorForState(
							getDrawableState(), Color.BLACK));
				if (iconTop != null)
					iconTop.mPaint.setColor(this.iconColor.getColorForState(
							getDrawableState(), Color.BLACK));
				if (iconBottom != null)
					iconBottom.mPaint.setColor(this.iconColor.getColorForState(
							getDrawableState(), Color.BLACK));
			}
			if (textColor != null && textColor.isStateful()) {
				textPaint.setColor(this.textColor.getColorForState(
						getDrawableState(), Color.BLACK));
			}
			invalidate();
		}

	}

}
