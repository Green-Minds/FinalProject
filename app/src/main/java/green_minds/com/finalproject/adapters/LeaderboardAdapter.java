package green_minds.com.finalproject.adapters;

import android.content.Context;
import android.graphics.Color;
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
import green_minds.com.finalproject.model.GlideApp;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<ParseUser> mUsers;
    private Context context;

    public LeaderboardAdapter(List<ParseUser> users) {mUsers = users;}

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ParseUser user = mUsers.get(position);
        holder.tvPosition.setText(String.valueOf(position + 1));
        holder.tvUserLeaderbaord.setText(user.getUsername());
        holder.tvPts.setText(String.valueOf(user.getInt("points")) + " points");
        if (user.getInt("points") == 1) holder.tvPts.setText(String.valueOf(user.getInt("points")) + " point");
        if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername()))
            holder.itemView.setBackgroundColor(Color.parseColor("#D3D3D3"));

        GlideApp.with(context)
                .load(user.getParseFile("smaller_photo").getUrl())
                .placeholder(R.drawable.camera)
                .apply(RequestOptions.circleCropTransform())
                .error(R.drawable.placeholder)
                .into(holder.ivUserImg);
    }

    @Override
    public int getItemCount() { return mUsers.size();}


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvPostion) TextView tvPosition;
        @BindView(R.id.ivUserImg) ImageView ivUserImg;
        @BindView(R.id.tvUserLeaderboard) TextView tvUserLeaderbaord;
        @BindView(R.id.tvPts) TextView tvPts;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
