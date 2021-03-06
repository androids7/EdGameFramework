package com.edplan.framework.graphics.opengl;
import android.opengl.GLES20;
import android.util.Log;
import com.edplan.framework.graphics.opengl.objs.Color4;
import com.edplan.framework.utils.AbstractSRable;
import com.edplan.framework.utils.SRable;
import com.edplan.framework.utils.SRable.SROperation;
import com.edplan.framework.utils.advance.BooleanCopyable;
import com.edplan.framework.utils.advance.BooleanSetting;
import com.edplan.framework.interfaces.Setter;
import com.edplan.framework.graphics.opengl.objs.GLTexture;
import com.edplan.framework.interfaces.Copyable;
import com.edplan.framework.graphics.opengl.bufferObjects.FBOPool;
import android.opengl.GLES10;
import android.opengl.GLES30;
import android.opengl.GLES31Ext;
import android.opengl.GLES11Ext;
import java.nio.Buffer;

public class GLWrapped
{
	public static int GL_VERSION;
	
	public static int GL_SHORT=GLES20.GL_SHORT;
	
	public static int GL_UNSIGNED_SHORT=GLES20.GL_UNSIGNED_SHORT;
	
	public static int GL_TRIANGLES=GLES20.GL_TRIANGLES;
	
	public static int GL_MAX_TEXTURE_SIZE;
	
	public static final
	BooleanSetting depthTest=new BooleanSetting(new Setter<Boolean>(){
			@Override
			public void set(Boolean t) {
				// TODO: Implement this method
				if(t){
					if(GL_VERSION==2){
						GLES20.glEnable(GLES20.GL_DEPTH_TEST);
					}else{
						GLES10.glEnable(GLES10.GL_DEPTH_TEST);
					}
				}else{
					if(GL_VERSION==2){
						GLES20.glDisable(GLES20.GL_DEPTH_TEST);
					}else{
						GLES10.glDisable(GLES10.GL_DEPTH_TEST);
					}
				}
			}
		},
		false).initial();
	
	public static BlendSetting blend=new BlendSetting().setUp();
	
	public static void initial(int version){
		GL_VERSION=version;
		GL_MAX_TEXTURE_SIZE=getIntegerValue(GLES20.GL_MAX_TEXTURE_SIZE);
		GLTexture.initial();
		FBOPool.initialGL();
		//GLES20.glEnable(GLES20.gl_stencil_t
		//GLES20.glst
	}
	
	public static void onFrame(){
		drawCalls=0;
	}
	
	private static int drawCalls=0;
	public static void drawArrays(int mode,int offset,int count){
		GLES20.glDrawArrays(mode,offset,count);
		drawCalls++;
	}
	
	public static void drawElements(int mode,int count,int type,Buffer b){
		GLES20.glDrawElements(mode,count,type,b);
		drawCalls++;
	}
	
	public static int frameDrawCalls(){
		return drawCalls;
	}
	
	private static int px1,px2,py1,py2;
	public static void setViewport(int x1,int y1,int x2,int y2){
		//if(!(px1==x1&&px2==x2&&py1==y1&&py2==y2)){
			if(GL_VERSION==2){
				GLES20.glViewport(x1,y1,x2-x1,y2-y1);
			}else{
				GLES10.glViewport(x1,y1,x2-x1,y2-y1);
			}
			px1=x1;
			px2=x2;
			py1=y1;
			py2=y2;
		//}
	}
	
	public static void setClearColor(float r,float g,float b,float a){
		if(GL_VERSION==2){
			GLES20.glClearColor(r,g,b,a);
		}else{
			GLES10.glClearColor(r,g,b,a);
		}
	}
	
	public static void clearColorBuffer(){
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	}
	
	public static void clearDepthBuffer(){
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
	}
	
	public static void clearDepthAndColorBuffer(){
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
	}
	
	/*
	public static void setDepthTest(boolean f){
		if(f!=depthTest){
			if(f){
				GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			}else{
				GLES20.glDisable(GLES20.GL_DEPTH_TEST);
			}
			depthTest=f;
		}
	}*/
	
	public static void setClearColor(Color4 c){
		setClearColor(c.r,c.g,c.b,c.a);
	}
	
	public static int getIntegerValue(int key){
		if(GL_VERSION>=2){
			int[] b=new int[1];
			GLES20.glGetIntegerv(key,b,0);
			return b[0];
		}else{
			int[] b=new int[1];
			GLES10.glGetIntegerv(key,b,0);
			return b[0];
		}
	}
		
	public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("ES20_ERROR", op + ": glError " + error);
            throw new GLException(op + ": glError " + error);
        }
	}
}
