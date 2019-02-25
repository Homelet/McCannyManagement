package mccanny.util;

import java.awt.*;

public class MyFlowLayout implements LayoutManager{
	
	private final Dimension prefDimension;
	private       int       threshold;
	private       int       verticalGap;
	private       int       horizontalGap;
	
	public MyFlowLayout(int threshold, int verticalGap, int horizontalGap){
		this.threshold = threshold;
		this.verticalGap = verticalGap;
		this.horizontalGap = horizontalGap;
		this.prefDimension = new Dimension();
	}
	
	public Dimension prefDimension(){
		return prefDimension;
	}
	
	@Override
	public void addLayoutComponent(String name, Component comp){}
	
	@Override
	public void removeLayoutComponent(Component comp){}
	
	@Override
	public Dimension preferredLayoutSize(Container target){
		synchronized(target.getTreeLock()){
			Dimension dim         = new Dimension(threshold, verticalGap * 2);
			int       nmembers    = target.getComponentCount();
			int       widthOffset = horizontalGap;
			boolean   firstCol    = true;
			for(int i = 0; i < nmembers; i++){
				Component m = target.getComponent(i);
				if(m.isVisible()){
					Dimension d = m.getPreferredSize();
					// if first Item, we assume all item are equally height
					if(i == 0)
						dim.height += d.height;
					int newWidth = widthOffset + horizontalGap + d.width;
					if(newWidth <= threshold){
						widthOffset = newWidth;
					}else{
						dim.height += verticalGap + d.height;
						widthOffset = horizontalGap + d.width;
					}
				}
			}
			return dim;
		}
	}
	
	@Override
	public Dimension minimumLayoutSize(Container target){
		synchronized(target.getTreeLock()){
			Dimension dim         = new Dimension(threshold, verticalGap * 2);
			int       nmembers    = target.getComponentCount();
			int       widthOffset = horizontalGap;
			boolean   firstCol    = true;
			for(int i = 0; i < nmembers; i++){
				Component m = target.getComponent(i);
				if(m.isVisible()){
					Dimension d = m.getMinimumSize();
					// if first Item, we assume all item are equally height
					if(i == 0)
						dim.height += d.height;
					int newWidth = widthOffset + horizontalGap + d.width;
					if(newWidth <= threshold){
						widthOffset = newWidth;
					}else{
						dim.height += verticalGap + d.height;
						widthOffset = horizontalGap + d.width;
					}
				}
			}
			return dim;
		}
	}
	
	@Override
	public void layoutContainer(Container target){
		synchronized(target.getTreeLock()){
			int     nmembers    = target.getComponentCount();
			int     widthOffset = horizontalGap;
			int     lineCount   = 0;
			boolean lineStart   = true;
			int     height      = 0;
			for(int i = 0; i < nmembers; ){
				Component m = target.getComponent(i);
				if(m.isVisible()){
					Dimension d = m.getPreferredSize();
					m.setSize(d);
					// if no line yet addItem a line
					if(lineCount == 0){
						height = d.height;
						lineCount = 1;
					}
					int newWidth = widthOffset + d.width + horizontalGap;
					// if item can fit or item is very long (greater than the threshold)
					if(newWidth <= threshold){
						m.setLocation(widthOffset, (lineCount - 1) * (d.height + verticalGap) + verticalGap);
						widthOffset = newWidth;
						lineStart = false;
						i++;
					}else if(d.width + horizontalGap * 2 > threshold){
						if(lineStart){
							m.setLocation(widthOffset, (lineCount - 1) * (d.height + verticalGap) + verticalGap);
							lineCount++;
							widthOffset = horizontalGap;
						}else{
							lineCount++;
							widthOffset = horizontalGap;
							m.setLocation(widthOffset, (lineCount - 1) * (d.height + verticalGap) + verticalGap);
							lineCount++;
						}
						i++;
					}else{
						lineCount++;
						widthOffset = horizontalGap;
						lineStart = true;
					}
				}
			}
			prefDimension.setSize(threshold, (lineCount) * (height + verticalGap) + verticalGap);
		}
	}
}
