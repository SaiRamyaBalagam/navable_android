package com.navable.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.navable.app.R;
import com.navable.app.model.RouteStep;
import java.util.List;

public class RouteStepAdapter extends RecyclerView.Adapter<RouteStepAdapter.ViewHolder> {

    private final List<RouteStep> steps;

    public RouteStepAdapter(List<RouteStep> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RouteStep step = steps.get(position);
        holder.tvStepNumber.setText(String.valueOf(position + 1));
        holder.tvLocationName.setText(step.getLocationName());
        holder.tvLocationType.setText(step.getLocationType());
        if (position == 0) {
            holder.tvDistance.setText("Start here");
        } else {
            holder.tvDistance.setText(String.format("Walk %.1f meters", step.getDistanceFromPrevious()));
        }
    }

    @Override
    public int getItemCount() { return steps.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStepNumber, tvLocationName, tvLocationType, tvDistance;

        ViewHolder(View view) {
            super(view);
            tvStepNumber = view.findViewById(R.id.tvStepNumber);
            tvLocationName = view.findViewById(R.id.tvLocationName);
            tvLocationType = view.findViewById(R.id.tvLocationType);
            tvDistance = view.findViewById(R.id.tvDistance);
        }
    }
}
