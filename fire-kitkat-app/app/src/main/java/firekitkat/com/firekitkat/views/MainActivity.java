package firekitkat.com.firekitkat.views;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import firekitkat.com.firekitkat.R;
import firekitkat.com.firekitkat.adapters.MatchesAdapter;
import firekitkat.com.firekitkat.models.Artist;
import firekitkat.com.firekitkat.models.Match;
import firekitkat.com.firekitkat.models.User;

public class MainActivity extends AppCompatActivity {
    RecyclerView rvMatches;
    RelativeLayout rlEmpty;
    MatchesAdapter adapterRvMatches;
    ArrayList<Match> matchArrayList;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        rvMatches = findViewById(R.id.rvMatches);
        rlEmpty= findViewById(R.id.rlEmpty);
        rvMatches.setHasFixedSize(true);
        LinearLayoutManager layoutManagerRvMatches = new LinearLayoutManager(this);
        rvMatches.setLayoutManager(layoutManagerRvMatches);

        query = db.collection("matches")
                .orderBy("votesCount", Query.Direction.DESCENDING)
                .limit(20);

        adapterRvMatches = new MatchesAdapter(query){
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    rvMatches.setVisibility(View.GONE);
                    rlEmpty.setVisibility(View.VISIBLE);
                } else {
                    rvMatches.setVisibility(View.VISIBLE);
                    rlEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error trying to fetch data", Snackbar.LENGTH_LONG).show();
            }
        };
        rvMatches.setAdapter(adapterRvMatches);
    }
    @Override
    protected void onStart(){
        super.onStart();
        if (adapterRvMatches != null) {
            adapterRvMatches.startListening();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (adapterRvMatches != null) {
            adapterRvMatches.stopListening();
        }
    }

    public void fabNewMatchOnClick(View view) {
        Intent intent = new Intent(this, MatchActivity.class);
        startActivity(intent);
    }


}
