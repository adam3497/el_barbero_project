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
import static develop.elbarberoapptest.ProductsActivity.CONTENT_IMAGES_PRODUCTS;

/**
 * Created by adma9717 on 29/05/18.
 */

public class CustomProductAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private SmartImageView smivImageItem, smivBackground;
    private TextView name, price;
    private ArrayList<ItemListProduct> itemsArray;

    private static final String NO_ICON = "ic_no_icon.png";

    public CustomProductAdapter(Context context, ArrayList<ItemListProduct> itemsArray){
        this.context = context;
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
        ViewGroup vg = (ViewGroup) layoutInflater.inflate(R.layout.item_product_list, null);
        smivImageItem = (SmartImageView) vg.findViewById(R.id.iv_item_product_background);
        smivBackground = (SmartImageView) vg.findViewById(R.id.product_background);
        name = (TextView) vg.findViewById(R.id.txt_item_product_name);
        price = (TextView) vg.findViewById(R.id.txt_item_product_price);

        Rect itemImage = new Rect(smivImageItem.getLeft(), smivImageItem.getTop(), smivImageItem.getRight(), smivImageItem.getBottom());
        Rect itemBackground = new Rect(smivBackground.getLeft(), smivBackground.getTop(), smivBackground.getRight(), smivBackground.getBottom());

        String finalUrl;

        if(itemsArray.get(i).getImage().equals("no_icon")){
            finalUrl = CONTENT_IMAGES_PRODUCTS + NO_ICON;
        }
        else{
            finalUrl = CONTENT_IMAGES_PRODUCTS + itemsArray.get(i).getImage();
        }
        smivBackground.setImageUrl(finalUrl, itemBackground);
        smivImageItem.setImageUrl(finalUrl, itemImage);

        name.setText(itemsArray.get(i).getTitle());
        price.setText(context.getResources().getString(R.string.item_price).replace("{0}", "" + itemsArray.get(i).getPrice()));

        return vg;
    }
}
