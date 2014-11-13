package com.gecq.gwidget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

/****
 * IconCheckBox <br>
 * Copyright (C) 2014 <b>Gechaoqing</b>
 * 
 * @author Gechaoqing
 * @since 2014-10-20
 * 
 */
public class IconCheckBox extends SvgPathView implements
		android.view.View.OnClickListener {

	private boolean checked, progressing;
	private CheckedChangedListener checkedChanged;

	private float mCheckedProgress = 0;
	private PathDataSet checkedPath, pathDataSet;
	private Matrix scaleMatrix;

	private ColorStateList checkedColor;
	private String iconChecked;

	public IconCheckBox(Context context) {
		super(context);
	}

	public IconCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = getResources().obtainAttributes(attrs,
				R.styleable.iconCheckBox);
		this.checkedColor = ta
				.getColorStateList(R.styleable.iconCheckBox_iconCheckedColor);
		if (this.checkedColor == null) {
			this.checkedColor = ColorStateList
					.valueOf(getDefaultCheckedColor());
		}
		this.iconChecked = ta.getString(R.styleable.iconCheckBox_iconChecked);
		ta.recycle();
		init();
		setOnClickListener(this);
	}

	private void init() {
		scaleMatrix = new Matrix();
		scaleMatrix.setScale(1.0f, 1.0f);
		if (checked) {
			mCheckedProgress = 1.0f;
		}
		setUnchecked();
		setChecked();
	}

	public void setChecked(boolean isChecked) {
		if (this.checked == isChecked)
			return;
		this.checked = isChecked;
		if (checked) {
			mCheckedProgress = 1.0f;
		} else {
			mCheckedProgress = 0.0f;
		}
		requestLayout();
		postInvalidate();
	}

	public void setChecking(boolean isChecked) {
		if (this.checked != isChecked) {
			progressing = true;
			if (isChecked) {
				goChecked();
			} else {
				goUnchecked();
			}
		}
	}

	public boolean isChecked() {
		return this.checked;
	}

	private void setUnchecked() {
		pathDataSet = new PathDataSet();
		pathDataSet.computeDatas(icon);
		pathDataSet.mPaint.setColor(this.iconColor.getColorForState(
				getDrawableState(), Color.BLACK));
	}

	private int getDefaultCheckedColor() {
		return Color.GREEN;
	}

	private void setChecked() {
		checkedPath = new PathDataSet();
		checkedPath.mPaint.setColor(checkedColor.getColorForState(
				getDrawableState(), getDefaultCheckedColor()));
		mPareser.load(iconChecked, 0);
		checkedPath.computeDatas(iconChecked);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		pathDataSet.mPaint.setAlpha((int) (255 * (1 - mCheckedProgress)));
		canvas.drawPath(pathDataSet.mPath, pathDataSet.mPaint);
		checkedPath.mPaint.setAlpha((int) (255 * mCheckedProgress));
		canvas.save();
		scaleMatrix.setScale(mCheckedProgress, mCheckedProgress, width / 2,
				height / 2);
		canvas.concat(scaleMatrix);
		canvas.drawPath(checkedPath.mPath, checkedPath.mPaint);
		canvas.restore();
	}

	private void updateOnProgress(float delt) {
		this.mCheckedProgress += delt;
		if (this.mCheckedProgress < 0) {
			this.mCheckedProgress = 0;
		}
		if (this.mCheckedProgress > 1) {
			this.mCheckedProgress = 1;
		}
		if (this.mCheckedProgress == 1 || this.mCheckedProgress == 0) {
			progressing = false;
			checked = (mCheckedProgress == 1);
			if (this.checkedChanged != null) {
				this.checkedChanged.checkedChanged(checked);
			}
		}

	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		if (checkedColor != null && checkedColor.isStateful()) {
			checkedPath.mPaint.setColor(this.checkedColor.getColorForState(
					getDrawableState(), getDefaultCheckedColor()));
			invalidate();
		}
	}

	private void goChecked() {
		postDelayed(new Runnable() {

			@Override
			public void run() {
				if (progressing) {
					updateOnProgress(0.1f);
					postInvalidate();
					goChecked();
				}
			}
		}, 15);
	}

	private void goUnchecked() {
		postDelayed(new Runnable() {

			@Override
			public void run() {
				if (progressing) {
					updateOnProgress(-0.1f);
					postInvalidate();
					goUnchecked();
				}
			}
		}, 15);
	}

	public interface CheckedChangedListener {
		void checkedChanged(boolean isChecked);
	}

	@Override
	protected void onMeasure(int wms, int hms) {
		setMeasuredDimension((int) width + 1, (int) height + 1);
	}

	@Override
	public void onClick(View v) {
		if (this.progressing) {
			return;
		}
		progressing = true;
		if (checked) {
			goUnchecked();
		} else {
			goChecked();
		}
		invalidate();
	}
}
