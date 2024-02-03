package com.example.criapp.abstractions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.criapp.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class CriAppSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<String> data;
    public int position;
    public TextInputLayout inputLayout;

    public CriAppSpinnerAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.criapp_spinner_layout, parent, false);
        }

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(data.get(position));

        return view;
    }
}
