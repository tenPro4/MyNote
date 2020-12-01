package com.example.mynote.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mynote.R;
import com.example.mynote.models.Folder;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {

    private ArrayList<Folder> items;
    private ClickListener listener;

    public FolderAdapter(ArrayList<Folder> items, ClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_folder, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Folder item = items.get(position);

        holder.title.setText(item.name);
        if (item.isDirectory) {
            holder.icon.setImageResource(item.isBack ? R.drawable.back_folder : R.drawable.ic_folder);
        } else {
            holder.icon.setImageResource(R.drawable.ic_file);
        }

        holder.holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(item);
            }
        });
    }

    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public View holder;
        public TextView title;
        public ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            holder = itemView.findViewById(R.id.holder);
            title = (TextView) itemView.findViewById(R.id.title);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }
    }

    public interface ClickListener{
        void onClick(Folder item);
    }
}
