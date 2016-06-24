package smugleaf.drinksmenu;

import org.json.JSONObject;

/**
 * Created by Theseus on 6/2/2016.
 */
interface AsyncResult {
    void onResult(JSONObject object);
}
