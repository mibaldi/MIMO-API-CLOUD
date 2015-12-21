package util;
import models.UsuarioModel;
import controllers.routes;
import play.mvc.Http.*;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;

public class secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
    	String token= getTokenFromHeader(ctx);
    	if (token!=null){
    		UsuarioModel u= UsuarioModel.find.where().eq("TOKEN",token).findUnique();
    		if (u!=null){
    			ctx.args.put("usuario_logado", u);
    			return u.username;
    		}return null;
    	}return null;
    }
    @Override
    public Result onUnauthorized(Context ctx) {
        return Results.unauthorized("Acceso denegado");
    }
    private String getTokenFromHeader(Http.Context ctx) {
        String[] authTokenHeaderValues = ctx.request().headers().get("X-AUTH-TOKEN");
        if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
            return authTokenHeaderValues[0];
        }
        return null;
    }
}