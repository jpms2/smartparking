package br.cin.ufpe.inesescin.smartparking.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sails.engine.LocationRegion;

import java.util.List;
import java.util.Map;

import br.cin.ufpe.inesescin.smartparking.R;

/**
 * Created by João Pedro on 02/09/2016.
 */
public class ExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    List<Map<String, String>> groups;
    List<List<Map<String, LocationRegion>>> childs;

    public ExpandableAdapter(Context context, List<Map<String, String>> groups, List<List<Map<String, LocationRegion>>> childs) {
        this.context = context;
        this.groups = groups;
        this.childs = childs;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childs.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childs.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.group, null);
        String text = ((Map<String, String>) getGroup(groupPosition)).get("group");
        TextView tv = (TextView) linearLayout.findViewById(R.id.group_tv);
        tv.setText(text);
        linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        tv.setTextColor(ContextCompat.getColor(context, R.color.wallet_bright_foreground_holo_light));
        return linearLayout;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.child, null);
        LocationRegion lr = ((Map<String, LocationRegion>) getChild(groupPosition, childPosition)).get("child");
        TextView tv = (TextView) linearLayout.findViewById(R.id.child_tv);
        tv.setText(lr.getName());
        ImageView imageView = (ImageView) linearLayout.findViewById(R.id.child_iv);
        imageView.setImageResource(R.drawable.expand_item);
        return linearLayout;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
