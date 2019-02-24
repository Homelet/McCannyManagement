package mccanny.visual.swing.JIndexedChooser;

import homelet.GH.handlers.Layouter;
import homelet.GH.handlers.Layouter.GridBagLayouter;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Anchor;
import homelet.GH.handlers.Layouter.GridBagLayouter.GridConstrain.Fill;
import homelet.GH.utils.ToolBox.Orientation;
import mccanny.util.Utility;
import mccanny.visual.Display;

import javax.swing.*;
import java.awt.event.MouseAdapter;

public class JIndexedChooser extends JComponent{
	
	public static ValueProcessor DEFAULT_PROCESSOR = value->value - Math.floor(value) == 0 ? String.valueOf((int) value) : String.valueOf(value);
	private final JLabel         label;
	private final JButton        plus;
	private final JButton        minus;
	private       double         value;
	private       double         step;
	private       double         max;
	private       double         min;
	private       ValueProcessor processor;
	JIndexedChooserGroup group;
	
	public JIndexedChooser(JComponent parent, double init, double step, Orientation orientation){
		this(parent, step, init, Integer.MAX_VALUE, Integer.MIN_VALUE, orientation, DEFAULT_PROCESSOR);
	}
	
	public JIndexedChooser(JComponent parent, double step, double init, double max, double min, Orientation orientation, ValueProcessor processor){
		super();
		this.step = step;
		this.max = max;
		this.min = min;
		this.value = 0;
		this.processor(processor);
		this.plus = new JButton("+".concat(step != 1 ? String.valueOf(step) : ""));
		this.plus.addActionListener((actionEvent)->{
			processShift(+this.step);
		});
		this.minus = new JButton("-".concat(step != 1 ? String.valueOf(step) : ""));
		this.minus.addActionListener((actionEvent)->{
			processShift(-this.step);
		});
		this.label = new JLabel();
		this.label.setHorizontalAlignment(JLabel.CENTER);
		this.label.setFont(Display.CLEAR_SANS_BOLD);
		this.plus.setFont(Display.CLEAR_SANS_BOLD);
		this.minus.setFont(Display.CLEAR_SANS_BOLD);
		addAction(parent);
		processShift(init);
		Layouter.GridBagLayouter layouter = new GridBagLayouter(this);
		switch(orientation){
			case EQUILATERAL:
			case HORIZONTAL:
				layouter.put(layouter.instanceOf(minus, 0, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100));
				layouter.put(layouter.instanceOf(label, 1, 0).setAnchor(Anchor.CENTER).setFill(Fill.VERTICAL).setWeight(100, 100));
				layouter.put(layouter.instanceOf(plus, 2, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100));
				break;
			case VERTICAL:
				layouter.put(layouter.instanceOf(minus, 0, 0).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100));
				layouter.put(layouter.instanceOf(label, 0, 1).setAnchor(Anchor.CENTER).setFill(Fill.HORIZONTAL).setWeight(100, 100));
				layouter.put(layouter.instanceOf(plus, 0, 2).setAnchor(Anchor.CENTER).setFill(Fill.BOTH).setWeight(0, 100));
				break;
		}
	}
	
	private void processShift(double shift){
		processValue(value + shift);
	}
	
	private void addAction(JComponent parent){
		label.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e){
				if(e.getClickCount() == 2 && label.isEnabled()){
					String filen = (String) (JOptionPane.showInputDialog(parent, "Enter your new Value", "Enter new value", JOptionPane.PLAIN_MESSAGE, null, null, String.valueOf(value)));
					if(filen == null){
						return;
					}
					double value = 0;
					try{
						value = Double.parseDouble(filen);
					}catch(NumberFormatException es){
						JOptionPane.showMessageDialog(parent, "Unparsable Input For \"".concat(filen).concat("\""), "Unparsable Input", JOptionPane.ERROR_MESSAGE, null);
						return;
					}
					switch(Utility.betweenPeaks(value, max, min)){
						case -2:
							switch(JOptionPane.showConfirmDialog(parent, "The value \"" + filen + "\" is smaller than it's minimum\nDo you want to continue as Minimum?", "illegal Argument", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null)){
								case JOptionPane.NO_OPTION:
									return;
								case JOptionPane.YES_OPTION:
									processValue(min);
							}
							break;
						case 2:
							switch(JOptionPane.showConfirmDialog(parent, "The value \"" + filen + "\" is bigger than it's maximum\nDo you want to continue as Maximum?", "illegal Argument", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null)){
								case JOptionPane.NO_OPTION:
									return;
								case JOptionPane.YES_OPTION:
									processValue(max);
							}
							break;
						case -1:
						case 1:
						case 0:
							break;
					}
					processValue(value);
				}
			}
		});
	}
	
	public void processValue(double newValue){
		switch(Utility.betweenPeaks(newValue, max, min)){
			case -2:
			case -1:
				setButtonEnable(false, true);
				setValue(min);
				break;
			case 2:
			case 1:
				setButtonEnable(true, false);
				setValue(max);
				break;
			case 0:
				setButtonEnable(true, true);
				setValue(newValue);
				break;
		}
		if(this.group != null)
			this.group.onTrigger(this);
	}
	
	public void setButtonEnable(boolean decrease, boolean increase){
		minus.setEnabled(decrease);
		plus.setEnabled(increase);
	}
	
	private void setValue(double value){
		this.value = value;
		label.setText(processor.process(value));
	}
	
	public double value(){
		return value;
	}
	
	public double step(){
		return step;
	}
	
	public double max(){
		return max;
	}
	
	public double min(){
		return min;
	}
	
	public void range(int min, int max){
		this.max = Math.max(min, max);
		this.min = Math.min(min, max);
		processValue(value);
	}
	
	public ValueProcessor processor(){
		return processor;
	}
	
	public void processor(ValueProcessor processor){
		if(processor == null)
			processor = DEFAULT_PROCESSOR;
		this.processor = processor;
	}
	
	public interface ValueProcessor{
		
		String process(double value);
	}
}