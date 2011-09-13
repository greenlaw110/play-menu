package models._menu;

import java.util.Collection;
import java.util.List;

public interface IMenu {
    Object _getId();
    String getName();
    void setName(String name);
    String getCssClass();
    void setCssClass(String cssClass);
    String getUrl();
    void setUrl(String url);
    String getTitle();
    void setTitle(String title);
    boolean hasLabel(String label);
    void setLabels(Collection<String> labels);
    IMenu getParent();
    void setParent(IMenu parent);
    List<IMenu> getSubMenus();
    List<IMenu> getSubMenusByLabel(String label);

    List<IMenu> _topLevelMenus();
    List<IMenu> _topLevelMenusByLabel(String label);
    List<IMenu> _all();
    IMenu _newInstance();
    long _count();
    IMenu _findById(String id);
    void _save();
    void _delete();
    void _purge();
}
