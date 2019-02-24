package mccanny.visual.swing.JIndexedChooser;

public class JIndexedChooserGroup{
	
	private JIndexedChooserHandler handler;
	
	public JIndexedChooserGroup(JIndexedChooserHandler handler){
		this.handler = handler;
	}
	
	public void add(JIndexedChooser chooser){
		chooser.group = this;
	}
	
	public void remove(JIndexedChooser chooser){
		chooser.group = null;
	}
	
	public JIndexedChooserHandler handler(){
		return handler;
	}
	
	public void handler(JIndexedChooserHandler handler){
		this.handler = handler;
	}
	
	void onTrigger(JIndexedChooser initiator){
		if(handler != null)
			handler.onTrigger(new JIndexedChooserEvent(initiator));
	}
}
