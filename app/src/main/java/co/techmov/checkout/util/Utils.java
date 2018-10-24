package co.techmov.checkout.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.app.AlertDialog;
import android.util.Log;

import com.appboy.Appboy;
import com.appboy.models.outgoing.AppboyProperties;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import co.techmov.checkout.R;
import co.techmov.checkout.entity.ShowView;
import io.realm.Realm;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by victor on 08-26-15.
 */
public class Utils {

    public static Utils getInstance(){
        return new Utils();
    }

    public static Realm getRealmInstance(Context ctx){
        Realm realm;
        try {
            realm = Realm.getInstance(ctx);
        }catch (RealmMigrationNeededException e){
            Log.d("REALM-EXCEP", "Classes of fields doest no match.. Reparing the classes...");
            Realm.deleteRealmFile(ctx);
            realm = Realm.getInstance(ctx);
            Log.d("REALM", "Realm is OK");
        }
        return realm;
    }

    public Float toFloat(Object o){
        return parseToFloat(o);
    }

    public static Float parseToFloat(Object o){
        String str = evaluateStr(o.toString());
        return parseToFloat(str);
    }

    public static Float parseToFloat(String strFloat){
        if(strFloat.equals("null")){
            return ((float) 0.0);
        }else{
            return Float.valueOf(strFloat);
        }
    }

    public static String evaluateStr(String str){
        if(str.contains("$")){
            str = str.replace("$","");
        }
        return str;
    }

    public static ShowView findInList(List<ShowView> showViews, int orderid){
        ShowView showView = null;
        for(ShowView s: showViews){
            if(s.getOrderId() == orderid)
                showView = s;
        }
        return showView;
    }

    public static String decimalFormatString(float number){
        Locale locale  = new Locale("en", "UK");
        String pattern = "###0.00";
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        decimalFormat.applyPattern(pattern);
        return decimalFormat.format(number);
    }

    public static Boolean checkGPSNetworkServices(final Context context, Appboy instance) {
        try {
            boolean gpsEnabled;
            boolean networkEnabled;

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!gpsEnabled && !networkEnabled) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(context.getResources().getString(R.string.no_gps_title));
                dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
                dialog.setPositiveButton(context.getResources().getString(R.string.gps_network_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton(context.getString(R.string.ignore), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();

                appboyEvent(instance, context.getString(R.string.appboy_no_gps));

                checkInternetConnection(context, instance);

                return false;

            } else {
                return checkInternetConnection(context, instance);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static void appboyEvent(Appboy appboyInstance, String eventName){
        AppboyProperties properties = new AppboyProperties();
        appboyInstance.logCustomEvent(eventName, properties);
    }

    public static boolean checkInternetConnection(final Context context, Appboy instance){
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo == null) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(context.getResources().getString(R.string.no_internet_title));
                dialog.setMessage(context.getResources().getString(R.string.no_internet_available));
                dialog.setPositiveButton(context.getResources().getString(R.string.gps_network_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                        context.startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton(context.getString(R.string.ignore), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();

                appboyEvent(instance, context.getString(R.string.appboy_no_internet));

                return false;
            }

            return netInfo.isConnected();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
