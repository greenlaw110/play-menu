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
    String getUrl();
    void setUrl(String url);
    String getTitle();
    void setTitle(String title);
    @Deprecated
    String getContext();
    @Deprecated
    void setContext(String context);
    boolean taggedBy(String tag);
    void setTags(Set<String> tags);
    IMenu getParentMenu();
    List<IMenu> getSubMenus();
    @Deprecated
    List<IMenu> getSubMenusByContext(String context);
    List<IMenu> getSubMenusByTag(String tag);
    List<IMenu> getTopLevelMenus();
    @Deprecated
    List<IMenu> getTopLevelMenusByContext(String context);
    List<IMenu> getTopLevelMenusByTag(String tag);
}
