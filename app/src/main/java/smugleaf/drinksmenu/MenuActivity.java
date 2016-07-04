package smugleaf.drinksmenu;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

// TODO: Create commercial version, work with PINNING, use pin to keep app from changing, fullscreen
// Create options menu to switch between commercial and personal
// NFC
// More silhouettes
// Big + button when empty? --got it, but, not right icon?
// Divider color? -- meh
// Theeeeeeeeeeeeeemes -- popup menu thingy
// Toolbar color? Dunno wtf to do with this
// Theme popupBackground to not be white on dark-theme
// Maybe change app topbar for non-dark theme? Default seems like it should be a different color or something....
// Save for offline
// Delete filler data of drink_item
// Fancy spinner refresh animation
// Fancy ( + ) button with shadow

public class MenuActivity extends AppCompatActivity {

    public static final String PREFERENCES_FILE = "preferences";
    public static final String THEME = "theme";
    public static final String DEFAULT_THEME = "default";
    public static final String LIGHT_THEME = "light";
    public static final String DARK_THEME = "dark";
    private static final String SHEET_URL = "sheet";
    private static final String JSON_STRING = "json";

    private String sheet = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SharedPreferences.Editor editor = this.getSharedPreferences(PREFERENCES_FILE, this.MODE_PRIVATE).edit();
//        editor.putString(THEME, DEFAULT_THEME);
//        editor.commit();

        SharedPreferences prefs = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
        String sheetUrl = prefs.getString(SHEET_URL, "");
        System.out.print(String.format("Sheet: ", sheetUrl));
        String theme = prefs.getString(THEME, DEFAULT_THEME);

        if (theme.equals(LIGHT_THEME)) {
            setTheme(R.style.LightTheme);
        } else if (theme.equals(DARK_THEME)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.activity_menu);
        // TODO: Refreshing the theme doesn't fix the title bar

        Button importButton = (Button) findViewById(R.id.import_button);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"FAB button clicked",Toast.LENGTH_LONG).show();
//            }
//        });

        if (sheetUrl.isEmpty()) {
            importButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createImportAlert();
                }
            });
        } else {
            sheet = sheetUrl;
            String jsonString = prefs.getString(JSON_STRING, "");

            try {
                processJson(new JSONObject(jsonString));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            refresh();
            importButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.open, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import:
                createImportAlert();
                return true;
            case R.id.action_theme:
                setTheme();
                return true;
            case R.id.action_mode:
                Toast.makeText(this, "Mode", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createImportAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Paste the shareable link for your Google Sheet:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alert.setView(input);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sheet = input.getText().toString();
                sheet = parseUrl(sheet);
                loadMenu(sheet);
            }
        });

        alert.create();
        alert.show();
    }

    private void setTheme() {
        // TODO: Make this ask for a theme, set it, refresh, and then save

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.themes);

        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();

                SharedPreferences.Editor editor = MenuActivity.this.getSharedPreferences(
                        PREFERENCES_FILE, MenuActivity.this.MODE_PRIVATE).edit();

                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {

                        if (btn.getText().toString().equals("Default Theme")) {
                            editor.putString(THEME, DEFAULT_THEME);
                            editor.commit();
                            refresh();
                            dialog.dismiss();
                        } else if (btn.getText().toString().equals("Classic Theme")) {
                            editor.putString(THEME, LIGHT_THEME);
                            editor.commit();
                            refresh();
                            dialog.dismiss();
                        } else if (btn.getText().toString().equals("Dark Theme")) {
                            editor.putString(THEME, DARK_THEME);
                            editor.commit();
                            refresh();
                            dialog.dismiss();
                        }
                    }
                }
            }
        });

        dialog.show();

    }

    private void refresh() {
        if (!sheet.equals("")) {
            loadMenu(sheet);
        }
    }

    private void createRecipeAlert(String recipe) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(recipe)
                .show();
    }

    private String parseUrl(String link) {
        try {
//            URL url = new URL("https://docs.google.com/spreadsheets/d/1D-QT0LivQJcsqnK4NJe9FwMRvbYmXXCOMJb4myYObtE/edit?usp=sharing");
//          URL url = new URL("https://docs.google.com/spreadsheets/d/1-EzK8yVDNskKmTKV_ycg2q2Xn2z86gp6QOwevwp1k94/edit?usp=sharing");
//          URL url = new URL(link);
            URL url = new URL("https://docs.google.com/spreadsheets/d/12pe7__L5dADvUi3qcSsvPVlt5QYwEHU5pMCahmgd6I0/edit?usp=sharing");

            link = url.getPath();
            link = link.replace("/spreadsheets/d/", "");
            link = link.replace("/edit", ""); // TODO: This can... probably be handled better
            // TODO: Include validation and error messages for user so they know why they suck
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "https://spreadsheets.google.com/tq?key=" + link;
    }

    protected void loadMenu(String link) {
        // TODO: Loading icon START
        new DownloadSheet(new AsyncResult() {
            @Override
            public void onResult(JSONObject object) {
                processJson(object);
//                System.out.print(object);
            }
        }).execute(link);//https://docs.google.com/spreadsheets/d/1D-QT0LivQJcsqnK4NJe9FwMRvbYmXXCOMJb4myYObtE/edit?usp=sharing
//        }).execute("https://spreadsheets.google.com/tq?key=1D-QT0LivQJcsqnK4NJe9FwMRvbYmXXCOMJb4myYObtE");
    }

    private void processJson(JSONObject object) {

        try {
            JSONArray rows = object.getJSONArray("rows");
            ArrayList<DrinkItem> drinks = new ArrayList<>();
            final ArrayList<String> recipes = new ArrayList<>();

            // TODO: What the fuck is going on. First sheet loads fine with r = 0
            // TODO: Second sheet crashes because of glassware and must use r = 1

            for (int r = 0; r < rows.length(); ++r) {
                JSONObject row = rows.getJSONObject(r);
                JSONArray columns = row.getJSONArray("c");

                if (!getString(columns, 0).equals("glassware")) {

                    drinks.add(new DrinkItem(getString(columns, 0),
                            getString(columns, 1),
                            getString(columns, 2),
                            getString(columns, 3),
                            getString(columns, 5),
                            getString(columns, 6),
                            getString(columns, 7)));

                    recipes.add(getString(columns, 4));
                }
            }

            DrinkAdapter adapter = new DrinkAdapter(this, drinks);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int position, long id) {
                    if (!recipes.get(position).isEmpty()) {
                        createRecipeAlert(recipes.get(position));
                    }
                    return true;
                }
            });

            setBackgroundColor();

            if (drinks.size() > 0) {
                saveSheet(object);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // TODO: Loading icon STOP
    }

    private void setBackgroundColor(){
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relative_layout);
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
        String theme = prefs.getString(THEME, DEFAULT_THEME);
        if (theme.equals(DARK_THEME)) {
            layout.setBackgroundColor(Color.BLACK);
        } else {
            layout.setBackgroundColor(Color.WHITE);
        }
    }

    private void saveSheet(JSONObject object) {
        Button importButton = (Button) findViewById(R.id.import_button);
        importButton.setVisibility(View.GONE);
        String jsonString = object.toString();
        SharedPreferences.Editor editor = this.getSharedPreferences(PREFERENCES_FILE, this.MODE_PRIVATE).edit();
        editor.putString(SHEET_URL, sheet);
        editor.putString(JSON_STRING, jsonString);
        editor.commit();
    }

    private String getString(JSONArray columns, int position) {
        try {
            if (columns.get(position).equals(null)) {
                return "";
            } else {
                return columns.getJSONObject(position).getString("v");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}