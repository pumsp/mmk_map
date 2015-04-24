package com.example.pumsper.mmk;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;


public class MyActivity extends ActionBarActivity implements OnMapReadyCallback{

    public static final String KEY_DRAWABLE_ID = "drawableId";
    private String[] mDrawerTitle ;
    private DrawerLayout mDrawerLayout;
    private ListView mListView;
    private View mView;

    /// map
    private String ip;
    private GoogleMap map;
    private Dialog mDialog;
    private ProgressDialog pDialog;
    private JSONArray dormArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        // web teach seach in listView http://www.androprogrammer.com/2014/02/add-searching-in-list-view.html
        try{
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mListView = (ListView) findViewById(R.id.drawer_list);
            mView =  findViewById(R.id.drawer_linear);



            android.support.v7.app.ActionBar actionBar_dorm = getSupportActionBar();
            actionBar_dorm.setHomeButtonEnabled(true);
            actionBar_dorm.setTitle("หน้าหลัก");
            actionBar_dorm.setDisplayHomeAsUpEnabled(true);
            actionBar_dorm.setDisplayShowCustomEnabled(true);



            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(this);
            ip = "11.0.100.220";
            new Processing().execute();

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String name =(String)parent.getItemAtPosition(position);
                    //Toast.makeText(MyActivity.this,"click "+name ,Toast.LENGTH_SHORT).show();
                    mDrawerLayout.closeDrawer(mView);
                    showDialog(name);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btn_search) {
//            mDrawerLayout.openDrawer(mDrawerLayout);
            if(mDrawerLayout.isDrawerOpen(mView)) {
                mDrawerLayout.closeDrawer(mView);
            }
            else {
                mDrawerLayout.openDrawer(mView);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap gmap) {
        map = gmap;

        double dlat = 13.8702980;
        double dlng = 100.55076899;
        LatLng dpu = new LatLng(dlat,dlng);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(dpu, 15));
        map.getUiSettings().setZoomControlsEnabled(true);
        // tool link to google map app
        map.getUiSettings().setMapToolbarEnabled(false);
        //map.setMyLocationEnabled(true);
        //Toast.makeText(this,"hello Map"+mLastLocation,Toast.LENGTH_SHORT).show();
        //map.setOnMyLocationChangeListener(myLocationChangeListener);

//        map.addMarker(new MarkerOptions()
//                .title("DPU")
//                .snippet("มหาวิทยาลัยธุรกิจบัณฑิตย์")
//                .position(dpu)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                try {
                    if (marker.getSnippet() == null) {
                        map.moveCamera(CameraUpdateFactory.zoomIn());
                    }
                    //
                    showDialog(marker.getTitle());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void showDialog(String title){
        try{
            mDialog = new Dialog(MyActivity.this);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.info_content);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            Button btn_close = (Button) mDialog.findViewById(R.id.info_btn_close);

            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDialog.isShowing()) mDialog.dismiss();
                }
            });
            //String title = marker.getTitle();
            TextView txt_name = (TextView) mDialog.findViewById(R.id.info_txt_name);
            TextView txt_outer = (TextView) mDialog.findViewById(R.id.info_txt_outer);
            TextView txt_comb = (TextView) mDialog.findViewById(R.id.info_txt_comb);
            TextView txt_detail = (TextView) mDialog.findViewById(R.id.info_txt_detail);

            for(int i = 0 ; i < dormArray.length() ; i++){
                JSONObject data = dormArray.getJSONObject(i);
                if(data.getString("nam").equals(title)){
                    txt_name.setText(data.getString("nam"));
                    if(data.getString("outer").equals("1")) txt_outer.setText("หอพักภายใน");
                    else txt_outer.setText("หอพักเครือข่าย");
                    if(data.getString("comb").equals("1"))  txt_comb.setText("หอพักหญิง");
                    else txt_comb.setText("หอพักรวม");

                    txt_detail.setText(data.getString("detail"));

                    String latitude = data.getString("lat");
                    String longitude = data.getString("lon");

                    if(!latitude.equals("-") && !longitude.equals("-")){
                        Double lat = Double.parseDouble(latitude);
                        Double lon = Double.parseDouble(longitude);
                        //move cametra
                    }

                    break;
                }
            }
            //
            //set full screen
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(mDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mDialog.show();
            mDialog.getWindow().setAttributes(lp);


        }catch(Exception e){
            e.printStackTrace();
        }
    }// end showDialog

    /// doInBackground
    class Processing extends AsyncTask<Context, Integer, String> {
        private getData d;
        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar
            pDialog = new ProgressDialog(MyActivity.this);
        }
        @Override
        protected String doInBackground(Context... params) {

            String url = "http://"+ip+"/saoproject/dorm_api.php";
            Log.w("url", "" + url);
            d = new getData(url);

            return "";
        }
        @Override
        protected void onPostExecute(String result)  {
            JSONArray ar = d.jsonArray;
            dormArray = ar;
            mDrawerTitle = new String[ar.length()];
            Log.w("array",""+ar);
            try {
                String nam = ar.getJSONObject(0).getString("nam");
                if(ar.length() <= 1 && nam =="ว่าง"){
                    Toast.makeText(MyActivity.this,"ไม่มีข้อมูลหอพัก",Toast.LENGTH_SHORT).show();
                }
                else{
                    for(int i = 0; i<ar.length();i++ ){

                        JSONObject dorm_arr = ar.getJSONObject(i);
                        nam = dorm_arr.getString("nam");
                        String outer = dorm_arr.getString("outer");
                        String comb = dorm_arr.getString("comb");
                        String detail = dorm_arr.getString("detail");
                        String pic = dorm_arr.getString("pic");
                        String latitude = dorm_arr.getString("lat");
                        String longitude = dorm_arr.getString("lon");
                        if(!latitude.equals("-") && !longitude.equals("-")){
                            Double lat = Double.parseDouble(latitude);
                            Double lon = Double.parseDouble(longitude);

                            LatLng mark = new LatLng(lat,lon);

                            MarkerOptions ma = new MarkerOptions()
                                    .title(nam)
                                    .snippet("รายละเอียด")
                                    .position(mark)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            map.addMarker(ma);
                        }
                        mDrawerTitle[i] = nam;

                        Log.w("name",nam);
                        // Toast.makeText(MapsActivity.this,"name = "+nam,Toast.LENGTH_SHORT).show();

                    }
                    //set drawer list view
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyActivity.this,
                            android.R.layout.simple_list_item_1, mDrawerTitle);

                    mListView.setAdapter(adapter);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            pDialog.dismiss();
            // Dismiss ProgressBar
            // updateWebView(result);
        }
    }//end on background
}//end class

