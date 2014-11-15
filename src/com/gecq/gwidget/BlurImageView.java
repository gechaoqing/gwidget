package com.gecq.gwidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BlurImageView extends ImageView {

	private float radius=8;
	private Paint mPaint;
	
	public BlurImageView(Context context) {
		super(context);
		init();
	}

	public BlurImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BlurImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		mPaint = new Paint();
		BlurMaskFilter blur= new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
		mPaint.setMaskFilter(blur);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(getDrawable()!=null){
			Bitmap bit=((BitmapDrawable)getDrawable()).getBitmap();
			canvas.drawBitmap(bit, getPaddingLeft(), getPaddingTop(), mPaint);
		}
	}
	

}
