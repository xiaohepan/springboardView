# springboard
    this is a view that can drag sort buttons and merge buttons to a folder.
    这个view ，能够拖动排序菜单，和合并文件夹。
#Features
    1:drag sort the buttons.拖动排序
    2:drag button into a folder.把按钮拖进文件夹
    3:drag button out of a folder.把按钮拖出文件夹
    4:delete buttons in menu and folder.能够删除菜单和文件夹中的按钮
    5:rename the folder.文件夹重命名.
#How to Work with the Source

    1:make your data model extends com.panxiaohe.springboard.library.FavoritesItem;

    2:make your adapter extends com.panxiaohe.springboard.library.SpringboardAdapter;

    3:set the adapter to com.panxiaohe.springboard.library.MenuView;

    the attrs of the view :
        <attr name="row_count" format="integer"/>
        <attr name="column_count" format="integer"/>
        <attr name="divider_color" format="color"/>
        <attr name="divider_width" format="dimension"/>
        <attr name="default_folder_name" format="string|reference"/>
        <attr name="delete_icon" format="reference"/>
        <attr name="page_divider_width" format="dimension"/>
        <attr name="is_last_item_stable" format="boolean"/>
        <attr name="stable_header_count" format="integer"/>
        <attr name="folder_dialog_background" format="color|reference"/>
        <attr name="folder_view_background" format="color|reference"/>
        <attr name="folder_row_count" format="integer"/>
        <attr name="folder_column_count" format="integer"/>
        <attr name="folder_edittext_background" format="color|reference"/>
        <attr name="folder_edittext_textcolor" format="color|reference"/>
        <attr name="folder_edittext_textsize" format="dimension|reference"/>
#notice
    this view is not well tested.