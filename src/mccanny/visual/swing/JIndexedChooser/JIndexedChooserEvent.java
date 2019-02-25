package mccanny.visual.swing.JIndexedChooser;

public class JIndexedChooserEvent{
	
	public static final int             EVENT_INCREASE = 0;
	public static final int             EVENT_DECEASE  = 1;
	private final       JIndexedChooser initiator;
	private final       int             eventFlag;
	private final       double          value;
	
	JIndexedChooserEvent(JIndexedChooser initiator, int eventFlag, double value){
		this.initiator = initiator;
		this.eventFlag = eventFlag;
		this.value = value;
	}
	
	public JIndexedChooser initiator(){
		return initiator;
	}
	
	public int eventFlag(){
		return eventFlag;
	}
	
	public double value(){
		return value;
	}
}
