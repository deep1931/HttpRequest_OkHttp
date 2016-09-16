package stem.okhttp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import stem.okhttp.listadapter.DetailAdapter;
import stem.okhttp.model.DetailModel;


public class DetailActivity extends AppCompatActivity {

    private Context context;
    private RecyclerView recyclerView;
    private DetailAdapter mAdapter;

    private ArrayList<DetailModel> alDetail = new ArrayList<>();

    private final String URL = "http://stemtechnocrats.in/training/get-users.php";


    private final String USER_ID = "user_id";
    private final String NAME = "user_name";
    private final String EMAIL = "email_id";
    private final String MOBILE = "mobile";
    private final String PROFILE_PHOTO = "profile_photo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        init();
    }

    private void init() {
        context = this;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new DetailAdapter(this, alDetail);

        //set the layout of list
        mAdapter.setLayout(R.layout.list_items);

        //initialize layout manager as per need, (linear, grid etc)
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        new MakeHttp().execute();

    }

    class MakeHttp extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait..");
            progressDialog.show();


        }

        @Override
        protected Void doInBackground(Void... voids) {

            makeHttp();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog != null)
                progressDialog.dismiss();

        }

        private void makeHttp() {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(URL)
                    .build();

            try {

                Response response = client.newCall(request).execute();

                String res = response.body().string();

                try {
                    JSONArray jsonArray = new JSONArray(res);

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            DetailModel model = new DetailModel();

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            model.setId(jsonObject.getString(USER_ID));
                            model.setName(jsonObject.getString(NAME));
                            model.setEmail(jsonObject.getString(EMAIL));
                            model.setMobile(jsonObject.getString(MOBILE));
                            model.setProfileImageUrl(jsonObject.getString(PROFILE_PHOTO));

                            alDetail.add(model);

                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.setData(alDetail);

                            }
                        });

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {


            case R.id.action_listview:

                initListDisplay();
                break;

            case R.id.action_gridview:

                initGridDisplay();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    // Display a list
    private void initListDisplay() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setLayout(R.layout.list_items);
    }

    // Display the Grid
    private void initGridDisplay() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setLayout(R.layout.items_grid);


    }
}
