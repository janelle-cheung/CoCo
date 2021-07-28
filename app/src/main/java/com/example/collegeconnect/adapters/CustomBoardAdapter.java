package com.example.collegeconnect.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.allyants.boardview.BoardAdapter;
import com.allyants.boardview.Item;
import com.example.collegeconnect.R;
import com.example.collegeconnect.models.Save;

import java.util.ArrayList;

public class CustomBoardAdapter extends BoardAdapter {

    public static final String TAG = "CustomBoardAdapter";
    public CustomBoardAdapter instance;
    int header_resource;
    int item_resource;

    public CustomBoardAdapter(Context context, ArrayList<CustomColumn> data) {
        super(context);
        instance = this;
        this.columns = (ArrayList)data;
        this.header_resource = R.layout.custom_column_header;
        this.item_resource = R.layout.custom_column_item;
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public int getItemCount(int column_position) {
        return columns.get(column_position).objects.size();
    }

    @Override
    public int maxItemCount(int column_position) {
        return -1;
    }

    @Override
    public Object createHeaderObject(int column_position) {
        return columns.get(column_position).header_object;
    }

    @Override
    public Object createFooterObject(int column_position) {
        return null;
    }

    @Override
    public Object createItemObject(int column_position, int item_position) {
        return columns.get(column_position).objects.get(item_position);
    }

    @Override
    public boolean isColumnLocked(int column_position) {
        return true; // Columns can't be dragged
    }

    @Override
    public boolean isItemLocked(int column_position) {
        return false;
    }

    @Override
    public View createItemView(Context context, Object header_object, Object item_object, int column_position, int item_position) {
        View view = View.inflate(context, item_resource, null);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        Save save = (Save) (columns.get(column_position).objects.get(item_position));
        textView.setText(save.getCollegeName());
        view.setTag(save);
        return view;
    }

    @Override
    public View createHeaderView(Context context, Object header_object, int column_position) {
        View column = View.inflate(context, header_resource, null);
        TextView textView = (TextView) column.findViewById(R.id.textView);
        textView.setText(columns.get(column_position).header_object.toString());
        return column;
    }

    @Override
    public View createFooterView(Context context, Object footer_object, int column_position) {
        return null;
    }

    public static class CustomColumn extends Column {
        public String title;
        public CustomColumn(String title, ArrayList<Object> items) {
            super();
            this.title = title;
            this.header_object = new Item(title);
            this.objects = items;
        }
    }
}
