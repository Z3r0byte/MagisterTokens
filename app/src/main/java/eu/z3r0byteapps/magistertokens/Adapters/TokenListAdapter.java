package eu.z3r0byteapps.magistertokens.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import eu.z3r0byteapps.magistertokens.Container.Token;
import eu.z3r0byteapps.magistertokens.R;

/**
 * Created by bas on 16-2-17.
 */

public class TokenListAdapter extends ArrayAdapter<Token> {
    private static final String TAG = "TokenListAdapter";

    Context context;
    Token[] tokens;

    public TokenListAdapter(Context context, Token[] tokens) {
        super(context, -1, tokens);
        this.context = context;
        this.tokens = tokens;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_token, parent, false);


        TextView token = (TextView) rowView.findViewById(R.id.token);
        token.setText(tokens[position].getToken());
        TextView tokenId = (TextView) rowView.findViewById(R.id.tokenId);
        tokenId.setText(tokens[position].getId().toString());


        return rowView;
    }
}
