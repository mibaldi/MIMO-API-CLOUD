package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public Result index() {
        return redirect("http://mibaldi.com/swagger/dist/index.html");
    }
    public Result web(){
    	return redirect("http://mibaldi.com/swagger/dist/index.html");
    }

}
