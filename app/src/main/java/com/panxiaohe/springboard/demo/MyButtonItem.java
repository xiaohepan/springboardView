package com.panxiaohe.springboard.demo;

import com.panxiaohe.springboard.library.FavoritesItem;

import java.util.ArrayList;

/**
 * Created by panxiaohe on 16/3/20.
 * 继承
 */
public class MyButtonItem extends FavoritesItem<MyButtonItem>
{

    private String actionName;

    private String actionId;

    private int icon;

    private ArrayList<MyButtonItem>  menuList;


    @Override
    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    @Override
    public String getActionName()
    {
        return actionName;
    }

    @Override
    public void setActionId(String actionId)
    {
        this.actionId = actionId;
    }

    @Override
    public String getActionId()
    {
        return actionId;
    }

    @Override
    public boolean isFolder()
    {
        return menuList != null;
    }

    @Override
    public void addSubButton(MyButtonItem item, String defaultFolderName)
    {
        if(menuList ==null||menuList.size() == 0)
        {
            menuList = new ArrayList<>();

            MyButtonItem newItem= new MyButtonItem();
            newItem.copy(this);
            menuList.add(newItem);
            setActionId("-1");
            setActionName(defaultFolderName);
        }
        menuList.add(item);
    }

    @Override
    public void removeSubButton(int position)
    {
        menuList.remove(position);

        if(menuList.size() == 1)
        {
            copy(menuList.get(0));
            menuList = null;
        }
    }

    private void copy(MyButtonItem myButtonItem)
    {
        this.actionName = myButtonItem.actionName;
        this.actionId = myButtonItem.actionId;
        this.icon = myButtonItem.icon;
    }

    @Override
    public ArrayList<MyButtonItem> getMenuList() {
        return menuList;
    }

    @Override
    public void setMenuList(ArrayList<MyButtonItem> menuList)
    {
        if(menuList!=null && menuList.size() ==1)
        {
            throw new IllegalStateException("menuList.size() must large than 1");
        }

        this.menuList = menuList;
    }

    @Override
    public int getSubItemCount() {
        return menuList == null ? 0:menuList.size();
    }

    @Override
    public MyButtonItem getSubItem(int position)
    {
        if(menuList != null)
        {
            if(menuList.size() > position)
            {
                return menuList.get(position);
            }

        }
        throw  new IllegalStateException("按钮列表是空");
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public MyButtonItem(String actionId,String actionName,int icon) {
        this.actionName = actionName;
        this.actionId = actionId;
        this.icon = icon;
    }

    public MyButtonItem()
    {
    }
}
