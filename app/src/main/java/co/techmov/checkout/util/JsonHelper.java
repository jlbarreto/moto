package co.techmov.checkout.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.techmov.checkout.beans.BasicProductBean;

/**
 * Created by victor on 03-06-15.
 */
public class JsonHelper {

    public static List<BasicProductBean> parseSimpleProducts(org.json.JSONObject data) throws JSONException {
        org.json.JSONArray prods = data.getJSONArray("products");
        List<BasicProductBean> l = new ArrayList<BasicProductBean>();
        for(int i=0; i<prods.length(); i++){
            org.json.JSONObject p = prods.getJSONObject(i);
            String name = p.getString("product");
            String comment = p.getString("comment");
            int q = p.getInt("quantity");
            name = String.format("%dx %s", q, name);
            String conditions = "";
            String ingredients = "";
            JSONArray arr = p.getJSONArray("conditions");
            for(int j=0;j<arr.length();j++)
                conditions = conditions.concat(((org.json.JSONObject)
                        arr.get(j)).getString("condition_option").concat(","));

            arr = p.getJSONArray("ingredients");
            for(int j=0;j<arr.length();j++)
                ingredients = ingredients.concat(((org.json.JSONObject)
                        arr.get(j)).getString("ingredient").concat(","));

            BasicProductBean bean = new BasicProductBean(name,comment);
            if(!conditions.trim().equals(""))
                bean.setConditions("Condiciones:".concat(conditions));
            if(!ingredients.trim().equals(""))
                bean.setIngredients("Ingredientes:".concat(ingredients));
            l.add(bean);
        }
        return l;
    }

}
