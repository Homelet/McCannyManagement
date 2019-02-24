package mccanny.visual.swing.JIndexedChooser;

public class JIndexedChooserEvent{
	
	private final JIndexedChooser initiator;
	
	public JIndexedChooserEvent(JIndexedChooser initiator){
		this.initiator = initiator;
	}
	
	public JIndexedChooser initiator(){
		return initiator;
	}
}
