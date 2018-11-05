package com.example.moan.mogdairy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wx.wheelview.widget.WheelViewDialog;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*
* with a clock it change background color
* */
public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
    private List<Diary> diaryList;
    private Context mContext;
    public static final int WHERE = 0;

    public DiaryAdapter(List<Diary> myDiaryList) {
        diaryList = myDiaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }

        final View view = LayoutInflater.from(mContext).inflate(R.layout.diary_layout,
                viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.priorityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                final Diary mDiary = diaryList.get(position);
                final WheelViewDialog choosePriorityDialog = new WheelViewDialog(mContext);
                choosePriorityDialog.setCount(1)
                        .setItems(createNumbers())
                        .setTitle("choose priority")
                        .setButtonText("ok")
                        .setLoop(true)
                        .setOnDialogItemClickListener(new WheelViewDialog.OnDialogItemClickListener() {
                            @Override
                            public void onItemClick(int position, String s) {
                                mDiary.setPriority(s);
                                mDiary.update(mDiary.getId());
                                DiaryAdapter.this.notifyDataSetChanged();
                            }
                        })
                        .show();
            }
        });

        viewHolder.diaryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Diary diary = diaryList.get(position);
                Intent intent = new Intent(mContext, WritingActivity.class);
                intent.putExtra("diaryGotten", diary);
                intent.putExtra("where", WHERE);
                mContext.startActivity(intent);
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
//                                MainActivity.diaryAdapter.notifyDataSetChanged();
                                DiaryAdapter.this.notifyDataSetChanged();
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
        viewHolder.titleView.setText(diary.getTitle());
        viewHolder.contentClippingView.setText(diary.getContent());



        // TODO: 11/5/18 make it
//        if (diary.isHasClock()){
//            viewHolder.dateView.setText("someting");
//        }
        viewHolder.priorityButton.setText(diary.getPriority());



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
        Button priorityButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView =  itemView.findViewById(R.id.showing_diary_title);
            contentClippingView =  itemView.findViewById(R.id.showing_diary_content_clipping);
            dateView =  itemView.findViewById(R.id.showing_diary_deadline);
            priorityButton = itemView.findViewById(R.id.showing_diary_priority);
            diaryView = itemView;
        }
    }
    private ArrayList<String> createNumbers() {
        ArrayList<String> nums = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            nums.add(String.valueOf(i));
        }
        return nums;
    }
}
