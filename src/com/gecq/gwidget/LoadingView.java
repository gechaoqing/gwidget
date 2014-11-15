package com.gecq.gwidget;

import com.gecq.gwidget.utils.SizeUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class LoadingView extends View {

	private int stepLength = 4;
	private int stepSize;
	private int stepPadding;
	private int shineCount = 1;
	private int currentShineIndex = 0;
	private long shineSpead = 15;
	private boolean canShine = true;
	private int stepColor = Color.BLACK;
	private RectModel[] mRects;
	private StepStyle stepStyle = StepStyle.ROUND;
	private int width,height;

	public enum StepStyle {
		RECT, ROUND
	}

	public LoadingView(Context context) {
		super(context);
		init();
	}

	public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		stepSize = SizeUtils.getInstance(getContext()).getSp2Int(12);
		stepPadding = SizeUtils.getInstance(getContext()).getDip2Int(5);
		mRects = new RectModel[stepLength];
		for (int i = 0; i < stepLength; i++) {
			RectModel rm = new RectModel(stepStyle);
			Rect rect = rm.rect;
			RectF rectF = rm.rectF;
			if (i == 0) {
				if (rect != null) {
					rect.set(0, 0, stepSize, stepSize);
				}
				if (rectF != null) {
					rectF.set(0, 0, stepSize, stepSize);
				}
			} else {
				Rect pre = mRects[i - 1].rect;
				if (pre != null) {
					int preX = pre.left + pre.width() + stepPadding;
					rect.set(preX, 0, stepSize + preX, stepSize);
				}
				RectF preF = mRects[i - 1].rectF;
				if (preF != null) {
					float preX = preF.left + preF.width() + stepPadding;
					rectF.set(preX, 0, stepSize + preX, stepSize);
				}
			}
			mRects[i] = rm;
		}
		height+=stepSize;
		width+=stepSize*stepLength+stepPadding*(stepLength-1);
		doShine();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (mRects != null) {
			if (stepStyle == StepStyle.RECT) {
				for (int i = 0; i < stepLength; i++) {
					RectModel rm = mRects[i];
					canvas.drawRect(rm.rect, rm.mPaint);
				}
			} else {
				for (int i = 0; i < stepLength; i++) {
					RectModel rm = mRects[i];
					canvas.drawRoundRect(rm.rectF, stepSize, stepSize,
							rm.mPaint);
				}
			}
		}
	}

	private void doShine() {
		postDelayed(new Runnable() {

			@Override
			public void run() {
				if (canShine) {
					updateShineProgress();
					postInvalidate();
					doShine();
				}
			}
		}, shineSpead < 15 ? 15 : shineSpead);
	}

	private void updateShineProgress() {
		for (int i = 0; i < shineCount; i++) {
			int over = i+currentShineIndex-stepLength;
			over = over>=0?0:(i + currentShineIndex);
			RectModel rm = mRects[over];
			rm.shineProgress += (rm.progressAdd ? 0.1f : -0.1f);
			if (rm.shineProgress <= 0) {
				rm.progressAdd = true;
				rm.shineProgress = 0;
			}
			if (rm.shineProgress >= 1) {
				rm.progressAdd = false;
				rm.shineProgress = 1;
				currentShineIndex++;
				if(currentShineIndex>=stepLength){
					currentShineIndex=0;
				}
			}
			rm.update();
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		width+=getPaddingLeft()+getPaddingRight();
		height+=getPaddingTop()+getPaddingBottom();
		setMeasuredDimension(width, height);
	}

	public void stopShine() {
		canShine = false;
	}
	
	public void startShine(){
		if(!canShine){
			canShine=true;
			doShine();
		}
	}

	private class RectModel {
		Rect rect;
		RectF rectF;
		Paint mPaint;
		float shineProgress = 1.0f;
		boolean progressAdd = false;

		public RectModel(StepStyle style) {
			if (style == StepStyle.RECT) {
				rect = new Rect();
			} else {
				rectF = new RectF();
			}
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			roundPaint(mPaint);
			mPaint.setAlpha((int) (255 * shineProgress));
			mPaint.setColor(stepColor);
		}
		
		private void update(){
			mPaint.setAlpha((int) (255 * shineProgress));
		}
		private void roundPaint(Paint mPaint) {
			mPaint.setAntiAlias(true);
			mPaint.setDither(true);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
		}
	}

}
