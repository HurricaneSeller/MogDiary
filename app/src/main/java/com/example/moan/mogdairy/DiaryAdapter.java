package com.example.moan.mogdairy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
    private List<Diary> diaryList;
    private Context myContext;
    public static final int WHERE = 0;

    public DiaryAdapter(List<Diary> myDiaryList) {
        diaryList = myDiaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (myContext == null) {
            myContext = viewGroup.getContext();
        }

        final View view = LayoutInflater.from(myContext).inflate(R.layout.diary_layout,
                viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);


        viewHolder.diaryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Diary diary = diaryList.get(position);
                Intent intent = new Intent(myContext, WritingActivity.class);
                intent.putExtra("diaryGotten", diary);
                intent.putExtra("where", WHERE);
                myContext.startActivity(intent);
            }
        });


        viewHolder.diaryView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(view, "Delete Diary ?", Snackbar.LENGTH_LONG).
                        setAction("DELETE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int position = viewHolder.getAdapterPosition();
                                Diary diary = diaryList.get(position);
                                LitePal.delete(Diary.class, diary.getId());
                                diaryList.remove(diary);
                                MainActivity.diaryAdapter.notifyDataSetChanged();
                            }
                        }).setActionTextColor(Color.parseColor("#2e8b57"))
                        .show();
                return true;
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Diary diary = diaryList.get(i);
        viewHolder.titleView.setText(diary.getTitle().toString());
        viewHolder.contentClippingView.setText(diary.getContent().toString());

        String datewithFilter = diary.getDate().toLocaleString();
        SimpleDateFormat MonthandDay = new SimpleDateFormat("MM-dd HH:mm");
        datewithFilter = MonthandDay.format(diary.getDate());

        viewHolder.dateView.setText(datewithFilter);
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View diaryView;
        TextView titleView;
        TextView contentClippingView;
        TextView dateView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.showing_diary_title);
            contentClippingView = (TextView) itemView.findViewById(R.id.showing_diary_content_clipping);
            dateView = (TextView) itemView.findViewById(R.id.showing_diary_date);
            diaryView = itemView;
        }
    }
}
