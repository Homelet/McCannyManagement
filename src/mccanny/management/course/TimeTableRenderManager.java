package mccanny.management.course;

import homelet.GH.visual.interfaces.LocatableRender;
import homelet.GH.visual.interfaces.RenderParent;
import homelet.GH.visual.interfaces.Renderable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TimeTableRenderManager implements Renderable{
	
	// render list
	private final List<Renderable> preRenderList;
	private final List<Renderable> renderList;
	private final List<Renderable> postRenderList;
	
	public TimeTableRenderManager(){
		this.preRenderList = Collections.synchronizedList(new ArrayList<>());
		this.renderList = Collections.synchronizedList(new ArrayList<>());
		this.postRenderList = Collections.synchronizedList(new ArrayList<>());
	}
	
	public void start(){
	}
	
	public void addPreTargets(Renderable... r){
		synchronized(preRenderList){
			add(preRenderList, r);
		}
	}
	
	public void addTargets(Renderable... r){
		synchronized(renderList){
			add(renderList, r);
		}
	}
	
	public void addPostTargets(Renderable... r){
		synchronized(postRenderList){
			add(postRenderList, r);
		}
	}
	
	public void addPreTargets(int position, Renderable... r){
		synchronized(preRenderList){
			add(preRenderList, position, r);
		}
	}
	
	public void addTargets(int position, Renderable... r){
		synchronized(renderList){
			add(renderList, position, r);
		}
	}
	
	public void addPostTargets(int position, Renderable... r){
		synchronized(postRenderList){
			add(postRenderList, position, r);
		}
	}
	
	private void addCheck(Renderable... r){
		for(Renderable renderable : r){
			preRenderList.remove(renderable);
			renderList.remove(renderable);
			postRenderList.remove(renderable);
		}
	}
	
	private void add(List<Renderable> list, int position, Renderable... r){
		addCheck(r);
		list.addAll(position, Arrays.asList(r));
	}
	
	private void add(List<Renderable> list, Renderable... r){
		addCheck(r);
		Collections.addAll(list, r);
	}
	
	public void removeAllPre(){
		synchronized(preRenderList){
			remove(preRenderList);
		}
	}
	
	public void removeAllRender(){
		synchronized(renderList){
			remove(renderList);
		}
	}
	
	public void removeAllPost(){
		synchronized(postRenderList){
			remove(postRenderList);
		}
	}
	
	private void remove(List<Renderable> list){
		for(int index = 0; index < list.size(); index++){
			removeTargets(list.get(index));
		}
	}
	
	public void removeTargets(Renderable... r){
		for(Renderable renderable : r){
			preRenderList.remove(renderable);
			renderList.remove(renderable);
			postRenderList.remove(renderable);
		}
	}
	
	@Override
	public void tick(){}
	
	@Override
	public void render(Graphics2D g){
		preRender(g);
		doRender(g);
		postRender(g);
	}
	
	private void doRender(Graphics2D g){
		synchronized(renderList){
			doRenderProcess(g, renderList);
		}
	}
	
	private void preRender(Graphics2D g){
		synchronized(preRenderList){
			doRenderProcess(g, preRenderList);
		}
	}
	
	private void postRender(Graphics2D g){
		synchronized(postRenderList){
			doRenderProcess(g, postRenderList);
		}
	}
	
	private void doRenderProcess(Graphics2D g, List<? extends Renderable> renderList){
		if(renderList.isEmpty())
			return;
		for(Renderable renderable : renderList){
			if(renderable instanceof RenderParent){
				Graphics2D   localGraphics = g;
				RenderParent parent        = (RenderParent) renderable;
				if(parent.isRenderedBefore())
					localGraphics = renderProcess(localGraphics, renderable);
				List<? extends Renderable> subRenderable = parent.getChildren();
				if(subRenderable != null)
					doRenderProcess(localGraphics, subRenderable);
				if(!parent.isRenderedBefore())
					renderProcess(localGraphics, renderable);
			}else{
				renderProcess(g, renderable);
			}
		}
	}
	
	private Graphics2D renderProcess(Graphics2D g, Renderable renderable){
		Rectangle clipBounds = g.getClipBounds();
		Dimension size       = null;
		Point     vertex     = null;
		if(renderable instanceof LocatableRender){
			LocatableRender locatableRender = ((LocatableRender) renderable);
			size = locatableRender.getSize();
			vertex = locatableRender.getVertex(clipBounds);
		}
		if(size == null)
			size = clipBounds.getSize();
		if(vertex == null)
			vertex = new Point(0, 0);
		Rectangle  bounds = clipBounds.intersection(new Rectangle(vertex, size));
		Graphics2D g2     = (Graphics2D) g.create(bounds.x, bounds.y, bounds.width, bounds.height);
		if(renderable.isTicking())
			renderable.tick();
		if(renderable.isRendering())
			renderable.render(g2);
		return g2;
	}
}
