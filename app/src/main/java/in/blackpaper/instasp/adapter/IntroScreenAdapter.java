package in.blackpaper.instasp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.viewpager.widget.PagerAdapter;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.data.localpojo.IntroScreenList;
import in.blackpaper.instasp.view.RegularTextView;

public class IntroScreenAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    List<IntroScreenList> screenLists;

    public IntroScreenAdapter(Context context, List<IntroScreenList> items) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        screenLists = items;

    }

    @Override
    public int getCount() {
        return screenLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item_row_screen, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
        RegularTextView title = itemView.findViewById(R.id.tv_title);
        RegularTextView  subTitle = itemView.findViewById(R.id.tv_info);
        title.setText(screenLists.get(position).getTitle());
        subTitle.setText(screenLists.get(position).getInfo());
        imageView.setImageResource(screenLists.get(position).getImage());
        container.addView(itemView);
        return itemView;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}


