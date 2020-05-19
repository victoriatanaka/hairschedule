package com.example.cronogramacapilar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cronogramacapilar.activities.EditTreatmentActivity;
import com.example.cronogramacapilar.activities.MainActivity;
import com.example.cronogramacapilar.activities.TreatmentActivity;
import com.example.cronogramacapilar.helpers.TreatmentDaoAsync;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TreatmentsAdapter extends RecyclerView.Adapter<TreatmentsAdapter.TreatmentViewHolder> {
    public static class TreatmentViewHolder extends RecyclerView.ViewHolder {
        public CardView containerView;
        public TextView typeTextView;
        public TextView lastDateView;
        public TextView nextDateView;
        public TextView daysUntilView;
        public Button seeDetailsButton;
        public ImageButton menuButton;

        public TreatmentViewHolder(View view) {
            super(view);
            this.containerView = view.findViewById(R.id.card_view);
            this.typeTextView = view.findViewById(R.id.treatment_type);
            this.lastDateView = view.findViewById(R.id.last_date);
            this.nextDateView = view.findViewById(R.id.next_date);
            this.daysUntilView = view.findViewById(R.id.days_until);
            this.seeDetailsButton = view.findViewById(R.id.see_details_button);
            this.menuButton = view.findViewById(R.id.menu_button);
            this.seeDetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, TreatmentActivity.class);
                    intent.putExtra("id", ((Treatment) containerView.getTag()).id);
                    context.startActivity(intent);
                }
            });
        }
    }

    private List<Treatment> treatments = new ArrayList<>();
    private Context context;

    public TreatmentsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public TreatmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.treatment_row, parent, false);

        return new TreatmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TreatmentViewHolder holder, final int position) {
        Treatment current = treatments.get(position);
        holder.containerView.setTag(current);
        holder.typeTextView.setText(current.type);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        holder.nextDateView.setText("Próxima aplicação: " + formatter.format(current.nextDate));
        holder.lastDateView.setText("Última aplicação: " + formatter.format(current.lastDate));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate now = LocalDate.now();
            LocalDate nextDate = current.nextDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            long daysUntil = now.until(nextDate, ChronoUnit.DAYS);
            if (daysUntil > 1)
                holder.daysUntilView.setText("Em " + daysUntil + " dias");
            else if (daysUntil == 1)
                holder.daysUntilView.setText("Em 1 dia");
            else if (daysUntil == 0)
                holder.daysUntilView.setText("Hoje!");
            else if (daysUntil == -1)
                holder.daysUntilView.setText("1 dia atrasado");
            else
                holder.daysUntilView.setText(-daysUntil + " dias atrasado");
        }

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.menuButton, position, holder);
            }
        });
    }

    private void showPopupMenu(View view, final int position, final TreatmentViewHolder holder) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_button:
                        Intent intent = new Intent(context, EditTreatmentActivity.class);
                        intent.putExtra("id", ((Treatment) holder.containerView.getTag()).id);
                        context.startActivity(intent);
                        return true;
                    case R.id.delete_button:
                        new TreatmentDaoAsync().new DeleteTreatmentAsync(
                                new Callable<Void>() {
                                    public Void call() {
                                        treatments.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, treatments.size());
                                        return null;
                                    }
                                }).execute(((Treatment) holder.containerView.getTag()).id);
                        return true;
                    default:
                }
                return false;
            }
        });

        popup.show();
    }

    @Override
    public int getItemCount() {
        return treatments.size();
    }

    public void reload() {
        treatments = MainActivity.database.treatmentDao().getAll();
        notifyDataSetChanged();
    }

    public Treatment getTreatment(int position) {
        return treatments.get(position);
    }

    public void removeItem(int position, long id) {
        treatments.remove(position);
        MainActivity.database.treatmentDao().delete(id);
        notifyItemRemoved(position);
    }

    public void restoreItem(Treatment item, int position) {
        treatments.add(position, item);
        //MainActivity.database.treatmentDao().create(item.content);
        notifyItemInserted(position);
    }
}