package develop.elbarberoapptest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import org.json.JSONObject;;
import java.util.ArrayList;
import java.util.List;

import develop.elbarberoapptest.db.DatabaseModel;
import develop.elbarberoapptest.db.DatabaseServiceHelper;
import develop.elbarberoapptest.utils.CustomMainAdapter;
import develop.elbarberoapptest.utils.GetResponseTask;
import develop.elbarberoapptest.utils.ItemListMain;

import static develop.elbarberoapptest.utils.ServiceXmlFileUtils.getDescriptionFor;
import static develop.elbarberoapptest.utils.ServiceXmlFileUtils.getImageUrlFor;
import static develop.elbarberoapptest.utils.ServiceXmlFileUtils.getPriceFor;
import static develop.elbarberoapptest.utils.ServiceXmlFileUtils.getTitleFor;
import static develop.elbarberoapptest.utils.ServiceXmlFileUtils.parseResponse;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listItemsMain;
    private ArrayList<ItemListMain> itemsArray;

    public static final String SERVICE_DIR = "https://api.github.com/repos/adam3497/el_barbero_project/contents/services?ref=imagenes";
    public static final String SERVICE_CONTENT = "https://raw.githubusercontent.com/adam3497/el_barbero_project/imagenes/services/";
    public static final String IMAGES_DIR = "https://api.github.com/repos/adam3497/el_barbero_project/contents/service_images?ref=imagenes";
    public static final String CONTENT_IMAGES = "https://raw.githubusercontent.com/adam3497/el_barbero_project/imagenes/service_images/";

    private SwipeRefreshLayout refreshLayout;

    private DatabaseServiceHelper serviceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolBar();

        itemsArray = new ArrayList<>();
        downloadServiceFiles();
        serviceHelper = new DatabaseServiceHelper(getApplicationContext());

        listItemsMain = (ListView) findViewById(R.id.lv_main);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_list);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                listItemsMain.setAdapter(null);
                itemsArray = null;
                itemsArray = new ArrayList<>();
                downloadServiceFiles();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void downloadServiceFiles() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
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
        }.execute(SERVICE_DIR);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadServices(List<String> services) {
        // Gets the data repository in write mode
        final SQLiteDatabase db = serviceHelper.getWritableDatabase();

        for(String xmlFile : services){
            String url = SERVICE_CONTENT + xmlFile;
            new GetResponseTask(){
                @Override
                protected void onPostExecute(String response) {
                    String title = getTitleFor(response);
                    String description = getDescriptionFor(response);
                    String price = getPriceFor(response);
                    String imageURL = getImageUrlFor(response);

                    ContentValues values = new ContentValues();
                    values.put(DatabaseModel.ServiceTable.COLUMN_1, title);
                    values.put(DatabaseModel.ServiceTable.COLUMN_2, description);
                    values.put(DatabaseModel.ServiceTable.COLUMN_3, price);
                    values.put(DatabaseModel.ServiceTable.COLUMN_4, imageURL);

                    // Insert the new row, returning the primary key value of the new row
                    long newRowId = db.insert(DatabaseModel.ServiceTable.TABLE_NAME, null, values);

                    setItemsForList(newRowId);

                }
            }.execute(url);
        }
    }

    private void setItemsForList(long newRowId) {
        SQLiteDatabase db = serviceHelper.getReadableDatabase();

        // Columns which we need.
        String[] projection = {
                DatabaseModel.ServiceTable.COLUMN_1,
                DatabaseModel.ServiceTable.COLUMN_2,
                DatabaseModel.ServiceTable.COLUMN_3,
                DatabaseModel.ServiceTable.COLUMN_4,
        };

        // Filter results WHERE "_ID" = 'newRowId'
        String selection = DatabaseModel.ServiceTable._ID + " = ?";
        String[] selectionArgs = {"" + newRowId};

        Cursor cursor = db.query(DatabaseModel.ServiceTable.TABLE_NAME, projection, selection, selectionArgs,
                null, null, null);

        while(cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseModel.ServiceTable.COLUMN_1));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseModel.ServiceTable.COLUMN_2));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseModel.ServiceTable.COLUMN_3));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseModel.ServiceTable.COLUMN_4));

            itemsArray.add(new ItemListMain(title, desc, price, image));
        }

        final TextView empyText = (TextView) findViewById(R.id.empty_list);
        if(itemsArray.size() > 0){
            listItemsMain.setVisibility(View.VISIBLE);
            empyText.setVisibility(View.INVISIBLE);
            listItemsMain.setAdapter(new CustomMainAdapter(getApplicationContext(), itemsArray));
            listItemsMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Snackbar.make(empyText, "Presionó el item " + i, Snackbar.LENGTH_SHORT).show();
                }
            });
        }else{
            listItemsMain.setVisibility(View.INVISIBLE);
            empyText.setVisibility(View.VISIBLE);
        }
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //put black icon menu for the nav view
        Drawable menuIcon = getResources().getDrawable(R.drawable.ic_menu);
        menuIcon.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(menuIcon);

        Drawable menuOverflowIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_menu_overflow);
        menuOverflowIcon.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setOverflowIcon(menuOverflowIcon);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_home:
                Intent inicio = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(inicio);
                break;
            case R.id.nav_products:
                Intent productos = new Intent(getApplicationContext(), ProductsActivity.class);
                startActivity(productos);
                break;
            case R.id.nav_date:
                Intent date = new Intent(getApplicationContext(), ProgramDate.class);
                startActivity(date);
                break;
            case R.id.nav_settings:
                Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.nav_about:
                Intent about = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(about);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.opt_franquicias:
                Toast.makeText(this, "Franquicias se está mostrando", Toast.LENGTH_SHORT).show();
                break;
            case R.id.opt_contact:
                Toast.makeText(this, "Contacto se está mostrando", Toast.LENGTH_SHORT).show();
                break;
            case R.id.opt_about:
                Intent about = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(about);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
