package com.edplan.framework.graphics.opengl;
import android.opengl.GLSurfaceView;
import android.content.Context;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import android.view.MotionEvent;
import android.util.Log;
import com.edplan.framework.test.TestStaticData;
import android.view.View;
import com.edplan.framework.main.MainApplication;

public class BaseGLSurfaceView extends GLSurfaceView
{
	MainRenderer mRenderer;
	
	/*
	public BaseGLSurfaceView(Context con){
		super(con);
		this.setEGLContextClientVersion(2);
		this.setEGLConfigChooser(new MSAAConfig());
		mRenderer=new MainRenderer(getContext(),2);
		this.setRenderer(mRenderer);
		if(mRenderer instanceof OnTouchListener)this.setOnTouchListener((OnTouchListener)mRenderer);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	*/
	
	public BaseGLSurfaceView(Context con,MainRenderer r){
		super(con);
		this.setEGLContextClientVersion(2);
		this.setEGLConfigChooser(new MSAAConfig());
		mRenderer=r;
		mRenderer.setGlVersion(2);
		mRenderer.register(this);
		this.setRenderer(mRenderer);
		//if(mRenderer instanceof OnTouchListener)this.setOnTouchListener((OnTouchListener)mRenderer);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO: Implement this method
		//Log.v("thread","touch-thread: "+Thread.currentThread());
		TestStaticData.touchPosition.set(event.getX(),event.getY());
		mRenderer.onTouch(this,event);
		return true;
	}
	
	
	public class MSAAConfig implements EGLConfigChooser {
		@Override
		public EGLConfig chooseConfig(EGL10 egl,EGLDisplay display) {
			// TODO: Implement this method
			int attribs[] = {
				EGL10.EGL_LEVEL, 0,
				EGL10.EGL_RENDERABLE_TYPE, 4,  // EGL_OPENGL_ES2_BIT
				EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
				EGL10.EGL_RED_SIZE, 8,
				EGL10.EGL_GREEN_SIZE, 8,
				EGL10.EGL_BLUE_SIZE, 8,
				EGL10.EGL_DEPTH_SIZE, 16,
				EGL10.EGL_SAMPLE_BUFFERS, 1,
				EGL10.EGL_SAMPLES, 4,  // This is for 4x MSAA.
				EGL10.EGL_NONE
			};
			EGLConfig[] configs = new EGLConfig[1];
			int[] configCounts = new int[1];
			egl.eglChooseConfig(display, attribs, configs, 1, configCounts);

			if (configCounts[0] == 0) {
				// Failed! Error handling.
				throw new RuntimeException("err choose comfig");
				//return null;
			} else {
				return configs[0];
			}
		}
		
		
	}
}
