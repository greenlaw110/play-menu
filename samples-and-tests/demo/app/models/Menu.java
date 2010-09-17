package models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;


@Entity(value="my_menu")
public class Menu extends Model implements IMenu {
    
    @Indexed(unique=true, dropDups=true)
    public String name;
    public String url;
    public String title;
    public String context;
    public Set<String> tags;
    
    @Reference(value="p")
    public Menu parent;
    
    public static Menu findByName(String name) {
        return (Menu)filter("name", name).get();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        if (null == name) throw new NullPointerException();
        this.name = name;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public String getUrl() {
        return url;
    }
    
    @Override
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public String getContext() {
        return context;
    }
    
    @Override
    public void setContext(String context) {
        this.context = context;
    }
    
    @Override
    public boolean taggedBy(String tag) {
        if (null == tags) return false;
        return tags.contains(tag);
    }
    
    @Override
    public void setTags(Set<String> tags) {
        if (null == tags) return;
        this.tags = new HashSet(tags);
    }
    
    @Override
    public IMenu getParentMenu() {
        return parent;
    }
    
    @Override
    public List<IMenu> getSubMenus() {
        return Menu.filter("parent", this).asList();
    }
    
    @Override
    public List<IMenu> getSubMenusByContext(String context) {
        return Menu.filter("parent", this).filter("context", context).asList();
    }
    
    @Override
    public List<IMenu> getSubMenusByTag(String tag) {
        return Menu.filter("parent", this).filter("tags", tag).asList();
    }
    
    @Override
    public List<IMenu> getTopLevelMenus() {
        return (List<IMenu>)filter("parent", null).asList();
    }
    
    @Override
    public List<IMenu> getTopLevelMenusByContext(String context) {
        return filter("parent", null).filter("context", context).asList();
    }
    
    @Override
    public List<IMenu> getTopLevelMenusByTag(String tag) {
        return filter("parent", null).filter("tags", tag).asList();
    }
}
