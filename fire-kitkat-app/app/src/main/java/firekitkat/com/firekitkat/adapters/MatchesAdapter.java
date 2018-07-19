package firekitkat.com.firekitkat.adapters;


import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

import firekitkat.com.firekitkat.FireKitkat;
import firekitkat.com.firekitkat.R;
import firekitkat.com.firekitkat.models.Match;
import firekitkat.com.firekitkat.views.LoginActivity;


/***
 * Adapter class to handle  Recycler view match items
 */
public class MatchesAdapter extends  FirestoreAdapter<MatchesAdapter.ViewHolder>  {


    public MatchesAdapter(Query query) {
        super(query);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCardMatchTitle;
        TextView tvMatchResult;
        ImageView ivMatchUser;
        ImageView ivMatchArtist;
        ImageButton ivMatchVote;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCardMatchTitle = itemView.findViewById(R.id.tvCardMatchTitle);
            tvMatchResult = itemView.findViewById(R.id.tvMatchResult);
            ivMatchUser = itemView.findViewById(R.id.ivMatchUser);
            ivMatchArtist = itemView.findViewById(R.id.ivMatchArtist);
            ivMatchVote = itemView.findViewById(R.id.ivMatchVote);


        }
        public void bind(final DocumentSnapshot snapshot) {
            final Match match = snapshot.toObject(Match.class);
            if(FireKitkat.getInstance().getUser()!=null){//not best to do it her :(
                snapshot.getReference().collection("votes").document(FireKitkat.getInstance().getUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        final DocumentSnapshot document = task.getResult();
                        int imageResource=R.drawable.ic_exposure_plus_1_enabled;
                        if (document.exists()) {
                            match.setVoted(true);
                            ivMatchVote.setClickable(false);
                            imageResource=R.drawable.ic_exposure_plus_1_disabled;
                        }else{
                            ivMatchVote.setOnClickListener(new View.OnClickListener() {//not the best to implement here
                                @Override
                                public void onClick(View view) {
                                    if(FireKitkat.getInstance().getUser()!=null) {
                                        HashMap<String, Boolean> mapVote = new HashMap<>();
                                        mapVote.put("vote", true);
                                        document.getReference().set(mapVote);
                                        ivMatchVote.setBackgroundResource(R.drawable.ic_exposure_plus_1_disabled);
                                    }
                                    else
                                    {
                                        Intent intentLogin = new Intent(ivMatchVote.getContext(), LoginActivity.class);
                                        ivMatchVote.getContext().startActivity(intentLogin);
                                    }
                                }
                            });
                        }
                        ivMatchVote.setBackgroundResource(imageResource);
                    }
                });
            }

            Resources resources = itemView.getResources();
            // Load image
            Glide.with(ivMatchUser.getContext())
                    .load(match.getUser().getPhotoUrl())
                    .into(ivMatchUser);
            Glide.with(ivMatchArtist.getContext())
                    .load(match.getArtist().getPhotoUrl())
                    .into(ivMatchArtist);
            String matchTitle = String.format(resources.getString(R.string.str_tvmatchtitle), match.getUser().getName(), match.getArtist().getName());
            String matchResult = String.format(resources.getString(R.string.str_tvmatchresult), match.getResult()+"");


            tvCardMatchTitle.setText(matchTitle);
            tvMatchResult.setText(matchResult);

        }
    }
    @NonNull
    @Override
    public MatchesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.match_card, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
    }
}
