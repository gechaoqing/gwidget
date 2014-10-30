package com.gecq.gwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class IconCheckBox extends SvgPathView {

	private boolean checked=true, progressing;
	private CheckedChangedListener checkedChanged;

	private float mCheckedProgress = 0;
	private PathDataSet checkedPath;
	private Matrix scaleMatrix;

	public IconCheckBox(Context context) {
		super(context);
	}

	public IconCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		scaleMatrix = new Matrix();
		if (checked) {
			mCheckedProgress = 1.0f;
		}
		setUnchecked();
		setChecked();
	}

	public void setChecked(boolean isChecked) {
		this.checked = isChecked;
		requestLayout();
		postInvalidate();
	}

	private void setUnchecked() {
		pathDataSet.mPaint.setColor(this.iconColor.getColorForState(
				getDrawableState(), Color.BLACK));
		pathDataSet.mMatrix.setScale(pathDataSet.scale, pathDataSet.scale);
		pathDataSet.mMatrix.postTranslate(pathDataSet.dx, pathDataSet.dy);
		pathDataSet.mPath.transform(pathDataSet.mMatrix);
	}

	private void setChecked() {
		checkedPath = new PathDataSet();
		checkedPath.mPaint.setColor(checkedColor);
		mPareser.load(iconChecked, 0);
		checkedPath = computeDatas(checkedPath, iconChecked);
		checkedPath.mMatrix.setScale(checkedPath.scale, checkedPath.scale);
		checkedPath.mMatrix.postTranslate(checkedPath.dx, checkedPath.dy);
		checkedPath.mPath.transform(checkedPath.mMatrix);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		pathDataSet.mPaint.setAlpha((int) (255 * (1 - mCheckedProgress)));
		canvas.drawPath(pathDataSet.mPath, pathDataSet.mPaint);
		checkedPath.mPaint.setAlpha((int) (255 * mCheckedProgress));
		checkedPath.mMatrix.postScale(mCheckedProgress, mCheckedProgress, width / 2,
				height / 2);
//		checkedPath.mMatrix.postScale(mCheckedProgress, mCheckedProgress, width / 2,
//				height / 2);
//		checkedPath.mPath.transform(scaleMatrix);
		canvas.drawPath(checkedPath.mPath, checkedPath.mPaint);
	}

	private void updateOnProgress(float delt) {
		this.mCheckedProgress += delt;
		if (this.mCheckedProgress < 0) {
			this.mCheckedProgress = 0;
		}
		if (this.mCheckedProgress > 1) {
			this.mCheckedProgress = 1;
		}
		System.out.println(mCheckedProgress + " ==========");
		if (this.mCheckedProgress == 1 || this.mCheckedProgress == 0) {
			progressing = false;
			checked = (mCheckedProgress == 1);
			if (this.checkedChanged != null) {
				this.checkedChanged.checkedChanged(checked);
			}
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

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.progressing) {
			return true;
		}
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_CANCEL:
			if (progressing) {
				if (checked) {
					goUnchecked();
				} else {
					goChecked();
				}
			}

			break;
		case MotionEvent.ACTION_UP:
			progressing = true;
			if (checked) {
				goUnchecked();
			} else {
				goChecked();
			}
			break;
		}
		invalidate();
		return true;
	}

	public interface CheckedChangedListener {
		void checkedChanged(boolean isChecked);
	}

	@Override
	protected void onMeasure(int wms, int hms) {
		setMeasuredDimension((int) width + 1, (int) height + 1);
	}
}
