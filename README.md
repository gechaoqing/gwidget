gwidget
=======

android widgets defined: switchButton/Android 自定义控件:switchButton

使用说明
========
### 1.设置监听
    
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
### 2.Layout文件中定义样式
		注:在根节点中添加[xmlns:gwidget="http://schemas.android.com/apk/res/com.gecq.gwidget"]
		gwidget:offColor     ------off状态背景色
		gwidget:onColor      ------on状态背景色
		gwidget:thumbOffColor------off状态小圆点颜色
		gwidget:thumbOnColor ------on状态小圆点颜色
		gwidget:thumbStyle   ------小圆点样式:match,padding
