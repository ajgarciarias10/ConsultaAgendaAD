package org.izv.aff.consultaagendaad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {
    /**
     * Incializamos los permisos de los contactos con un ContactPemision =1
     * Utilizamos el tag  hacer un pequeño debugger de los resultados de cada cosa
     */
    private final int CONTACTS_PERMISSION = 1;
    private final String TAG = "xyzyx";

    private Button btSearch;
    private EditText etPhone;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate");
        initialize();
    }

    /**
     *
     * @param menu
     *
     * Inflamos el menu
     *
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Cuando cerremos la actividad se destruye
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    /**
     *
     * @param item
     * Realiza el metodo ViewSettings
     * cuando le de a ajustes
     *
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_ajustes) {
            viewSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Cuando se Pause la aplicacion que guarde los datos introducidos
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     *
     * Metodo que ve si los permisos estan garantizados o no .
     *
     *
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(TAG, "onRequestPermissionsResult");//verbose
        switch (requestCode) {
            case CONTACTS_PERMISSION:
                if(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permiso
                    search();
                } else {
                    //sin permiso
                }
                break;
        }
    }

    /**
     * Cuando reinicie que guarde datos
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    /**
     * Metodo explain pregunta si quieres que tengamos las SharedPreferences de permisos de contactos
     *
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void explain() {
        showRationaleDialog(getString(R.string.title),
                getString(R.string.message),
                Manifest.permission.READ_CONTACTS,
                CONTACTS_PERMISSION);
    }

    /**
     * Metodo initialize() utilizado para hacer los findViewByid de el :
     *  Boton Search
     *  Del EditText del Telefono
     *  Texview Del resultado de la lista de Telefono
     *
     */
    private void initialize() {
        btSearch = findViewById(R.id.btSearch);
        etPhone = findViewById(R.id.etPhone);
        tvResult = findViewById(R.id.tvResult);

        SharedPreferences preferenciasActividad = getPreferences(Context.MODE_PRIVATE);//Coge las preferencias de la Actividad
        String lastSearch = preferenciasActividad.getString(getString(R.string.last_search), "");//Coge como cadena  la busqueda
        if(!lastSearch.isEmpty()) {
            etPhone.setText(lastSearch);
        }

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchIfPermitted();///METODO BUSCA SI ESTA PERMITIDO
            }
        });
    }

    /**
     * Crea un array de permisos
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS }, CONTACTS_PERMISSION);
    }

    /**
     * Metodo de busqueda
     *
     *
     */

    private void search() {
        String phone = etPhone.getText().toString();

        tvResult.setText("");

        SharedPreferences preferenciasActividad = getPreferences(Context.MODE_PRIVATE);//Coge las preferencias de la actividad
        SharedPreferences.Editor editor = preferenciasActividad.edit();
        editor.putString(getString(R.string.last_search), phone);//Saca la ultima busqueda y su telefono
        editor.commit();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this );
        String email = sharedPreferences.getString(getString(R.string.settings_email), getString(R.string.no_email));
        tvResult.append(email + "\n");

        phone = searchFormat(phone);
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        String seleccion = ContactsContract.CommonDataKinds.Phone.NUMBER + " like ?";
        String argumentos[] = new String[]{ phone };//etPhone.getText().toString()};
        String orden = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        String[] columnas = cursor.getColumnNames();
        int columnaNombre = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String nombre, numero;
        while(cursor.moveToNext()) {
            nombre = cursor.getString(columnaNombre);
            numero = cursor.getString(columnaNumero);
            for(String s: columnas) {
                int pos = cursor.getColumnIndex(s);
                String valor = cursor.getString(pos);
                tvResult.append(s + " " + valor + "\n");
            }
        }
    }

    private String searchFormat(String phone) {
        String newString = "";
        for (char ch: phone.toCharArray()) {
            newString += ch + "%";
        }
        return newString;
    }

    /**
     * Buscar si esta permitido en las SharedPreferences
     */
    private void searchIfPermitted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //La version de android es posterior a la 6 incluida
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_CONTACTS) ==
                    PackageManager.PERMISSION_GRANTED) {
                //Ya tengo el permiso
                search();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                explain(); //2ª ejecución
            } else {
                requestPermission(); //1ª ejecución
            }
        } else {
            //La version de android es anterior a la 6
            //Ya tengo el permiso
            search();
        }
    }

    /**
     * A
     * @param title
     * @param message
     * @param permission
     * @param requestCode
     *
     *
     * AlerDialogBuilder con el si y el no en dar las preferencias
     */
    private void showRationaleDialog (String title, String message, String permission, int requestCode) {
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nada
                    }
                })
                .setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission();
                    }
                });
        builder.create().show();
    }

    private void viewSettings() {
        //intent - intención
        //intenciones explícitas, o implícitas
        //explícita: definir que quiero ir desde el contaxto actual a un contexto
        //que se crea con la clase SettingsActivity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}

//buscar entre los contactos
//ContentProvider Proveedor de contenidos
//ContentResolver Consultor de contenidos
// Queries the user dictionary and returns results
//url: https://ieszaidinvergeles.org/carpeta/carpeta2/pagina.html?dato=1
//uri: protocolo://dirección/ruta/recurso
        /*Cursor cursor = getContentResolver().query(
                UserDictionary.Words.CONTENT_URI,
                new String[] {"projection"},
                "campo1 = ? and campo2 > ? or campo3 = ? ",
                new String[] {"pepe", "4", "23"},
                "campo5, camp3, campo4");*/
        /*Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        //seleccion = null;
        //argumentos = null;
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        String[] columnas = cursor.getColumnNames();
        for(String s: columnas) {
            Log.v(TAG, s);
        }
        String displayName;
        int columna = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        while(cursor.moveToNext()) {
            displayName = cursor.getString(columna);
            Log.v(TAG, displayName);
        }*/