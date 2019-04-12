package com.example.datavisual;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class PowerButtonAdapter extends RecyclerView.Adapter<PowerButtonAdapter.ViewHolder> {

    private List<PowerButton> mFruitList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View powerbuttonView;
        ImageView powerbutton_Image;
        TextView powerbutton_Name;

        public ViewHolder(View view){
            super(view);
            powerbuttonView = view;
            powerbutton_Image = (ImageView)view.findViewById(R.id.powerbutton_image);
            powerbutton_Name = (TextView)view.findViewById(R.id.powerbutton_name);
        }
    }
    public PowerButtonAdapter(List<PowerButton>fruitList){
        mFruitList = fruitList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.powerbutton_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.powerbuttonView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = holder.getAdapterPosition();
                PowerButton fruit = mFruitList.get(position);
                Toast.makeText(v.getContext(),"you clicked view" + fruit.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.powerbutton_Image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = holder.getAdapterPosition();
                PowerButton fruit = mFruitList.get(position);
                Toast.makeText(v.getContext(),"you clicked image" + fruit.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        //ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        PowerButton fruit = mFruitList.get(position);
        holder.powerbutton_Image.setImageResource(fruit.getImageId());
        holder.powerbutton_Name.setText(fruit.getName());
    }
    @Override
    public int getItemCount(){
        return mFruitList.size();
    }

}
