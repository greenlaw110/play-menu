package models;

import java.util.List;

public interface IMenu {
    String getName();
    String getUrl();
    String getTitle();
    String getContext();
    IMenu getParentMenu();
    List<IMenu> getSubMenus();
    List<IMenu> getSubMenusByContext(String context);
    List<IMenu> getTopLevelMenus();
    List<IMenu> getTopLevelMenusByContext(String context);
}
