package controllers;


import models._menu.JPAMenu;
import play.modules.menu.MenuPlugin;
import play.mvc.Before;
import play.mvc.Controller;

public class Application extends Controller {
    
    @Before
    public static void before() {
        Class menuClass = MenuPlugin.menuInstance().getClass();
        renderArgs.put("menuClass", menuClass.getName());
        if (menuClass.equals(JPAMenu.class)) {
            renderArgs.put("style", 2);
        }
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
        render(id);
    }
    
    public static void sql() {
        render();
    }
    
    public static void nonSql() {
        render();
    }
    
    public static void mysql() {
        render();
    }
    
    public static void postgres() {
        render();
    }
    
    public static void mongodb() {
        render();
    }
}
