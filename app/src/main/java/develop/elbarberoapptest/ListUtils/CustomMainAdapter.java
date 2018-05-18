package develop.elbarberoapptest.ListUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import develop.elbarberoapptest.R;

/**
 * Created by labexp on 18/05/18.
 */

public class CustomMainAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ItemListMain> listItems;

    public CustomMainAdapter(Context context, ArrayList<ItemListMain> listItems){
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //instance the custom item in the list
        ItemListMain item = (ItemListMain) getItem(i);

        //inflate the custom appearance item
        view = LayoutInflater.from(context).inflate(R.layout.item_list_main, null);

        //reference the attr of each item
        TextView title = view.findViewById(R.id.txt_item_main_title);
        TextView description = view.findViewById(R.id.txt_item_main_description);
        TextView price = view.findViewById(R.id.txt_item_main_price);
        ImageView imageView = view.findViewById(R.id.iv_item_main_background);

        //put the title, description, price and image for the item
        title.setText(item.getItemName());
        description.setText(item.getItemDescription());
        price.setText(item.getItemPrice());
        imageView.setImageResource(item.getItemImage());

        return view;
    }
}
