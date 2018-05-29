package com.edplan.framework.main;
import android.app.Activity;
import android.content.Context;
import com.edplan.framework.graphics.opengl.BaseGLSurfaceView;
import com.edplan.framework.graphics.opengl.MainRenderer;
import com.edplan.framework.ui.text.font.bmfont.BMFont;
import com.edplan.framework.ui.text.font.drawing.TextPrinter;
import com.edplan.framework.MContext;
import com.edplan.framework.resource.AResource;
import java.io.IOException;
import com.edplan.framework.test.TestStaticData;
import com.edplan.framework.ui.ViewConfiguration;
import com.edplan.framework.ui.text.font.FontAwesome;

public abstract class MainApplication implements MainCallBack
{
	protected Context context;
	
	protected MContext mContext;
	
	protected BaseGLSurfaceView surface;
	
	protected MainRenderer renderer;
	
	public abstract MainRenderer createRenderer(MContext context);
	
	public Context getNativeContext(){
		return context;
	}
	
	public BaseGLSurfaceView createGLView(){
		return new BaseGLSurfaceView(getNativeContext(),renderer=createRenderer(mContext));
	}
	
	@Override
	public void onPause(){
		if(renderer!=null)renderer.getViewRoot().onPause();
	}
	
	@Override
	public void onResume(){
		if(renderer!=null)renderer.getViewRoot().onPause();
	}
	
	@Override
	public void onLowMemory(){
		if(renderer!=null)renderer.getViewRoot().onPause();
	}
	
	@Override
	public boolean onBackPressed(){
		if(renderer!=null){
			if(renderer.getViewRoot().onBackPressed()){
				return true;
			}
		}
		if(!onBackPressNotHandled()){
			onExit();
		}
		return true;
	}
	
	public boolean onBackPressNotHandled(){
		return false;
	}
	
	public void onExit(){
		System.exit(0);
	}
	
	public void setUpActivity(EdMainActivity act){
		this.context=act;
		act.register(this);
		mContext=new MContext(context);
		TestStaticData.context=mContext;
		surface=createGLView();
		act.setContentView(surface);
		onApplicationCreate();
	}
	
	/**
	 *应用被初始化时被调用，GL环境可能尚未搭建
	 */
	public void onApplicationCreate(){
		ViewConfiguration.loadContext(mContext);
	}
	
	/**
	 *GL环境创建好了之后被调用
	 */
	public void onGLCreate(){
		AResource res=mContext.getAssetResource().subResource("font");
		try{
			{
				BMFont font=BMFont.loadFont(
					res,
					"Noto-CJK-Basic.fnt");
				font.addFont(res,"Noto-Basic.fnt");
				font.setErrCharacter('⊙');
				BMFont.addFont(font,font.getInfo().face);
				BMFont.setDefaultFont(font);
			}
			{
				BMFont font=BMFont.loadFont(
					res,
					"Exo2.0-Regular.fnt");
				font.setErrCharacter(BMFont.CHAR_NOT_FOUND);
				BMFont.addFont(font,font.getInfo().face);
			}
			{
				BMFont font=BMFont.loadFont(
					res,
					"Exo2.0-SemiBold.fnt");
				font.setErrCharacter(BMFont.CHAR_NOT_FOUND);
				BMFont.addFont(font,font.getInfo().face);
			}
			{
				BMFont font=BMFont.loadFont(
					res,
					"FontAwesome.fnt");
				font.setErrCharacter(FontAwesome.fa_ban.charvalue);
				BMFont.addFont(font,font.getInfo().face);
			}
			
		}catch(IOException e){
			e.printStackTrace();
			mContext.toast("读取字体失败 msg="+e.getMessage());
		}
	}
}
