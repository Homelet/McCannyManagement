package mccanny.util;

import java.awt.*;

public interface ImageLocateable{
	
	Dimension getSize();
	
	Point getVertex(int renderOffset, int lineIndex);
}
