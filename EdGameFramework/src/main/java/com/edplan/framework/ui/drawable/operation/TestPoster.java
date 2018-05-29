package com.edplan.framework.ui.drawable.operation;
import com.edplan.framework.graphics.opengl.objs.GLTexture;
import com.edplan.framework.graphics.opengl.GLCanvas2D;
import com.edplan.framework.math.RectF;
import com.edplan.framework.graphics.opengl.GLPaint;
import com.edplan.framework.graphics.opengl.BaseCanvas;

public class TestPoster implements ITexturePoster
{
	@Override
	public void drawTexture(BaseCanvas canvas,GLTexture t) {
		// TODO: Implement this method
		GLPaint paint=new GLPaint();
		canvas.drawTexture(t,new RectF(0,0,t.getWidth(),t.getHeight()),paint);
	}
}
