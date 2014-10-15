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
