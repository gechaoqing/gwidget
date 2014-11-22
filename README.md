gwidget:
=======
	注:在使用gwidget样式前，需在根节点中添加
	xmlns:gwidget="http://schemas.android.com/apk/res/com.gecq.gwidget"
1.SwitchButton
============
SwitchButton:使用说明
--------
### 1.1.设置监听
    
		SwitchButton sb=(SwitchButton) findViewById(R.id.equalizer);
		sb.setOnToggleChangedListener(new SwitchButton.OnToggleChangedListener() {
			
			@Override
			public void onToggleChanged(boolean on, SwitchButton view) {
				if(on){
					// do if toggle on
				}else{
					// do if toggle off
				}
			}
		});
### 1.2.Layout文件中定义样式
		
		gwidget:offColor     ------off状态背景色
		gwidget:onColor      ------on状态背景色
		gwidget:thumbOffColor------off状态小圆点颜色
		gwidget:thumbOnColor ------on状态小圆点颜色
		gwidget:thumbStyle   ------小圆点样式:match,padding
### 1.3.图例
![github](https://github.com/gechaoqing/gwidget/blob/master/switchbutton.gif) ------thumbStyle padding <br>
![github](https://github.com/gechaoqing/gwidget/blob/master/switchbutton1.gif) ------thumbStyle match
2.RadiusImage
===========
RadiusImage:使用说明
----------
### 2.1.Layout文件中定义样式
	gwidget:borderColor      ------边框颜色
    gwidget:borderStyle      ------边框样式 over,outside(默认)
    gwidget:borderWidth      ------边框宽度
    gwidget:radius           ------图片圆角半径
	gwidget:radiusTopLeft    ------图片左上角圆角半径
	gwidget:radiusTopRight   ------图片右上角圆角半径
	gwidget:radiusBottomLeft ------图片左下角圆角半径
	gwidget:radiusBottomRight------图片右下角圆角半径
	gwidget:alpha            ------图片透明度，取值0-1(0:全透明，1:不透明)
	gwidget:radiusScaleType  ------图片取图位置:
							centerCrop(默认)
							topLeft,topCenter,topRight
							bottomLeft,bottomCenter,bottomRight
							leftCenter,rightCenter
### 2.2.图例
![github](https://github.com/gechaoqing/gwidget/blob/master/radiusImage1.jpg) <br>
![github](https://github.com/gechaoqing/gwidget/blob/master/radiusImage2.jpg) 
3.IconView
===========
IconView:使用说明
----------
	继承SvgPathView->View，实现通过SVG Path字符串绘制图形/图标
### 3.1.Layout文件中定义样式
	gwidget:icon      ------SVG Path 字符串
	gwidget:iconColor ------图形/图标颜色
	gwidget:iconSize  ------图形/图标大小
### 3.2.图例
![github](https://github.com/gechaoqing/gwidget/blob/master/icon.jpg) 
4.IconTextView
==========
IconTextView:使用说明
---------
	继承SvgPathView->View，实现通过SVG Path字符串绘制图形/图标和相应位置的文字
### 4.1.Layout文件中定义样式
	gwidget:iconLeft    ------文字左侧图标
	gwidget:iconRight   ------文字右侧图标
	gwidget:iconTop     ------文字上侧图标
	gwidget:iconBottom  ------文字下侧图标
	gwidget:iconPadding ------文字与图标的距离
	gwidget:iconColor   ------图标颜色
	gwidget:iconSize    ------图标大小
	gwidget:text        ------文字内容
	gwidget:textSize    ------文字大小
	gwidget:textColor   ------文字颜色 
### 4.2.图例
 ![github](https://github.com/gechaoqing/gwidget/blob/master/iconTextView.jpg) 