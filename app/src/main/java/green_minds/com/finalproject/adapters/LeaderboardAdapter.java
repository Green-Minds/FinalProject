package green_minds.com.finalproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.activities.UserInfoActivity;
import green_minds.com.finalproject.fragments.LeaderboardFragment;
import green_minds.com.finalproject.model.GlideApp;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<ParseUser> mUsers;
    private Context context;
    private LeaderboardFragment.OnFragmentInteractionListener mListener;

    public LeaderboardAdapter(List<ParseUser> users) {mUsers = users;}

    public LeaderboardAdapter(ArrayList<ParseUser> users, LeaderboardFragment.OnFragmentInteractionListener listener) {
        mUsers = users;
        mListener = listener;
    }

    public void clear () {
        mUsers.clear();
        notifyDataSetChanged();
    }

    public void addAll (ArrayList<ParseUser> list) {
        mUsers.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View leaderboardView = inflater.inflate(R.layout.item_leaderboard, parent, false);
        ViewHolder viewHolder = new ViewHolder(leaderboardView);
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user = mUsers.get(position);

        holder.tvPosition.setText(String.valueOf(position + 1));
        holder.tvUserLeaderbaord.setText(user.getString("original_username"));
        holder.tvPts.setText(String.valueOf(user.getInt("points")) + " points");
        holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
        holder.tvPts.setTextColor(context.getResources().getColor(R.color.black));
        holder.tvPosition.setTextColor(context.getResources().getColor(R.color.black));
        holder.tvUserLeaderbaord.setTextColor(context.getResources().getColor(R.color.black));
        holder.itemView.setClickable(true);

        if (user.getInt("points") == 1) holder.tvPts.setText(String.valueOf(user.getInt("points")) + " point");
        if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
            holder.itemView.setBackground(context.getDrawable(R.drawable.gradient));
            holder.tvPts.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvPosition.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvUserLeaderbaord.setTextColor(context.getResources().getColor(R.color.white));
            holder.itemView.setClickable(false);
            holder.tvUserLeaderbaord.setText("You");
        }

        GlideApp.with(context)
                .load(user.getParseFile("smaller_photo").getUrl())
                .error(R.drawable.placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.ivUserImg);
    }

    @Override
    public int getItemCount() { return mUsers.size();}


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvPostion) TextView tvPosition;
        @BindView(R.id.ivUserImg) ImageView ivUserImg;
        @BindView(R.id.tvUserLeaderboard) TextView tvUserLeaderbaord;
        @BindView(R.id.tvPts) TextView tvPts;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                ParseUser parseUser = mUsers.get(position);
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("user", parseUser);
                context.startActivity(intent);
            }
        }
    }
}
