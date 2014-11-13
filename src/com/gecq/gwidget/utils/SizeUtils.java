package com.gecq.gwidget.utils;

import android.content.Context;
import android.util.TypedValue;

public class SizeUtils {
	private static SizeUtils instance;
	private Context ctx;
	public static final SizeUtils getInstance(Context ctx){
		if(instance==null){
			instance=new SizeUtils(ctx);
		}
		return instance;
	}
	private SizeUtils (Context ctx){
		this.ctx=ctx;
	}
	public int getDip2Int(int size){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, ctx.getResources().getDisplayMetrics());
	}
	/***
	 * 单位SP-PX转换
	 * @param size
	 * @return
	 */
	public int getSp2Int(int size){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, ctx.getResources().getDisplayMetrics());
	}
	/***
	 * 单位转换
	 * @param unit
	 * <li>{@link TypedValue#COMPLEX_UNIT_DIP TypedValue.COMPLEX_UNIT_DIP}等单位
	 * @param size
	 * @return
	 */
	public int getUnit2Int(int unit,int size){
		return (int) TypedValue.applyDimension(unit, size, ctx.getResources().getDisplayMetrics());
	}
}
