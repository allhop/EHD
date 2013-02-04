package com.nova.ehd.error;

public class EHDParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8149308110782948248L;
	
	
	
	public EHDParseException(String message) {
		outMessage = message;
	}
	
	String outMessage = new String();
	String documentBody = new String();
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		
		
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append(super.getMessage());
		messageBuilder.append("\n");
		if(outMessage!= null) {
			messageBuilder.append(outMessage);
			messageBuilder.append("\n");
			
		}
		messageBuilder.append(documentBody);

		return messageBuilder.toString();
	}
	
	public void setDocumentBody(String aDocumentBody) {
		this.documentBody = aDocumentBody;
	}
	
	
	
}
