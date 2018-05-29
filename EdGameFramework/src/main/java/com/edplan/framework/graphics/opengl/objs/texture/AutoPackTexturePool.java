package com.edplan.framework.graphics.opengl.objs.texture;
import com.edplan.framework.graphics.opengl.objs.GLTexture;
import com.edplan.framework.graphics.opengl.objs.AbstractTexture;
import com.edplan.framework.graphics.opengl.GLCanvas2D;
import com.edplan.framework.MContext;
import com.edplan.framework.graphics.opengl.GLPaint;
import com.edplan.framework.math.RectF;
import com.edplan.framework.math.RectI;
import java.util.List;
import java.util.ArrayList;
import com.edplan.framework.graphics.opengl.objs.Color4;
import java.io.File;
import android.graphics.Bitmap;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import com.edplan.framework.graphics.layer.BufferedLayer;
import android.util.ArrayMap;
import java.util.Map;
import com.edplan.framework.graphics.opengl.objs.texture.TexturePool.MsgTexture;
import java.util.Collections;
import java.util.Comparator;
import java.io.IOException;
import com.edplan.framework.graphics.opengl.GLWrapped;
import com.edplan.framework.graphics.opengl.ShaderManager;
import com.edplan.framework.interfaces.Function;

public class AutoPackTexturePool extends TexturePool
{
	private int packWidth=2000;
	private int packHeight=2000;
	private int maxWidth=900;
	private int maxHeight=900;
	
	private List<PackNode> packs=new ArrayList<PackNode>();
	private List<AbstractTexture> packedTextures=new ArrayList<AbstractTexture>();
	public List<RectF> packedPosition=new ArrayList<RectF>();
	private PackNode currentPack;
	private GLCanvas2D packCanvas;
	private GLPaint rawPaint=new GLPaint();
	private int currentX;
	private int currentY;
	private int lineMaxY;
	
	//public GLTexture textureTest;
	
	private int marginX=2,marginY=2;
	
	private MContext context;
	
	public AutoPackTexturePool(TextureLoader loader,MContext context){
		super(loader);
		this.context=context;
		packWidth=Math.min(GLWrapped.GL_MAX_TEXTURE_SIZE,2000);
		packHeight=packWidth;
		maxWidth=packWidth*3/4;
		maxHeight=packHeight/2;
		toNewPack();
	}

	@Override
	public void directPut(String msg,AbstractTexture t) {
		// TODO: Implement this method
		super.directPut(msg, t);
	}

	@Override
	public void addAll(List<TexturePool.MsgTexture> list) {
		// TODO: Implement this method
		Collections.sort(list, new Comparator<MsgTexture>(){
				@Override
				public int compare(TexturePool.MsgTexture p1,TexturePool.MsgTexture p2) {
					// TODO: Implement this method
					if(p1.texture.getHeight()==p2.texture.getHeight()){
						return p1.texture.getWidth()-p2.texture.getWidth();
					}else{
						return p1.texture.getHeight()-p2.texture.getHeight();
					}
				}
			});
		for(MsgTexture t:list){
			t.texture=testAddRaw(t.texture,t.msg);
			directPut(t.msg,t.texture);
		}
	}
	
	public int packedCount(){
		return packedTextures.size();
	}

	public void setPackWidth(int packWidth) {
		this.packWidth=packWidth;
	}

	public int getPackWidth() {
		return packWidth;
	}

	public void setPackHeight(int packHeight) {
		this.packHeight=packHeight;
	}

	public int getPackHeight() {
		return packHeight;
	}

	public List<AbstractTexture> getPackedTextures() {
		return packedTextures;
	}

	public List<PackNode> getPacks() {
		return packs;
	}
	
	public PackNode getPackByIndex(int idx){
		if(idx<0)return null;
		int c=0;
		for(PackNode n:packs){
			c+=n.inPacks.size();
			if(c>idx)return n;
		}
		return getPackByIndex(idx%c);
	}
	
	public void writeToDir(File dir,String s,Function<OutputNode> out){
		int i=-1;
		for(PackNode t:packs){
			i++;
			try {
				File f=new File(dir,s+i+".png");
				f.createNewFile();
				t.layer.getTexture().getTexture().toBitmap(context).compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(f));
				if(out!=null)out.invoke(new OutputNode(f,t.layer.getTexture().getTexture()));
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("err compress "+i);
			}
		}
	}
	
	public void toNewPack(){
		currentPack=new PackNode();
		currentPack.layer=new BufferedLayer(context,packWidth,packHeight,true);
		//GLTexture.createGPUTexture(packWidth,packHeight);
		packs.add(currentPack);
		packCanvas=new GLCanvas2D(currentPack.layer);
		packCanvas.prepare();
		packCanvas.drawColor(Color4.Alpha);
		packCanvas.clearBuffer();
		packCanvas.unprepare();
		currentX=0;
		currentY=0;
		lineMaxY=0;
	}
	
	private void toNextLine(){
		currentX=0;
		currentY=lineMaxY;
	}
	
	private AbstractTexture tryAddInLine(AbstractTexture raw,String msg){
		if(currentY+raw.getHeight()+marginY<currentPack.layer.getHeight()){
			packCanvas.prepare();
			packCanvas.save();
			//packCanvas.getData().getShaders().setTexture3DShader(ShaderManager.getRawTextureShader());
			packCanvas.drawTexture(raw,RectF.xywh(currentX,currentY,raw.getWidth(),raw.getHeight()),rawPaint);
			packCanvas.restore();
			packCanvas.unprepare();
			RectF area=RectF.xywh(currentX,currentY,raw.getWidth(),raw.getHeight());
			AbstractTexture t=new TextureRegion(
											currentPack.layer.getTexture().getTexture(),
											//textureTest,
											area);
			currentPack.inPacks.put(msg,t);
			packedPosition.add(area);
			packedTextures.add(t);
			currentX+=raw.getWidth()+marginX;
			lineMaxY=Math.max(lineMaxY,currentY+raw.getHeight()+marginY);
			return t;
		}else{
			toNewPack();
			return tryAddToPack(raw,msg);
		}
	}
	
	private AbstractTexture tryAddToPack(AbstractTexture raw,String msg){
		if(currentX+raw.getWidth()+marginX<currentPack.layer.getWidth()){
			return tryAddInLine(raw,msg);
		}else{
			toNextLine();
			return tryAddToPack(raw,msg);
		}
	}
	
	private int getMaxWidth(){
		return Math.min(packWidth,maxWidth);
	}
	
	private int getMaxHeight(){
		return Math.min(packHeight,maxHeight);
	}

	private AbstractTexture testAddRaw(AbstractTexture raw,String msg){
		if(raw.getWidth()>=getMaxWidth()||raw.getHeight()>=getMaxHeight()){
			return raw;
		}else{
			return tryAddToPack(raw,msg);
		}
	}
	
	@Override
	protected AbstractTexture loadTexture(String msg) {
		// TODO: Implement this method
		AbstractTexture raw=super.loadTexture(msg);
		return testAddRaw(raw,msg);
	}
	
	public class PackNode{
		public ArrayMap<String,AbstractTexture> inPacks=new ArrayMap<String,AbstractTexture>();
		public BufferedLayer layer;
		public PackNode(){
			
		}
	}
	
	public class OutputNode{
		public File file;
		public GLTexture tex;
		public OutputNode(File f,GLTexture t){
			file=f;
			tex=t;
		}
	}
}
