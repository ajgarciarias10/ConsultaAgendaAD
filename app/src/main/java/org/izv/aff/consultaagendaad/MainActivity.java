package org.izv.aff.consultaagendaad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.UserDictionary;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.izv.aff.consultaagendaad.settings.SettingsActivity;

public class             MainActivity extends AppCompatActivity {

    private final int CONTACT_PERMISSION = 1;
    private final String TAG = "xyzyx";

    private Button bt_search; // = findViewById(R.id.bt_search);
    private EditText etPhone;
    private TextView tvResult;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG,"onCreate");//verbase
        initialize();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            viewSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"onDestroy");//verbase
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG,"onPause");//verbase
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(TAG,"onRequestPermissionResult");//verbase
        switch (requestCode){
            case CONTACT_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permiso
                    search();
                }else{
                    //sin permiso
                }
                break;
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG,"onRestart");//verbase
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG,"onResumen");//verbase
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG,"onStart");//verbase
    }

    private void initialize() {
        bt_search = findViewById(R.id.bt_search);
        etPhone = findViewById(R.id.etPhone);
        tvResult = findViewById(R.id.tvResult);

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchIfPermitted();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void explain() {
        showRationaleDialog(getString(R.string.title),
                                      getString(R.string.message),
                                      Manifest.permission.READ_CONTACTS,
                                      CONTACT_PERMISSION);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION);
    }

    private void search() {

        //buscar entre los contactos
        //ContentProvider Proveedor de contenidos
        //ContentResolver Consultor de contenidos
        // Queries the user dictionary and returs results
        /*Cursor cursor = getContentResolver().query(
                UserDictionary.Words.CONTENT_URI, // The content URI of the words table
                new String[] {"projection"},      // The columns to returns for each row
                "campo1 = ? and campo2 > ? or campo3= ?",       //  Selection criteria
                new String[] {"pepe","4","23"},  //  Selection criteria
                "campo5,camp3,campo4");          //   The sort order for the returned rows


        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = new String[]{ContactsContract.Contacts.DISPLAY_NAME}/*Sacar Nombre de contacto;
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        seleccion = null;
        argumentos =null;
        String orden = ContactsContract.Contacts.DISPLAY_NAME + "ASC";
        //ordenar por algun tipo de campo
        Cursor cursor =
                getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
       String[] columnas = cursor.getColumnNames();
       for(String s: columnas){
           Log.v(TAG,s);

       }
       String displayName;
       int columna = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
       while(cursor.moveToNext()){
           displayName = cursor.getColumnName(columna);
           Log.v(TAG,displayName);
       }
       */

        Uri uri2 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion2[] = new String[]{ ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME , ContactsContract.CommonDataKinds.Phone.NUMBER };
        String seleccion2 = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";
        String argumentos2[] = new String[]{"1%2%3"};
        String orden2 = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Cursor cursor2 = getContentResolver().query(uri2, proyeccion2, seleccion2, argumentos2, orden2);
        String[] columnas2 = cursor2.getColumnNames();
        for(String s: columnas2) {
            Log.v(TAG, s);
        }
        int columnaNombre = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnaNumero = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String nombre,numero;

        while (cursor2.moveToNext()){
            nombre = cursor2.getString(columnaNombre);
            numero = cursor2.getString(columnaNumero);
            //Log.v(TAG, c)
            tvResult.append("Nombre: " + nombre +
                            "Numero:" +  numero
            );
            for (String c : columnas2){
                int pos = cursor2.getColumnIndex(c);
                String valor = cursor2.getString(pos);
                Log.v(TAG ,pos + " " + c +" " + valor);
            }
        }


    }

    private void searchIfPermitted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // La version de android es posterior a la 6 incluida
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_CONTACTS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Ya tengo el permiso
                search();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                explain(); // 2º ejecucuión
            } else {
                requestPermission(); // 1º ejecucución
            }
        } else {
            // La version de android es anterior a la 6
            // Ya tengo el permiso
            search();
        }
    }

    private void showRationaleDialog(String title, String message, String permission, int requestCode) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Si pulso negativo no quiero hacer nada
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Si pulso positivo quiero pedir los permisos
                        requestPermission();
                    }
                });


        builder.create().show();

    }
private void viewSettings(){
        //intent - intencion
        // intenciones  explicitas o implicitas
        // explica : definir que quiera ir desde el contexto actual a un contexto
        // que se crea la clase SettingsActivity
    Intent intent = new Intent(this, SettingsActivity.class) ;
    startActivity(intent);
}

}