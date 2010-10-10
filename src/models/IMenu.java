package models;

import java.util.List;
import java.util.Set;

public interface IMenu {
    String getName();
    /**
     * 
     * @param name
     * @throws NullPointerException if name is null
     */
    void setName(String name);
    String getCssClass();
    void setCssClass(String cssClass);
    String getUrl();
    void setUrl(String url);
    String getTitle();
    void setTitle(String title);
    @Deprecated
    String getContext();
    @Deprecated
    void setContext(String context);
    boolean hasLabel(String label);
    void setLabels(Set<String> labels);
    IMenu getParentMenu();
    List<IMenu> getSubMenus();
    @Deprecated
    List<IMenu> getSubMenusByContext(String context);
    List<IMenu> getSubMenusByLabel(String label);
    List<IMenu> getTopLevelMenus();
    @Deprecated
    List<IMenu> getTopLevelMenusByContext(String context);
    List<IMenu> getTopLevelMenusByLabel(String label);
    
    /**
     * Implementation should use this method to load menu definition file 
     * 
     * Implementation should NOT throw exception from this method
     */
    void loadMenu();
}
