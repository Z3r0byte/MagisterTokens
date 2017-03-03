package eu.z3r0byteapps.magistertokens.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import eu.z3r0byteapps.magistertokens.Container.List;
import eu.z3r0byteapps.magistertokens.R;

/**
 * Created by bas on 1-3-17.
 */

public class ListAdapter extends ArrayAdapter<List> {
    private static final String TAG = "ListAdapter";

    Context context;
    List[] lists;

    public ListAdapter(Context context, List[] lists) {
        super(context, -1, lists);
        this.context = context;
        this.lists = lists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_list, parent, false);


        TextView token = (TextView) rowView.findViewById(R.id.listName);
        token.setText(lists[position].getName());
        TextView tokenId = (TextView) rowView.findViewById(R.id.tokenAmount);
        tokenId.setText(String.format(context.getString(R.string.msg_token_amount_formattable),
                lists[position].getAmountOfTokens()));

        ImageView preferred = (ImageView) rowView.findViewById(R.id.preferred);
        if (lists[position].isPreferred()) {
            IconicsDrawable drawable = new IconicsDrawable(context, GoogleMaterial.Icon.gmd_star)
                    .color(context.getResources().getColor(R.color.primary)).sizeDp(25);
            preferred.setImageDrawable(drawable);
        } else {
            IconicsDrawable drawable = new IconicsDrawable(context, GoogleMaterial.Icon.gmd_star_border)
                    .sizeDp(25);
            preferred.setImageDrawable(drawable);
        }

        return rowView;
    }
}
