package by.bsuir.relaxapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.charset.MalformedInputException;

public class MoodAdapter extends RecyclerView.Adapter<MoodAdapter.ViewHolder>{

    public MoodAdapter(Context context) {
        this.context = context;
    }

    private Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mood_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mood_image.setImageResource(MainActivity.APP_MOODS[position].moodImageResourceID);
        holder.mood_name.setText(MainActivity.APP_MOODS[position].name);

        int pos = position;
        holder.card_view.setOnClickListener(lambda->{
            MainActivity.currentMood = pos;
            FeedFragment.setAppropriateNewsAdapter();
        });
    }

    @Override
    public int getItemCount() {
        return MainActivity.MOODS_COUNT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView card_view;
        ImageView mood_image;
        TextView mood_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mood_image = itemView.findViewById(R.id.mood_icon);
            mood_name = itemView.findViewById(R.id.mood_text);
            card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
