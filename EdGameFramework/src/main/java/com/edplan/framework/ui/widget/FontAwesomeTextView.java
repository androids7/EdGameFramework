package com.edplan.framework.ui.widget;
import com.edplan.framework.MContext;
import com.edplan.framework.ui.text.font.bmfont.BMFont;
import com.edplan.framework.ui.text.font.FontAwesome;

public class FontAwesomeTextView extends TextView
{
	public FontAwesomeTextView(MContext c){
		super(c);
		setFont(BMFont.getFont(BMFont.FontAwesome));
		setTextSize(getFont().getCommon().lineHeight*0.6f);
	}
	
	public void setIcon(FontAwesome fontAwesome){
		setText(fontAwesome.charvalue+"");
	}

	@Override
	public void onInitialLayouted(){
		// TODO: Implement this method
		super.onInitialLayouted();
		setTextSize(Math.min(getWidth(),getHeight()));
	}

	@Override
	protected void onLayout(boolean changed,float left,float top,float right,float bottom){
		// TODO: Implement this method
		super.onLayout(changed,left,top,right,bottom);
	}
}
