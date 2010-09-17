package controllers;


import play.mvc.Before;
import play.mvc.Controller;

public class DefaultMenu extends Controller {
    
    @Before
    public static void setTag() {
        //renderArgs.put("_menu_tag", "french");
    }
    
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
        /*
        if (null != id) {
            renderArgs.put("_menu_current", id);
        }
        render(id);
        */
        render();
    }
}
