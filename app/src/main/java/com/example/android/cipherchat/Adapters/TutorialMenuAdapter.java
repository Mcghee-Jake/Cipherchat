package com.example.android.cipherchat.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.cipherchat.Objects.Tutorial;
import com.example.android.encryptedmessengerapp.R;

import java.util.List;

public class TutorialMenuAdapter extends RecyclerView.Adapter<TutorialMenuAdapter.TutorialViewHolder> {

    private List<Tutorial> tutorialList;
    private TutorialMenuClickListener tutorialMenuClickListener;

    public TutorialMenuAdapter(List<Tutorial> tutorialList, TutorialMenuClickListener tutorialMenuClickListener) {
        this.tutorialList = tutorialList;
        this.tutorialMenuClickListener = tutorialMenuClickListener;
    }

    public interface TutorialMenuClickListener {
        void onMenuItemClicked(int position);
    }

    @NonNull
    @Override
    public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.tutorial_list_item, viewGroup, false);
        return new TutorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialViewHolder tutorialViewHolder, int i) {
        tutorialViewHolder.tutorialTitle.setText(i+1 + ". " + tutorialList.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return tutorialList.size();
    }

    public class TutorialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tutorialTitle;

        public TutorialViewHolder(@NonNull View itemView) {
            super(itemView);
            tutorialTitle = itemView.findViewById(R.id.tv_tutorial_list_item_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            tutorialMenuClickListener.onMenuItemClicked(getAdapterPosition());
        }
    }
}
