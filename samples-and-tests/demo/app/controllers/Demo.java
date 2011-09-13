package controllers;

import play.mvc.Controller;

public class Demo extends Controller {
    
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
        render(id);
    }
    
    public static void logout() {
        render();
    }
    
}
