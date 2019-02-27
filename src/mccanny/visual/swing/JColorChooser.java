package mccanny.visual.swing;

import homelet.GH.handlers.GH;
import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import mccanny.util.Utility;
import mccanny.visual.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class JColorChooser extends JComponent{
	
	private final JColorLabel label;
	
	public JColorChooser(JComponent parent, Color initColor){
		label = new JColorLabel(initColor);
		JButton button = new JButton("Choose Color");
		button.addActionListener((event)->{
			Color color = javax.swing.JColorChooser.showDialog(parent, "Color Prompt", label.color);
			if(color == null)
				return;
			label.color = color;
			label.repaint();
		});
		button.setToolTipText("Click To change Color.");
		button.setFont(Display.CLEAR_SANS_BOLD);
		Layouter.GridBagLayouter layout = new GridBagLayouter(this);
		layout.put(layout.instanceOf(label, 0, 0).setAnchor(Anchor.LEFT).setFill(Fill.BOTH).setWeight(80, 100).setInsets(0, 0, 0, 10));
		layout.put(layout.instanceOf(button, 1, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(20, 100));
	}
	
	public Color getColor(){
		return label.color;
	}
	
	private class JColorLabel extends JComponent implements MouseListener{
		
		Color color;
		
		JColorLabel(Color color){
			this.color = color;
			this.addMouseListener(this);
			this.setFocusable(true);
			this.setToolTipText("Double Click for Quick Color Prompt.");
		}
		
		@Override
		protected void paintComponent(Graphics g){
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(color);
			g2.fill(GH.rRectangle(false, 0, 0, this.getWidth(), this.getHeight(), this.getHeight(), this.getHeight()));
		}
		
		@Override
		public void mouseClicked(MouseEvent e){
			if(e.getClickCount() == 2){
				Color color = showPrompt();
				if(color == null)
					return;
				this.color = color;
				this.repaint();
			}
		}
		
		@Override
		public void mousePressed(MouseEvent e){}
		
		@Override
		public void mouseReleased(MouseEvent e){}
		
		@Override
		public void mouseEntered(MouseEvent e){}
		
		@Override
		public void mouseExited(MouseEvent e){}
		
		private Color showPrompt(){
			String text           = "Please Input With the Following Syntax:\nHEX :\n\t\t#FFFFFF\nRGB or ARGB :\n\t\tred, green, blue\n\t\tred, green, blue, alpha";
			String preColorString = color.getRed() + "," + color.getGreen() + "," + color.getBlue() + (color.getAlpha() == 255 ? "" : "," + color.getAlpha());
			String input          = (String) JOptionPane.showInputDialog(JColorLabel.this, text, "Quick Color Prompt", JOptionPane.PLAIN_MESSAGE, null, null, preColorString);
			return input == null ? null : Utility.translateColor(input);
		}
	}
}
