package de.bre.mits.mitsapp.ingredientsList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpStatus;

import de.bre.mits.mitsapp.R;
import de.bre.mits.mitsapp.util.Helpermethods;
import de.bre.mits.mitsapp.util.RestConnector;
import de.bre.mits.mitsapp.util.Settings;

public class CardsAdapter extends ArrayAdapter<CardModel> {
    private enum refreshType {
        INCREASE, DECREASE
    }

    public CardsAdapter(Context context) {
        super(context, R.layout.card_item);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.card_item, parent, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CardModel model = getItem(position);

        holder.id.setText(model.getId());
        holder.title.setText(model.getTitle());
        holder.crit.setText(model.getCritAmount());
        holder.ing.setText(model.getIngAmount());
        holder.quantityUnit.setText(model.getQuantityUnit());

        setOnClickListener(holder, model);

        if (Helpermethods.getpref().getString("username", "").equals("chef")) {
            holder.crit.setVisibility(View.VISIBLE);
            holder.critMinusBtn.setVisibility(View.VISIBLE);
            holder.critPlusBtn.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private void setOnClickListener(final ViewHolder holder, final CardModel model) {
        holder.critPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (increaseCritAmount(model.getId()).equals(HttpStatus.OK)) {
                    refreshUI(holder.crit, refreshType.INCREASE);
                } else {
                    Toast.makeText(getContext(), R.string.error_communicating_w_server, Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.critMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (decreaseCritAmount(model.getId()).equals(HttpStatus.OK)) {
                    refreshUI(holder.crit, refreshType.DECREASE);
                } else {
                    Toast.makeText(getContext(), R.string.error_communicating_w_server, Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.amountPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (increaseAmount(model.getId()).equals(HttpStatus.OK)) {
                    refreshUI(holder.ing, refreshType.INCREASE);
                } else {
                    Toast.makeText(getContext(), R.string.error_communicating_w_server, Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.amountMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (decreaseAmount(model.getId()).equals(HttpStatus.OK)) {
                    refreshUI(holder.ing, refreshType.DECREASE);
                } else {
                    Toast.makeText(getContext(), R.string.error_communicating_w_server, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    static class ViewHolder {
        TextView id;
        TextView title;
        TextView crit;
        TextView ing;
        TextView quantityUnit;
        Button critPlusBtn;
        Button critMinusBtn;
        Button amountPlusBtn;
        Button amountMinusBtn;

        ViewHolder(View view) {
            id = view.findViewById(R.id.idTextView);
            title = view.findViewById(R.id.ingNameTextView);
            crit = view.findViewById(R.id.critAmountTextView);
            ing = view.findViewById(R.id.ingAmountTextView);
            quantityUnit = view.findViewById(R.id.quantityUnitTextView);
            critPlusBtn = view.findViewById(R.id.critPlusButton);
            critMinusBtn = view.findViewById(R.id.critMinusButton);
            amountPlusBtn = view.findViewById(R.id.ingPlusButton);
            amountMinusBtn = view.findViewById(R.id.ingMinusButton);
        }
    }

    /**
     * called if ingredient critical amount should be increased
     * @param id indicator which critical ingredient amount id (same ingredient id in rest service) should be increased
     * @return status from rest service
     */
    private static HttpStatus increaseCritAmount(String id) {
        int idInt = Integer.parseInt(id);
        return RestConnector.putRessource(Settings.RESOURCE_STOCK_MODIFICATION_AND_INFO + idInt +
                Settings.PARAM_MANIPULATE_STOCK + "0" + Settings.PARAM_MANIPULATE_CRIT_STOCK + "1");
    }

    /**
     * called if ingredient critical amount should be decreased
     * @param id indicator which critical ingredient amount id (same ingredient id in rest service) should be decreased
     * @return status from rest service
     */
    private static HttpStatus decreaseCritAmount(String id) {
        int idInt = Integer.parseInt(id);
        return RestConnector.putRessource(Settings.RESOURCE_STOCK_MODIFICATION_AND_INFO + idInt +
                Settings.PARAM_MANIPULATE_STOCK + "0" + Settings.PARAM_MANIPULATE_CRIT_STOCK + "-1");
    }

    /**
     * called if ingredient amount should be increased
     * @param id indicator which ingredient id (same ingredient id in rest service) should be increased
     * @return status from rest service
     */
    private static HttpStatus increaseAmount(String id) {
        int idInt = Integer.parseInt(id);
        return RestConnector.putRessource(Settings.RESOURCE_STOCK_MODIFICATION_AND_INFO + idInt +
                Settings.PARAM_MANIPULATE_STOCK + "1" + Settings.PARAM_MANIPULATE_CRIT_STOCK + "0");
    }

    /**
     * called if ingredient amount should be decreased
     * @param id indicator which ingredient id (same ingredient id in rest service) should be decreased
     * @return status from rest service
     */
    private static HttpStatus decreaseAmount(String id) {
        int idInt = Integer.parseInt(id);
        return RestConnector.putRessource(Settings.RESOURCE_STOCK_MODIFICATION_AND_INFO + idInt +
                Settings.PARAM_MANIPULATE_STOCK + "-1" + Settings.PARAM_MANIPULATE_CRIT_STOCK + "0");
    }

    /**
     * increase or decrease text view string depending on refreshType
     * @param textView reference to text view
     * @param refreshType indicator if function should increase or decrease
     */
    private void refreshUI(TextView textView, refreshType refreshType) {
        String critValStr = textView.getText().toString();
        Double critValDouble = 0.0;

        if (refreshType.equals(CardsAdapter.refreshType.INCREASE)) {
            critValDouble = Double.parseDouble(critValStr) + 1.0;
        } else if (refreshType.equals(CardsAdapter.refreshType.DECREASE)) {
            critValDouble = Double.parseDouble(critValStr) - 1.0;
        }

        String newCritValStr = critValDouble.toString();
        textView.setText(newCritValStr);
    }
}
