package smugleaf.drinksmenu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DrinkAdapter extends BaseAdapter implements ListAdapter {

    private final Activity activity;
    private ArrayList<DrinkItem> drinks;

    public DrinkAdapter(Activity activity, ArrayList<DrinkItem> drinks) {
        assert activity != null;
        assert drinks != null;

        this.activity = activity;
        this.drinks = drinks;
    }

    @Override
    public int getCount() {
        if (drinks == null)
            return 0;
        else
            return drinks.size();
    }

    @Override
    public DrinkItem getItem(int position) {
        if (drinks == null)
            return null;
        else
            return drinks.get(position);
    }

    @Override
    public long getItemId(int position) {
        DrinkItem drink = getItem(position);
        return (drink.getName() + drink.getBody()).hashCode();
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        if (rowView == null) { // This used to be if () code, no {}, no else{}. But why?
            rowView = activity.getLayoutInflater().inflate(R.layout.drink_item, null);
        }

        SharedPreferences prefs = activity.getSharedPreferences(MenuActivity.PREFERENCES_FILE, activity.MODE_PRIVATE);
        String theme = prefs.getString(MenuActivity.THEME, "default");

        String backgroundColor = drinks.get(position).getBackgroundColor();
        String glassColor = drinks.get(position).getGlassColor();
        String fontColor = drinks.get(position).getFontColor();

        ImageView glassImageView = (ImageView) rowView.findViewById(R.id.glass);
        TextView nameTextView = (TextView) rowView.findViewById(R.id.name);
        TextView bodyTextView = (TextView) rowView.findViewById(R.id.ingredients);
        TextView priceTextView = (TextView) rowView.findViewById(R.id.price);
        RelativeLayout layout = (RelativeLayout) rowView.findViewById(R.id.background);

        int imageId = activity.getResources().getIdentifier(
                drinks.get(position).getGlassware(), "drawable", activity.getPackageName());
        // TODO: Error checking to prevent crashing
        // TODO: Enums or something to find drawables by string name
//        Log.v("IMAGE", "" + imageId);

        if (!drinks.get(position).getGlassware().isEmpty()) {
            try { // TODO: This error handling prevents crashing, but allows funny business
                // Funny business includes: Putting a random image in when a drawable is not found
                // Same for text and font coloring; it just pulls from random successful ones
                Drawable image = ContextCompat.getDrawable(activity, imageId);
                if (colorIsValid(glassColor) && theme.equals("default")) {
                    image.setColorFilter(Color.parseColor("#" + glassColor), PorterDuff.Mode.SRC_ATOP);
                } else if (theme.equals("dark")) {
                    // TODO: Set glass to dark theme if styles.xml can't handle it
                    image.setColorFilter(activity.getResources().getColor(R.color.glassDark), PorterDuff.Mode.SRC_ATOP);
                }
                glassImageView.setImageDrawable(image);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }


//            glassImageView.setImageResource(imageId);
            // TODO: Make work for API <20
            // Something to do with referencing the drawable folder
            // TODO: Color SVG
        }
        if (!drinks.get(position).getName().isEmpty()) {
            nameTextView.setText(drinks.get(position).getName());
            if (colorIsValid(fontColor) && theme.equals(MenuActivity.DEFAULT_THEME)) {
                nameTextView.setTextColor(Color.parseColor("#" + fontColor));
            }
        }
        if (!drinks.get(position).getBody().isEmpty()) {
            bodyTextView.setText(drinks.get(position).getBody().replace("\\n", "\n"));
            // TODO: Find /n and create line break
            if (colorIsValid(fontColor) && theme.equals(MenuActivity.DEFAULT_THEME)) {
                bodyTextView.setTextColor(Color.parseColor("#" + fontColor));
            }
        }
        if (!drinks.get(position).getPrice().isEmpty()) {
            priceTextView.setText(drinks.get(position).getPrice());
            if (colorIsValid(fontColor) && theme.equals(MenuActivity.DEFAULT_THEME)) {
                priceTextView.setTextColor(Color.parseColor("#" + fontColor));
            }
        }
        if (colorIsValid(backgroundColor) && theme.equals(MenuActivity.DEFAULT_THEME)) {
            layout.setBackgroundColor(Color.parseColor("#" + backgroundColor));
            // TODO: Don't ask them to use a #, remove it, then re-add it. Accept all forms.
        } else if (theme.equals(MenuActivity.DARK_THEME)) {
            layout.setBackgroundColor(Color.BLACK);
        } else {
            layout.setBackgroundColor(Color.WHITE);
        }

        return rowView;
    }

    private boolean colorIsValid(String hexColor) {
        if (hexColor.matches("-?[0-9a-fA-F]{6}")) {
            return true;
        }
        return false;
    }
}