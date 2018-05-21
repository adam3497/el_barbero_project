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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import develop.elbarberoapptest.utils.GetResponseTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listItemsMain;
    private ArrayList<String> imagenes;
    private ArrayList<String> titulo;
    private ArrayList<String> precio;
    private ArrayList<String> descripcion;

    private static final String IMAGES_DIR = "https://api.github.com/repos/adam3497/el_barbero_project/contents/imagenes?ref=imagenes";
    private static final String CONTENT_IMAGES = "https://raw.githubusercontent.com/adam3497/el_barbero_project/imagenes/imagenes/";

    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolBar();

        imagenes = new ArrayList<>();
        titulo = new ArrayList<>();
        precio = new ArrayList<>();
        descripcion = new ArrayList<>();

        listItemsMain = (ListView) findViewById(R.id.lv_main);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_list);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                refreshLayout.setRefreshing(false);
                startActivity(intent);
            }
        });
        descargarImagen();
        
    }

    @SuppressLint("StaticFieldLeak")
    private void descargarImagen() {
        titulo.clear();
        precio.clear();
        descripcion.clear();
        imagenes.clear();

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        new GetResponseTask(){
            @Override
            protected void onPostExecute(String response) {
                if(response == null){
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "No se pudo conectar, hay un problema con el servidor", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.dismiss();
                    setItemsForList(parseResponse(response));
                }
            }
        }.execute(IMAGES_DIR);
    }

    private void setItemsForList(List<String> response) {
        imagenes.addAll(response);

        final TextView empyText = (TextView) findViewById(R.id.empty_list);
        if(imagenes.size() > 0){
            listItemsMain.setVisibility(View.VISIBLE);
            empyText.setVisibility(View.INVISIBLE);
            listItemsMain.setAdapter(new CustomMainAdapter(getApplicationContext()));
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
                break;
            case R.id.nav_settings:
                Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.nav_about:
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
                Toast.makeText(this, "About se está mostrando", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class CustomMainAdapter extends BaseAdapter{

        private Context context;
        private LayoutInflater layoutInflater;
        private SmartImageView smartImageView;
        private TextView title, description, price;

        public CustomMainAdapter(Context applicationContext) {
            this.context = applicationContext;
            layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imagenes.size();
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
            ViewGroup vg = (ViewGroup) layoutInflater.inflate(R.layout.item_main_list, null);
            smartImageView = (SmartImageView) vg.findViewById(R.id.iv_item_main_background);
            title = (TextView) vg.findViewById(R.id.txt_item_main_title);
            description = (TextView) vg.findViewById(R.id.txt_item_main_description);
            price = (TextView) vg.findViewById(R.id.txt_item_main_price);

            Rect rect = new Rect(smartImageView.getLeft(), smartImageView.getTop(), smartImageView.getRight(), smartImageView.getBottom());
            String finalUrl = CONTENT_IMAGES + imagenes.get(i);
            System.out.println(finalUrl);
            smartImageView.setImageUrl(finalUrl, rect);

            title.setText("Título prueba " + i);
            description.setText("Descripción prueba " + i);
            price.setText("Precio prueba " + i);

            return vg;
        }
    }
}
