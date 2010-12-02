package controllers;


import models.IMenu;
import models._Menu;
import play.modules.menu.MenuPlugin;
import play.mvc.Before;
import play.mvc.Controller;

public class Application extends Controller {
    
    @Before
    public static void before() {
        Class menuClass = MenuPlugin.menuInstance().getClass();
        renderArgs.put("menuClass", menuClass.getName());
        if (menuClass.equals(_Menu.class)) {
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
        render();
    }
    
    public static void setMenuClass(String menuClass) {
        MenuPlugin.setMenuClass(menuClass);
        index();
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
