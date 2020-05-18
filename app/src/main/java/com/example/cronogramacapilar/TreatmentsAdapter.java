package com.example.cronogramacapilar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TreatmentsAdapter extends RecyclerView.Adapter<TreatmentsAdapter.TreatmentViewHolder> {
    public static class TreatmentViewHolder extends RecyclerView.ViewHolder {
        public CardView containerView;
        public TextView typeTextView;
        public TextView lastDateView;
        public TextView nextDateView;
        public TextView daysUntilView;

        public TreatmentViewHolder(View view) {
            super(view);
            this.containerView = view.findViewById(R.id.card_view);
            this.typeTextView = view.findViewById(R.id.treatment_type);
            this.lastDateView = view.findViewById(R.id.last_date);
            this.nextDateView = view.findViewById(R.id.next_date);
            this.daysUntilView = view.findViewById(R.id.days_until);


            this.containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Treatment treatment = (Treatment) containerView.getTag();
                    //Intent intent = new Intent(v.getContext(), TreatmentActivity.class);
                    //intent.putExtra("id", note.id);
                    //intent.putExtra("content", note.content);

                    //context.startActivity(intent);
                }
            });
        }
    }

    private List<Treatment> treatments = new ArrayList<>();

    @Override
    public TreatmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.treatment_row, parent, false);

        return new TreatmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TreatmentViewHolder holder, int position) {
        Treatment current = treatments.get(position);
        holder.containerView.setTag(current);
        holder.typeTextView.setText(current.type);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        holder.nextDateView.setText("Próxima aplicação: " + formatter.format(current.nextDate));
        holder.lastDateView.setText("Última aplicação: " + formatter.format(current.lastDate));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate now = LocalDate.now();
            LocalDate nextDate = current.nextDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            holder.daysUntilView.setText("Em " + now.until(nextDate, ChronoUnit.DAYS) + " dias");
        }

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