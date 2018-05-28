package develop.elbarberoapptest.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.snowdream.android.widget.SmartImageView;

import java.util.ArrayList;

import develop.elbarberoapptest.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static develop.elbarberoapptest.MainActivity.CONTENT_IMAGES;

/**
 * Created by adma9717 on 28/05/18.
 */

public class CustomMainAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private SmartImageView smartImageView;
    private TextView title, description, price;
    private ArrayList<ItemListMain> itemsArray;

    public CustomMainAdapter(Context applicationContext, ArrayList<ItemListMain> itemsArray) {
        this.context = applicationContext;
        layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.itemsArray = itemsArray;
    }

    @Override
    public int getCount() {
        return itemsArray.size();
    }

    @Override
    public Object getItem(int i) {
        return itemsArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"InflateParams", "ViewHolder", "SetTextI18n"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewGroup vg = (ViewGroup) layoutInflater.inflate(R.layout.item_main_list, null);
        smartImageView = (SmartImageView) vg.findViewById(R.id.iv_item_main_background);
        title = (TextView) vg.findViewById(R.id.txt_item_main_title);
        description = (TextView) vg.findViewById(R.id.txt_item_main_description);
        price = (TextView) vg.findViewById(R.id.txt_item_main_price);

        Rect rect = new Rect(smartImageView.getLeft(), smartImageView.getTop(), smartImageView.getRight(), smartImageView.getBottom());
        String finalUrl = CONTENT_IMAGES + itemsArray.get(i).getImage();
        System.out.println(finalUrl);
        smartImageView.setImageUrl(finalUrl, rect);

        title.setText(itemsArray.get(i).getTitle());
        description.setText(itemsArray.get(i).getDescription());
        price.setText(context.getResources().getString(R.string.item_price).replace("{0}", "" + itemsArray.get(i).getPrice()));

        return vg;
    }
}
