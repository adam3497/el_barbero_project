package develop.elbarberoapptest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.snowdream.android.widget.SmartImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import develop.elbarberoapptest.utils.GetResponseTask;

public class ProductsActivity extends AppCompatActivity {

    private ListView lvItemProducts;
    private ArrayList<String> itemsName;
    private ArrayList<String> itemsPrice;
    private ArrayList<String> itemsBackground;

    public static final String PRODUCT_IMAGES_DIR = "https://api.github.com/repos/adam3497/el_barbero_project/contents/imagenes?ref=imagenes";
    public static final String CONTENT_IMAGES_PRODUCTS = "https://raw.githubusercontent.com/adam3497/el_barbero_project/imagenes/imagenes/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_activity_products));
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_up_arrow);
        upArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        lvItemProducts = (ListView) findViewById(R.id.lv_products);
        itemsName = new ArrayList<>();
        itemsPrice = new ArrayList<>();
        itemsBackground = new ArrayList<>();

        descargarImagenes();


    }

    @SuppressLint("StaticFieldLeak")
    private void descargarImagenes() {
        itemsBackground.clear();
        itemsPrice.clear();
        itemsName.clear();

        final ProgressDialog progressDialog = new ProgressDialog(ProductsActivity.this);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        new GetResponseTask(){
            @Override
            protected void onPostExecute(String response) {
                if(response == null){
                    progressDialog.dismiss();
                    Toast.makeText(ProductsActivity.this, "No se pudo conectar, hay un problema con el servidor", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.dismiss();
                    setItemsForList(parseResponse(response));
                }
            }
        }.execute(PRODUCT_IMAGES_DIR);
    }

    private void setItemsForList(List<String> response) {
        itemsBackground.addAll(response);

        final TextView emptyList = (TextView) findViewById(R.id.empty_list_products);
        if(itemsBackground.size()>0){
            lvItemProducts.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.INVISIBLE);
            lvItemProducts.setAdapter(new CustomProductsAdapter(getApplicationContext()));
            lvItemProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Snackbar.make(emptyList, "Presionó el producto " + i, Snackbar.LENGTH_SHORT).show();
                }
            });
        }
        else{
            lvItemProducts.setVisibility(View.INVISIBLE);
            emptyList.setVisibility(View.VISIBLE);
        }
    }

    /**
     * parse the string (representation of a json) to get only the values associated with
     * key "name", which are the file names of the folder requested before.
     */
    private List<String> parseResponse(String response) {
        List<String> options = new ArrayList<String>();
        try {
            // create JSON Object
            JSONArray jsonArray = new JSONArray(response);
            for (int i= 0; i < jsonArray.length(); i++) {
                // create json object for every element of the array
                JSONObject object = jsonArray.getJSONObject(i);
                // get the value associated with
                options.add( object.getString("name") );
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return options;
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        return true;
    }

    private class CustomProductsAdapter extends BaseAdapter{
        private Context context;
        private LayoutInflater layoutInflater;
        private SmartImageView smivImageItem, smivBackground;
        private TextView name, price;

        public CustomProductsAdapter(Context applicationContext) {
            this.context = applicationContext;
            layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return itemsBackground.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint({"InflateParams", "ViewHolder"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewGroup vg = (ViewGroup) layoutInflater.inflate(R.layout.item_product_list, null);
            smivImageItem = (SmartImageView) vg.findViewById(R.id.iv_item_product_background);
            smivBackground = (SmartImageView) vg.findViewById(R.id.product_background);
            name = (TextView) vg.findViewById(R.id.txt_item_product_name);
            price = (TextView) vg.findViewById(R.id.txt_item_product_price);

            Rect itemImage = new Rect(smivImageItem.getLeft(), smivImageItem.getTop(), smivImageItem.getRight(), smivImageItem.getBottom());
            Rect itemBackground = new Rect(smivBackground.getLeft(), smivBackground.getTop(), smivBackground.getRight(), smivBackground.getBottom());

            String finalUrl = CONTENT_IMAGES_PRODUCTS + itemsBackground.get(i);
            smivBackground.setImageUrl(finalUrl, itemBackground);
            smivImageItem.setImageUrl(finalUrl, itemImage);

            name.setText("Producto " + i);
            price.setText("Precio " + i);

            return vg;

        }
    }
}
