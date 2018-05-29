package com.edplan.framework.resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class MultipleResource extends AResource
{
	private List<AResource> ress=new ArrayList<AResource>();
	
	public MultipleResource(){
		
	}
	
	public MultipleResource(AResource... rs){
		for(AResource r:rs){
			add(r);
		}
	}
	
	public void add(AResource r){
		ress.add(r);
	}
	
	@Override
	public String[] list(String dir) throws IOException {
		// TODO: Implement this method
		List<String> l=new ArrayList<String>();
		for(AResource r:ress){
			final String[] s=r.list(dir);
			if(s!=null)l.addAll(Arrays.asList(s));
		}
		String[] rl=new String[l.size()];
		l.toArray(rl);
		return rl;
	}

	@Override
	public boolean contain(String file) {
		// TODO: Implement this method
		for(AResource r:ress){
			if(r.contain(file))return true;
		}
		return false;
	}

	@Override
	public InputStream openInput(String path) throws IOException {
		// TODO: Implement this method
		InputStream in=null;
		for(AResource r:ress){
			in=r.openInput(path);
			if(in!=null)break;
		}
		return in;
	}
}
