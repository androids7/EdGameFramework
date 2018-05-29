package com.edplan.framework.graphics.opengl.shader.advance;
import android.util.Log;
import com.edplan.framework.graphics.opengl.GLException;
import com.edplan.framework.graphics.opengl.shader.GLProgram;
import com.edplan.framework.graphics.opengl.shader.VertexAttrib;
import com.edplan.framework.graphics.opengl.shader.uniforms.UniformColor4;
import com.edplan.framework.graphics.opengl.shader.uniforms.UniformFloat;
import com.edplan.framework.graphics.opengl.shader.uniforms.UniformMat2;
import com.edplan.framework.graphics.opengl.shader.uniforms.UniformMat4;
import com.edplan.framework.graphics.opengl.shader.uniforms.UniformSample2D;
import java.lang.reflect.Field;
import com.edplan.framework.graphics.opengl.shader.uniforms.UniformVec2;

public abstract class BaseShader extends GLProgram
{
	protected BaseShader(GLProgram program,boolean initial){
		super(program.getVertexShader(),program.getFragmentShader(),program.getProgramId());
		if(initial)loadPointer();
	}
	
	protected final void loadPointer(){
		try{
			Class klass=this.getClass();
			Field[] fields=klass.getFields();
			int sampleIndex=0;
			for(Field f:fields){
				if(f.isAnnotationPresent(PointerName.class)){
					String name=f.getAnnotation(PointerName.class).value();
					if(name.length()==0){
						name=defaultFieldNameToPointerName(f.getName());
					}
					f.setAccessible(true);
					if(f.getType().equals(VertexAttrib.class)){
						AttribType type=f.getAnnotation(AttribType.class);
						if(f.isAnnotationPresent(AttribVAOData.class)){
							AttribVAOData vaodata=f.getAnnotation(AttribVAOData.class);
							f.set(this,VertexAttrib.findAttrib(this,name,type.value(),vaodata.step(),vaodata.offset()));
						}else{
							f.set(this,VertexAttrib.findAttrib(this,name,type.value()));
						}
					}else if(f.getType().equals(UniformMat2.class)){
						f.set(this,UniformMat2.findUniform(this,name));
					}else if(f.getType().equals(UniformMat4.class)){
						f.set(this,UniformMat4.findUniform(this,name));
					}else if(f.getType().equals(UniformColor4.class)){
						f.set(this,UniformColor4.findUniform(this,name));
					}else if(f.getType().equals(UniformSample2D.class)){
						f.set(this,UniformSample2D.findUniform(this,name,sampleIndex));
						sampleIndex++;
					}else if(f.getType().equals(UniformFloat.class)){
						f.set(this,UniformFloat.findUniform(this,name));
					}else if(f.getType().equals(UniformVec2.class)){
						f.set(this,UniformVec2.findUniform(this,name));
					}
				}
			}
		}catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new GLException("reflect err illaccs: "+e.getMessage(),e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new GLException("reflect err illagr: "+e.getMessage(),e);
		}
	}
	
	public static String defaultFieldNameToPointerName(String fn){
		if(fn.startsWith("u")||fn.startsWith("a")){
			return fn.substring(0,1)+"_"+fn.substring(1,fn.length());
		}else{
			throw new GLException("if you didn't set PointName, Field should start with \"a\" or \"u\"");
		}
	}
	
	public static @interface PointerName{
		public String value() default "";
	}
	
	public static @interface AttribType{
		public VertexAttrib.Type value();
	}
	
	public static @interface AttribVAOData{
		public int step();
		public int offset();
	}
}
