package com.example.cronogramacapilar;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

public class DynamicWidthSpinner extends androidx.appcompat.widget.AppCompatSpinner {

    public DynamicWidthSpinner(Context context) {
        super(context);
    }

    public DynamicWidthSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicWidthSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(adapter != null ? new WrapperSpinnerAdapter(adapter) : null);
    }


    final class WrapperSpinnerAdapter implements SpinnerAdapter {

        private final SpinnerAdapter mBaseAdapter;


        WrapperSpinnerAdapter(SpinnerAdapter baseAdapter) {
            mBaseAdapter = baseAdapter;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            return mBaseAdapter.getView(getSelectedItemPosition(), convertView, parent);
        }

// --Commented out by Inspection START (21/05/20 16:56):
//        public final SpinnerAdapter getBaseAdapter() {
//            return mBaseAdapter;
//        }
// --Commented out by Inspection STOP (21/05/20 16:56)

        public int getCount() {
            return mBaseAdapter.getCount();
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return mBaseAdapter.getDropDownView(position, convertView, parent);
        }

        public Object getItem(int position) {
            return mBaseAdapter.getItem(position);
        }

        public long getItemId(int position) {
            return mBaseAdapter.getItemId(position);
        }

        public int getItemViewType(int position) {
            return mBaseAdapter.getItemViewType(position);
        }

        public int getViewTypeCount() {
            return mBaseAdapter.getViewTypeCount();
        }

        public boolean hasStableIds() {
            return mBaseAdapter.hasStableIds();
        }

        public boolean isEmpty() {
            return mBaseAdapter.isEmpty();
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            mBaseAdapter.registerDataSetObserver(observer);
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            mBaseAdapter.unregisterDataSetObserver(observer);
        }
    }
}
