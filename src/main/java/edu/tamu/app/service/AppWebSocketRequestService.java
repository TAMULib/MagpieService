/* 
 * AppWebSocketRequestService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.service;


import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import edu.tamu.framework.model.WebSocketRequest;
import edu.tamu.framework.service.WebSocketRequestService;

/**
 * Class AppWebSocketRequestService
 * 
 * @author
 */
@Service
public class AppWebSocketRequestService extends WebSocketRequestService {

	/**
	 * gets message and sets request 
	 * 
	 * @param       destination     String
	 * @param       user            String
	 * @param       index           int
	 * 
	 * @see edu.tamu.framework.service.WebSocketRequestService#getMessageAndSetRequest(java.lang.String, java.lang.String, int)
	 */
	@Override
	public Message<?> getMessageAndSetRequest(String destination, String user, int index) {
		
		Message<?> message = null;
		WebSocketRequest request = requests.get(index);

		if(destination.contains("/saf/{project}")) {			
			if(request.getUser().equals(user) && request.getDestination().contains("saf")) {				
				message = request.getMessage();
				requests.remove(index);
			}			
		} 
		else if(destination.contains("/csv/{project}")) {			
			if(request.getUser().equals(user) && request.getDestination().contains("csv")) {				
				message = request.getMessage();
				requests.remove(index);
			}			
		} 
		else if(destination.contains("/headers/{project}")) {			
			if(request.getUser().equals(user) && request.getDestination().contains("headers")) {	
				message = request.getMessage();
				requests.remove(index);
			}
		} 
		else if(destination.contains("/marc/{bibId}")) {			
			if(request.getUser().equals(user) && request.getDestination().contains("marc")) {	
				message = request.getMessage();
				requests.remove(index);
			}
		} 
		else if (destination.equals("/{label}")) {
			if(request.getUser().equals(user)) {
				message = request.getMessage();
				requests.remove(index);
			}
		}
		else {
			System.out.println("Unknown destination: " + destination);
		}
		
		return message;
	}

}
