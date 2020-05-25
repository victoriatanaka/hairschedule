package com.example.cronogramacapilar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.arch.core.util.Function;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cronogramacapilar.activities.EditTreatmentActivity;
import com.example.cronogramacapilar.activities.MainActivity;
import com.example.cronogramacapilar.activities.TreatmentActivity;
import com.example.cronogramacapilar.helpers.DeleteTreatmentWithConfirm;
import com.example.cronogramacapilar.helpers.NotificationHelper;
import com.example.cronogramacapilar.helpers.TreatmentDaoAsync;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

public class TreatmentsAdapter extends RecyclerView.Adapter<TreatmentsAdapter.TreatmentViewHolder> {
    public static class TreatmentViewHolder extends RecyclerView.ViewHolder {
        final TextView typeTextView;
        final TextView lastDateView;
        final TextView nextDateView;
        final TextView daysUntilView;
        final Button seeDetailsButton;
        final Button markAsCompleteButton;
        final ImageButton menuButton;
        final CardView containerView;

        TreatmentViewHolder(final View view) {
            super(view);
            this.typeTextView = view.findViewById(R.id.treatment_type);
            this.containerView = view.findViewById(R.id.card_view);
            this.lastDateView = view.findViewById(R.id.last_date);
            this.nextDateView = view.findViewById(R.id.next_date);
            this.daysUntilView = view.findViewById(R.id.days_until);
            this.seeDetailsButton = view.findViewById(R.id.see_details_button);
            this.markAsCompleteButton = view.findViewById(R.id.mark_as_complete_button);
            this.menuButton = view.findViewById(R.id.menu_button);
        }
    }

    private List<Treatment> treatments = new ArrayList<>();
    private final Context context;

    // Constructor receives the Activity's context
    public TreatmentsAdapter(Context context) {
        this.context = context;
    }

    @Override
    @NonNull
    public TreatmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.treatment_row, parent, false);

        return new TreatmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TreatmentViewHolder holder, final int position) {
        final Treatment current = treatments.get(position);
        holder.containerView.setTag(current);

        // setup type 
        holder.typeTextView.setText(current.type);

        // setup dates
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.nextDateView.setText(context.getString(R.string.next_application_n, formatter.format(current.nextDate)));
        holder.lastDateView.setText(context.getString(R.string.last_application_n, formatter.format(current.lastDate)));

        // setup days until + button to mark as complete
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate now = LocalDate.now();
            LocalDate nextDate = current.nextDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate lastDate = current.lastDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long daysUntil = now.until(nextDate, ChronoUnit.DAYS);
            long daysSince = now.until(lastDate, ChronoUnit.DAYS);

            setupMarkAsCompleteButton(holder, daysSince, daysUntil, position);
            setupDaysUntil(holder, daysUntil);
        }

        // setup menu button
        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.menuButton, position);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupDaysUntil(TreatmentViewHolder holder, long daysUntil) {
        if (daysUntil >= 1) {
            holder.daysUntilView.setText(context.getResources().getQuantityString(R.plurals.in_n_days, (int) daysUntil, daysUntil));
            holder.daysUntilView.setTypeface(null, Typeface.NORMAL);
            holder.daysUntilView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        }
        else if (daysUntil == 0) {
            holder.daysUntilView.setText(R.string.today);
            holder.daysUntilView.setTypeface(null, Typeface.BOLD_ITALIC);
            holder.daysUntilView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        } else {
            holder.daysUntilView.setTypeface(null, Typeface.NORMAL);
            holder.daysUntilView.setText(context.getResources().getQuantityString(R.plurals.n_days_late, (int) -daysUntil, -daysUntil));
            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_warning_black_24dp);
            holder.daysUntilView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
        }
    }

    private void setupMarkAsCompleteButton(TreatmentViewHolder holder, long daysSince, long daysUntil, final int position) {
        if (daysSince == 0)
            holder.markAsCompleteButton.setVisibility(View.GONE);
        else {
            holder.markAsCompleteButton.setVisibility(View.VISIBLE);

            if (daysUntil <= 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.markAsCompleteButton.setTextAppearance(R.style.TextAppearance_AppCompat_Widget_Button_Colored);
                    Drawable drawable = context.getDrawable(R.drawable.ic_check_white_24dp);
                    holder.markAsCompleteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
                    holder.markAsCompleteButton.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.colorAccent)));
                }


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
                    new TreatmentDaoAsync.SaveTreatmentAsync(
                            new Callable<Void>() {
                                public Void call() {
                                    reload();
                                    NotificationHelper.createNotification(current, context);
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
                new TreatmentDaoAsync.SaveTreatmentAsync(
                        new Callable<Void>() {
                            public Void call() {
                                reload();
                                NotificationHelper.createNotification(previous, context);
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

    private void showPopupMenu(View view, final int position) {
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
                        current = treatments.get(position);
                        DeleteTreatmentWithConfirm.deleteTreatment(
                                new Callable<Void>() {
                                    public Void call() {
                                        treatments.remove(position);
                                        if (treatments.isEmpty())
                                            ((MainActivity) context).createShowcase();
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, treatments.size());
                                        return null;
                                    }
                                }, current.id, context);
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
        new TreatmentDaoAsync.GetAllTreatmentsAsync(
                new Function<List<Treatment>, Void>() {
                    @Override
                    public Void apply(List<Treatment> treatmentList) {
                        treatments = treatmentList;
                        if (treatments.isEmpty())
                            ((MainActivity) context).createShowcase();
                        else
                            ((MainActivity) context).hideShowcase();
                        notifyDataSetChanged();
                        return null;
                    }
                }).execute();
    }
}