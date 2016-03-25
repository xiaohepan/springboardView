package com.panxiaohe.springboard.library;


import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * Created by panxiaohe on 16/3/19.
 * 适配器.
 */
public abstract class SpringboardAdapter<T extends FavoritesItem>
{
    private ArrayList<T> items;

    private boolean isEditing = false;

    private SoftReference<MenuView> springboardView;

    private SoftReference<FolderView> folderView;

//    public SpringboardAdapter(ArrayList<T> items)
//    {
//        if(items == null)
//        {
//            throw new IllegalStateException("请输入按钮列表");
//        }
//        this.items = items;
//    }
    /**
     * 首个层级数量
     * */
    public  int getCount()
    {
        return items.size();
    }
    /**
     * 文件夹里按钮数量
     * */
    public int getSubItemCount(int position)
    {
        return items.get(position).getSubItemCount();
    }

    /**
     * 首个层级item
     * */
    public   T getItem(int position)
    {
        return items.get(position);
    }
    /**
     * 首个层级item
     * */
    public T getSubItem(int folderPosition,int position)
    {
        return (T)items.get(folderPosition).getSubItem(position);
    }

    /**
     * @return 只需返回所需的view,所有设置请在configUI里完成
     * */
    public abstract FrameLayout initItemView(int position, ViewGroup parent);

    public abstract void configItemView(int position,FrameLayout frameLayout);

    /**
     * @return 只需返回所需的view,所有设置请在configUI里完成
     * */
    public abstract FrameLayout initSubItemView(int folderPosition,int position, ViewGroup parent);

    public abstract void configSubItemView(int folderPosition,int position,FrameLayout frameLayout);

    public void exchangeItem(int fromPosition ,int toPosition)
    {
        T item = items.remove(fromPosition);

        SpringboardView container = getSpringboardView();

        FrameLayout view = (FrameLayout)container.getChildAt(fromPosition);

        container.removeView(view);

        items.add(toPosition, item);

        onDataChange();

        container.addView(view, toPosition);

        getSpringboardView().setEditingMode(toPosition, view, isEditing, true);

    }

    public void exChangeSubItem(int folderPosition,int fromPosition ,int toPosition)
    {
        T folder = items.get(folderPosition);

        T item = (T)folder.getMenuList().remove(fromPosition);

        SpringboardView container = getFolderView();

        FrameLayout view = (FrameLayout)container.getChildAt(fromPosition);

        container.removeView(view);

        folder.getMenuList().add(toPosition, item);

        onDataChange();

        container.addView(view, toPosition);

        configItemView(folderPosition, (FrameLayout) getSpringboardView().getChildAt(folderPosition));

        getFolderView().setEditingMode(toPosition, view, isEditing, true);

    }

    public void deleteItem(int position)
    {
        items.remove(position);

        onDataChange();

        getSpringboardView().removeViewAt(position);
    }

    public void deleteItem(int folderPosition,int position)
    {
        T folder = items.get(folderPosition);

        folder.removeSubButton(position);

        onDataChange();

        getFolderView().removeViewAt(position);

        FrameLayout view = (FrameLayout) getSpringboardView().getChildAt(folderPosition);

        configItemView(folderPosition, view);

        if(!folder.isFolder())
        {
            getSpringboardView().setEditingMode(folderPosition, view, isEditing, true);
            getSpringboardView().removeFolder();
        }else
        {
            getSpringboardView().setEditingMode(folderPosition, view, isEditing, false);
        }
    }


    public void mergeItem(int fromPosition, int toPosition,String defaultFolderName)
    {
        T fromItem = items.get(fromPosition);

        T toItem = items.get(toPosition);

        toItem.addSubButton(fromItem, defaultFolderName);

        FrameLayout view = (FrameLayout) getSpringboardView().getChildAt(toPosition);

        configItemView(toPosition, view);

        getSpringboardView().setEditingMode(toPosition, view, isEditing, true);

        items.remove(fromPosition);

        onDataChange();

        getSpringboardView().removeViewAt(fromPosition);
    }

    public T tempRemoveItem(int folderPosition,int position)
    {
        T folder = items.get(folderPosition);

        T item = (T) folder.getSubItem(position);

        folder.removeSubButton(position);

        FrameLayout view = (FrameLayout) getSpringboardView().getChildAt(folderPosition);

        configItemView(folderPosition, view);

        getSpringboardView().setEditingMode(folderPosition, view, isEditing, true);

        return item;
    }

