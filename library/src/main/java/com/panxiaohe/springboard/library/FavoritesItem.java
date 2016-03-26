package com.panxiaohe.springboard.library;


import java.io.Serializable;

/**
 * item model
 *
 * @author panxiaohe
 */
public abstract class FavoritesItem implements Serializable
{

    public abstract boolean isFolder();

    public abstract String getActionName();

    public abstract void setActionName(String name);

    public abstract void setActionId(String id);

    /**
     * 如果this是文件夹,返回"-1"
     * 否则请返回该按钮的唯一识别.
     */
    public abstract String getActionId();

    /**
     * 从文件夹中删除按钮
     */
    public abstract void removeSubButton(int position);

    public abstract void addSubButton(FavoritesItem item, String defaultFolderName);

    protected abstract void addSubItem(int position, FavoritesItem item);

    protected abstract FavoritesItem removeSubItem(int position);

    public abstract int getSubItemCount();

    public abstract FavoritesItem getSubItem(int position);

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        String actionId = getActionId();
        result = prime * result + ((actionId == null) ? 0 : actionId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FavoritesItem other = (FavoritesItem) obj;
        String actionId = getActionId();
        if (actionId == null)
        {
            if (other.getActionId() != null)
                return false;
        } else if (!actionId.equals(other.getActionId()))
            return false;
        return true;
    }

}
