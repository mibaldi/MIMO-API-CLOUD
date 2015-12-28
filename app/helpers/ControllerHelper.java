package helpers;

import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ControllerHelper {

	public static JsonNode errorJson(Integer code, String message, JsonNode errors) {
		ObjectNode node = Json.newObject();
		node.put("code", code);
		node.put("message", Messages.get(message));
		node.putPOJO("errors", errors);
		return node;
	}
	
	public static boolean acceptsJson(Request request) {
		return request.accepts("application/json");
	}
	
	public static boolean acceptsXml(Request request) {
		return (request.accepts("application/xml") || request.accepts("text/xml"));
	}
	public static void headers(Response response){
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
	}

}