    public void addItem(int position , T item)
    {
        items.add(position, item);

        onDataChange();

        FrameLayout view = getSpringboardView().initItemView(position);
        getSpringboardView().configView(view);
        getSpringboardView().addView(view, position);

        getSpringboardView().setEditingMode(position, view, isEditing, true);

    }

    public void addItemToFolder(int dragPosition, T dragOutItem,String defaultName)
    {
        T folder = items.get(dragPosition);

        folder.addSubButton(dragOutItem, defaultName);

        onDataChange();

        configItemView(dragPosition, (FrameLayout) getSpringboardView().getChildAt(dragPosition));
    }

    public MenuView getSpringboardView()
    {
        return springboardView.get();
    }

    public void setSpringboardView(MenuView mSpringboardView)
    {
        this.springboardView = new SoftReference<>(mSpringboardView);
    }

    public FolderView getFolderView()
    {
        if(folderView != null)
        {
            return folderView.get();

        }
        return null;
    }

    public void setFolderView(FolderView folderView) {
        this.folderView = new SoftReference<>(folderView);
    }

    /**
     * @param position 位置
     * @return true 可以删除
     * */
    public boolean ifCanDelete(int position)
    {
//        Log.e("SpringboardAdapter",getItem(position).toString());
        return !getItem(position).isFolder();
    }

    /**
     * @param position 位置
     * @return true 可以删除
     * */
    public boolean ifCanDelete(int folderPosition,int position)
    {
        return !getSubItem(folderPosition, position).isFolder();
    }

    public boolean ifCanMerge(int fromPosition, int toPosition)
    {
        return !(getItem(fromPosition).isFolder() || (!getSpringboardView().ifCanMove(toPosition)));
    }

    public void changeFolderName(String name)
    {
        int folderPosition = getFolderView().getFolderPosition();
        T item = items.get(folderPosition);
        if(item.isFolder())
        {
            String oldName = items.get(folderPosition).getActionName();
            if(!oldName.equals(name))
            {
                items.get(folderPosition).setActionName(name);

                onDataChange();

                configItemView(folderPosition, (FrameLayout) getSpringboardView().getChildAt(folderPosition));
            }
        }
    }

    public void setEditing(boolean isEditing)
    {
        if(this.isEditing != isEditing)
    	{
//            Log.e("SpringboardAdapter", "编辑模式" + isEditing);
            this.isEditing = isEditing;
            FolderView folder;
            if(folderView!=null&& (folder = folderView.get()) != null)
            {
                int count = folder.getChildCount();
                for(int i = 0 ; i < count;i++)
                {
                    View child = folder.getChildAt(i);
                    folder.setEditingMode(i, child, isEditing,true);
                }
                if(isEditting())
                {
                    folder.requestFocus();
                }
                int count1 = getSpringboardView().getChildCount();
                for(int i = 0 ; i < count1;i++)
                {
                    View child1 = getSpringboardView().getChildAt(i);
                    getSpringboardView().setEditingMode(i, child1, isEditing,false);
                }
            }else
            {
                if(isEditting())
                {
                    getSpringboardView().requestFocus();
                }
                int count1 = getSpringboardView().getChildCount();
                for(int i = 0 ; i < count1;i++)
                {
                    View child1 = getSpringboardView().getChildAt(i);
                    getSpringboardView().setEditingMode(i, child1, isEditing,true);
                }
            }
    	}
    }

    public void initFolderEditingMode()
    {
        if (isEditing)
        {
            FolderView folder;
            if(folderView!=null&& (folder = folderView.get()) != null)
            {
                int count = folder.getChildCount();
                for(int i = 0 ; i < count;i++)
                {
                    View child = folder.getChildAt(i);
                    folder.setEditingMode(i, child, isEditing, true);
                }
                if(isEditting())
                {
                    folder.requestFocus();
                }
            }
        }
    }

    public boolean isEditting()
    {
        return isEditing;
    }

    public void removeFolder()
    {
        setFolderView(null);
    }

    public abstract void onDataChange();

    public ArrayList<T> getItems()
    {
        return items;
    }

    public void setItems(ArrayList<T> items) {
        this.items = items;
    }
}