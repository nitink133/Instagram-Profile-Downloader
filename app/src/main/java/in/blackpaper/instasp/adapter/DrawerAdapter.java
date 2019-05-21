package in.blackpaper.instasp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.blackpaper.instasp.R;
import in.blackpaper.instasp.data.localpojo.DrawerMenuPojo;
import in.blackpaper.instasp.view.RegularTextView;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ItemViewHolder> {

    private Context context;
    private List<DrawerMenuPojo> items;


    public DrawerAdapter(Context context) {

        this.context = context;
        this.items = new ArrayList<>();

    }

    public void setMenu(List<DrawerMenuPojo> itemsList) {
        items.clear();
        items.addAll(itemsList);
        notifyDataSetChanged();

    }

    public interface EventListener{
        void onItemClick(DrawerMenuPojo item);
    }

    private EventListener eventListener;

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public DrawerMenuPojo getItemData(int position) {
        return items.get(position);
    }


    @Override
    public DrawerAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drawer_menu, parent, false);

        return new DrawerAdapter.ItemViewHolder(itemLayoutView);
    }


    @Override
    public void onBindViewHolder(final DrawerAdapter.ItemViewHolder holder, final int position) {

        DrawerMenuPojo drawerMenuList = items.get(position);
        holder.text.setText(drawerMenuList.getMenuName());
//        Glide.with(context).load(drawerMenuList.getImage()).into(holder.image);
        holder.image.setVisibility(View.GONE);


    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        RegularTextView text;
        ImageView image;


        public ItemViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            image = itemView.findViewById(R.id.image);


        }

    }


}


