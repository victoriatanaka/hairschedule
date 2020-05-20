package com.example.cronogramacapilar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
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
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

public class TreatmentsAdapter extends RecyclerView.Adapter<TreatmentsAdapter.TreatmentViewHolder> {
    public static class TreatmentViewHolder extends RecyclerView.ViewHolder {
        public CardView containerView;
        public TextView typeTextView;
        public TextView lastDateView;
        public TextView nextDateView;
        public TextView daysUntilView;
        public Button seeDetailsButton;
        public Button markAsCompleteButton;
        public ImageButton menuButton;

        public TreatmentViewHolder(final View view) {
            super(view);
            this.containerView = view.findViewById(R.id.card_view);
            this.typeTextView = view.findViewById(R.id.treatment_type);
            this.lastDateView = view.findViewById(R.id.last_date);
            this.nextDateView = view.findViewById(R.id.next_date);
            this.daysUntilView = view.findViewById(R.id.days_until);
            this.seeDetailsButton = view.findViewById(R.id.see_details_button);
            this.markAsCompleteButton = view.findViewById(R.id.mark_as_complete_button);
            this.menuButton = view.findViewById(R.id.menu_button);
        }
    }

    private List<Treatment> treatments = new ArrayList<>();
    private Context context;

    // Constructor receives the Activity's context
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
        final Treatment current = treatments.get(position);

        // setup type 
        holder.typeTextView.setText(current.type);

        // setup dates
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        holder.nextDateView.setText(context.getString(R.string.next_application, formatter.format(current.nextDate)));
        holder.lastDateView.setText(context.getString(R.string.last_application, formatter.format(current.lastDate)));

        // setup days until + button to mark as complete
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate now = LocalDate.now();
            LocalDate nextDate = current.nextDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate lastDate = current.lastDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long daysUntil = now.until(nextDate, ChronoUnit.DAYS);
            long daysSince = now.until(lastDate, ChronoUnit.DAYS);

            setupMarkAsCompleteButton(holder, daysSince, position);
            setupDaysUntil(holder, daysUntil);
        }

        // setup menu button
        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.menuButton, position, holder);
            }
        });

        holder.seeDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TreatmentActivity.class);
                intent.putExtra("id", current.id);
                context.startActivity(intent);
            }
        });
    }

    private void setupDaysUntil(TreatmentViewHolder holder, long daysUntil) {
        if (daysUntil > 1)
            holder.daysUntilView.setText(context.getString(R.string.in_n_days, daysUntil));
        else if (daysUntil == 1)
            holder.daysUntilView.setText(R.string.in_one_day);
        else if (daysUntil == 0)
            holder.daysUntilView.setText(R.string.today);
        else if (daysUntil == -1)
            holder.daysUntilView.setText(R.string.one_day_late);
        else
            holder.daysUntilView.setText(context.getString(R.string.n_days_late, -daysUntil));
    }

    private void setupMarkAsCompleteButton(TreatmentViewHolder holder, long daysSince, final int position) {
        if (daysSince == 0)
            holder.markAsCompleteButton.setVisibility(View.GONE);
        else {
            holder.markAsCompleteButton.setVisibility(View.VISIBLE);
            holder.markAsCompleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final Treatment current = treatments.get(position);
                    Treatment previous = null;
                    try {
                        previous = (Treatment) current.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    current.lastDate = new Date();
                    current.nextDate = Treatment.calculateNextDate(current.lastDate, current.repeats, current.repeatsUnit);
                    final Treatment finalPrevious = previous;
                    new TreatmentDaoAsync().new SaveTreatmentAsync(
                            new Callable<Void>() {
                                public Void call() {
                                    reload();
                                    createSnackbar(finalPrevious);
                                    return null;
                                }
                            }, current).execute();
                }
            });
        }
    }

    private void createSnackbar(final Treatment previous) {
        final Snackbar snackbar = Snackbar
                .make(((Activity) context).findViewById(R.id.coordinator_layout), "Tratamento marcado como feito.", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Desfazer", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("teste", previous.lastDate.toString());
                new TreatmentDaoAsync().new SaveTreatmentAsync(
                        new Callable<Void>() {
                            public Void call() {
                                reload();
                                return null;
                            }
                        }, previous).execute();
            }
        });

        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                snackbar.dismiss();
            }
        });
        snackbar.show();
        ((MainActivity) context).setSnackbar(snackbar);
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
                        Treatment current = treatments.get(position);
                        Intent intent = new Intent(context, EditTreatmentActivity.class);
                        intent.putExtra("id", current.id);
                        context.startActivity(intent);
                        return true;

                    case R.id.delete_button:
                        current = treatments.remove(position);
                        new TreatmentDaoAsync().new DeleteTreatmentAsync(
                                new Callable<Void>() {
                                    public Void call() {
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, treatments.size());
                                        return null;
                                    }
                                }).execute(current.id);
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