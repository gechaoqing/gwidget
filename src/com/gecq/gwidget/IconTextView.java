package com.gecq.gwidget;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IconTextView extends RelativeLayout {

	private TextView mTextView;
	private IconView mSvgPathView;
	private int iconDir=DIR_TOP;//left 0 top 1 right 2 bottom 3
	private float iconPadding=5.0f;
	private LayoutParams mLayout,mLayout1;
	
	private static final int DIR_LEFT=0,DIR_TOP=1,DIR_RIGHTT=2,DIR_BOTTOM=3;

	public IconTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		create(context,attrs);
	}
	
	private void create(Context context, AttributeSet attrs){
		TypedArray ta=context.obtainStyledAttributes(R.styleable.iconView);
//		this.iconDir=ta.getInt(R.styleable.iconView_iconDir, DIR_LEFT);
//		this.iconPadding=ta.getDimension(R.styleable.iconView_iconPadding, 5.0f);
		ta.recycle();
		mTextView=new TextView(context,attrs);
		mTextView.setPadding(0, 0, 0, 0);
		mTextView.setBackgroundResource(android.R.color.transparent);
		mSvgPathView=new IconView(context,attrs);
		mSvgPathView.setBackgroundResource(android.R.color.transparent);
		mSvgPathView.setId(R.id.icon_in_icontextview);
		mSvgPathView.setPadding(0, 0, 0, 0);
		mLayout=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLayout1=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addView(mTextView);
		addView(mSvgPathView);
		switch (iconDir) {
		case DIR_LEFT:
			mLayout.addRule(RIGHT_OF,R.id.icon_in_icontextview);
			mLayout.addRule(CENTER_VERTICAL);
			mLayout1.addRule(CENTER_VERTICAL);
			mLayout.setMargins((int)iconPadding, 0, 0, 0);
			break;
		case DIR_RIGHTT:
			mLayout.addRule(LEFT_OF, R.id.icon_in_icontextview);
			mLayout.addRule(CENTER_VERTICAL);
			mLayout1.addRule(CENTER_VERTICAL);
			mLayout.setMargins(0, 0, (int)iconPadding,0);
			break;
		case DIR_BOTTOM:
			mLayout.addRule(ABOVE, R.id.icon_in_icontextview);
			mLayout.addRule(CENTER_HORIZONTAL);
			mLayout1.addRule(CENTER_HORIZONTAL);
			mLayout.setMargins(0, 0, 0, (int)iconPadding);
			break;
		case DIR_TOP:
			mLayout.addRule(BELOW, R.id.icon_in_icontextview);
			mLayout.addRule(CENTER_HORIZONTAL);
			mLayout1.addRule(CENTER_HORIZONTAL);
			mLayout.setMargins(0, (int)iconPadding, 0, 0);
			break;
		}
		mTextView.setLayoutParams(mLayout);
		mSvgPathView.setLayoutParams(mLayout1);
	}

}
