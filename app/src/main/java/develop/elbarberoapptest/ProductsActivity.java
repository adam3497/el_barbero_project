package develop.elbarberoapptest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import develop.elbarberoapptest.db.DatabaseModel;
import develop.elbarberoapptest.db.DatabaseProductHelper;
import develop.elbarberoapptest.utils.CustomProductAdapter;
import develop.elbarberoapptest.utils.GetResponseTask;
import develop.elbarberoapptest.utils.ItemListProduct;

import static develop.elbarberoapptest.utils.ServiceXmlFileUtils.getImageUrlFor;
import static develop.elbarberoapptest.utils.ServiceXmlFileUtils.getPriceFor;
import static develop.elbarberoapptest.utils.ServiceXmlFileUtils.getTitleFor;
import static develop.elbarberoapptest.utils.ServiceXmlFileUtils.parseResponse;

public class ProductsActivity extends AppCompatActivity {

    private ListView lvItemProducts;
    private ArrayList<ItemListProduct> itemsArray;

    public static final String PRODUCT_DIR = "https://api.github.com/repos/adam3497/el_barbero_project/contents/products?ref=imagenes";
    public static final String PRODUCT_CONTENT = "https://raw.githubusercontent.com/adam3497/el_barbero_project/imagenes/products/";
    public static final String PRODUCT_IMAGES_DIR = "https://api.github.com/repos/adam3497/el_barbero_project/contents/product_images?ref=imagenes";
    public static final String CONTENT_IMAGES_PRODUCTS = "https://raw.githubusercontent.com/adam3497/el_barbero_project/imagenes/product_images/";

    private DatabaseProductHelper productHelper;

    private SwipeRefreshLayout refreshLayout;

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

        itemsArray = new ArrayList<>();
        productHelper = new DatabaseProductHelper(getApplicationContext());

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout_products);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                lvItemProducts.setAdapter(null);
                itemsArray = null;
                itemsArray = new ArrayList<>();
                downloadProductFiles();
            }
        });

        downloadProductFiles();
    }

    @SuppressLint("StaticFieldLeak")
    private void downloadProductFiles() {
        final ProgressDialog progressDialog = new ProgressDialog(ProductsActivity.this);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        new GetResponseTask(){
            @Override
            protected void onPostExecute(String response) {
                if(response == null){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "No se pudo conectar, hay un problema con el servidor", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.dismiss();
                    loadServices(parseResponse(response));
                }
            }
        }.execute(PRODUCT_DIR);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadServices(List<String> products) {
        final SQLiteDatabase db = productHelper.getWritableDatabase();

        for(String xmlFile : products){
            String url = PRODUCT_CONTENT + xmlFile;

            new GetResponseTask(){
                @Override
                protected void onPostExecute(String response) {
                    String title = getTitleFor(response);
                    String price = getPriceFor(response);
                    String imageURL = getImageUrlFor(response);

                    ContentValues values = new ContentValues();
                    values.put(DatabaseModel.ProductTable.COLUMN_1, title);
                    values.put(DatabaseModel.ProductTable.COLUMN_2, price);
                    values.put(DatabaseModel.ProductTable.COLUMN_3, imageURL);

                    long newRowId = db.insert(DatabaseModel.ProductTable.TABLE_NAME, null, values);

                    setItemsForList(newRowId);
                }
            }.execute(url);
        }

    }

    private void setItemsForList(long newRowId) {
        SQLiteDatabase db = productHelper.getReadableDatabase();

        //columns which we need
        String[] projection = {
                DatabaseModel.ProductTable.COLUMN_1,
                DatabaseModel.ProductTable.COLUMN_2,
                DatabaseModel.ProductTable.COLUMN_3
        };

        // Filter results WHERE "_ID" = 'newRowId'
        String selection = DatabaseModel.ProductTable._ID + " = ?";
        String[] selectionArgs = {"" + newRowId};

        Cursor cursor = db.query(DatabaseModel.ProductTable.TABLE_NAME, projection, selection, selectionArgs,
                null, null, null);

        while(cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseModel.ProductTable.COLUMN_1));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseModel.ProductTable.COLUMN_2));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseModel.ProductTable.COLUMN_3));

            itemsArray.add(new ItemListProduct(title, price, image));
        }

        final TextView emptyList = (TextView) findViewById(R.id.empty_list_products);
        if(itemsArray.size()>0){
            lvItemProducts.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.INVISIBLE);
            lvItemProducts.setAdapter(new CustomProductAdapter(getApplicationContext(), itemsArray));
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

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        return true;
    }
}
