package com.gecq.gwidget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
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
	private PathDataSet checkedDataSet, unCheckedDataSet;
	private Matrix scaleMatrix;

	private boolean notifyChanged = true;
	private StringBuilder sb;

	public IconCheckBox(Context context) {
		super(context);
	}

	public IconCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = getResources().obtainAttributes(attrs,
				R.styleable.iconCheckBox);
		ColorStateList checkedColor = ta
				.getColorStateList(R.styleable.iconCheckBox_iconCheckedColor);
		if (checkedColor == null) {
			checkedColor = ColorStateList.valueOf(getDefaultCheckedColor());
		}
		String iconChecked = ta.getString(R.styleable.iconCheckBox_iconChecked);
		if (iconChecked == null) {
			iconChecked = getDefaultCheckedIcon();
		}

		ColorStateList unCheckedColor = ta
				.getColorStateList(R.styleable.iconCheckBox_iconUnCheckedColor);
		if (unCheckedColor == null) {
			unCheckedColor = ColorStateList.valueOf(getDefaultUncheckedColor());
		}
		String iconUnChecked = ta
				.getString(R.styleable.iconCheckBox_iconUnChecked);
		if (iconUnChecked == null) {
			iconUnChecked = getDefaultUncheckedIcon();
		}
		ta.recycle();
		init();
		checkedDataSet = new PathDataSet();
		checkedDataSet.icon = iconChecked;
		checkedDataSet.setIconColor(checkedColor, getDefaultCheckedColor(),
				getDrawableState());
		checkedDataSet.computeDatas();

		unCheckedDataSet = new PathDataSet();
		unCheckedDataSet.icon = iconUnChecked;
		unCheckedDataSet.setIconColor(unCheckedColor,
				getDefaultUncheckedColor(), getDrawableState());
		unCheckedDataSet.computeDatas();
		setOnClickListener(this);
	}

	private StringBuilder getSB() {
		if (sb == null) {
			sb = new StringBuilder();
		}
		return sb;
	}

	private String getDefaultCheckedIcon() {
		getSB().delete(0, getSB().length());
		getSB().append("M179.35,0C80.298,0,0,80.298,0,179.35")
				.append("S80.298,358.7,179.35,358.7S358.7,278.402,358.7,179.35S278.402,0,179.35,0z M174.516,267.978c-1.043,1.043-2.2,1.886-3.418,2.544")
				.append("c-3.92,2.442-8.632,2.595-11.991,0.295c-0.913-0.493-1.77-1.105-2.532-1.867l-19.511-19.512c-0.614-0.614-1.136-1.289-1.575-2.004")
				.append("l-80.73-80.73c-4.012-4.012-3.466-11.063,1.221-15.75l19.511-19.511c4.686-4.686,11.738-5.232,15.75-1.221l74.034,74.035")
				.append("L272.216,97.315c5.223-5.223,13.255-5.658,17.941-0.972l19.511,19.511c4.687,4.687,4.251,12.719-0.971,17.942L174.516,267.978z");
		return getSB().toString();
	}

	private String getDefaultUncheckedIcon() {
		getSB().delete(0, getSB().length());
		getSB().append("M179.35,0C80.298,0,0,80.298,0,179.35")
				.append("S80.298,358.7,179.35,358.7S358.7,278.402,358.7,179.35S278.402,0,179.35,0z M179.35,328.764")
				.append("c-82.519,0-149.414-66.895-149.414-149.414S96.831,29.937,179.35,29.937c82.519,0,149.414,66.895,149.414,149.414")
				.append("S261.868,328.764,179.35,328.764z");
		return getSB().toString();
	}

	public void setOnCheckedChangedListener(
			CheckedChangedListener checkedChanged) {
		this.checkedChanged = checkedChanged;
	}

	private void init() {
		scaleMatrix = new Matrix();
		scaleMatrix.setScale(1.0f, 1.0f);
		if (checked) {
			mCheckedProgress = 1.0f;
		}
	}

	public void setChecked(boolean isChecked) {
		setChecked(isChecked, true);
	}

	public void setChecked(boolean isChecked, boolean notifyChanged) {
		if (this.checked == isChecked)
			return;
		this.checked = isChecked;
		if (checked) {
			mCheckedProgress = 1.0f;
		} else {
			mCheckedProgress = 0.0f;
		}
		if (this.checkedChanged != null && notifyChanged) {
			this.checkedChanged.checkedChanged(this, isChecked);
		}
		requestLayout();
		postInvalidate();
	}

	public void setChecking(boolean isChecked) {
		setChecking(isChecked, true);
	}

	public void setChecking(boolean isChecked, boolean notifyChanged) {
		this.notifyChanged = notifyChanged;
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

	private int getDefaultCheckedColor() {
		return C("#20bf63");
	}

	private int getDefaultUncheckedColor() {
		return C("#858585");
	}

	@Override
	protected void onDraw(Canvas canvas) {
		unCheckedDataSet.mPaint.setAlpha((int) (255 * (1 - mCheckedProgress)));
		canvas.drawPath(unCheckedDataSet.mPath, unCheckedDataSet.mPaint);
		checkedDataSet.mPaint.setAlpha((int) (255 * mCheckedProgress));
		canvas.save();
		scaleMatrix.setScale(mCheckedProgress, mCheckedProgress, width / 2,
				height / 2);
		canvas.concat(scaleMatrix);
		canvas.drawPath(checkedDataSet.mPath, checkedDataSet.mPaint);
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
			if (this.checkedChanged != null && notifyChanged) {
				this.checkedChanged.checkedChanged(this, checked);
			}
		}

	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		ColorStateList checkedColor = checkedDataSet.getIconColor();
		ColorStateList uncheckedColor = unCheckedDataSet.getIconColor();
		if ((checkedColor != null && checkedColor.isStateful())
				|| (uncheckedColor != null && uncheckedColor.isStateful())) {
			if (checkedColor != null && checkedColor.isStateful()) {
				checkedDataSet.mPaint.setColor(checkedColor.getColorForState(
						getDrawableState(), getDefaultCheckedColor()));
			}
			if((uncheckedColor != null && uncheckedColor.isStateful())){
				unCheckedDataSet.mPaint.setColor(uncheckedColor.getColorForState(
						getDrawableState(), getDefaultUncheckedColor()));
			}
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
		void checkedChanged(IconCheckBox checkbox, boolean isChecked);
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
