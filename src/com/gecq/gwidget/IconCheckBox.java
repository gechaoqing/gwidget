package com.gecq.gwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

public class IconCheckBox extends SvgPathView implements android.view.View.OnClickListener {

	private boolean checked, progressing;
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
