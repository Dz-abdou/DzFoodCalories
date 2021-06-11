package example.com.pfe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder> {

    private String[] foodNames;
    private int[] foodImages;
    private String calories[];
    Context context;
    RecyclerViewOnItemClick recyclerViewOnItemClick;


    public FoodItemAdapter(String[] foodNames, int[] foodImages, String[] calories, Context context, RecyclerViewOnItemClick recyclerViewOnItemClick) {
        this.foodNames = foodNames;
        this.foodImages = foodImages;
        this.calories = calories;
        this.context = context;
        this.recyclerViewOnItemClick = recyclerViewOnItemClick;
    }

    @NonNull
    @Override
    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.common_food_item, parent, false);
        return new FoodItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemViewHolder holder, int position) {
        holder.foodNameTextView.setText(foodNames[position]);
        holder.foodImageView.setImageResource(this.foodImages[position]);
        holder.caloriesTextView.setText(this.calories[position]);

    }

    @Override
    public int getItemCount() {
        return this.foodNames.length;
    }

    public class FoodItemViewHolder extends RecyclerView.ViewHolder {

        TextView foodNameTextView;
        TextView caloriesTextView;
        ImageView foodImageView;

        public FoodItemViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.foodNameTextView);
            caloriesTextView = itemView.findViewById(R.id.caloriesTextView);
            foodImageView = itemView.findViewById(R.id.foodImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewOnItemClick.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
