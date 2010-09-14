package controllers;

import play.mvc.Controller;
import play.mvc.With;

public class DefaultMenu extends Controller {
    public static void index() {
        render();
    }
    
    public static void doc() {
        render();
    }
    
    public static void community() {
        render();
    }
    
    public static void code() {
        render();
    }
    
    public static void modules(String id) {
        if (null != id) {
            renderArgs.put("_menu_current", id);
        }
        render(id);
    }
}
