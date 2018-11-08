package com.example.moan.mogdairy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wx.wheelview.widget.WheelViewDialog;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
 * with a clock it change background color
 * */
public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
    private static final String DONE_COLOR = "#808080";
    private static final String WITHOUT_CLOCK_COLOR = "#f0fff0";
    private static final String WITH_CLOCK_COLOR = "#ffe4e1";
    private List<Diary> diaryList;
    private Context mContext;
    static final int WHERE = 0;

    DiaryAdapter(List<Diary> myDiaryList) {
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
        viewHolder.priorityButton.setText(diary.getPriority());


        int dueMonth = diary.getMonth();
        int dueDay = diary.getDay();
        int dueHour = diary.getHour();
        int dueMinute = diary.getMinute();
        long total = diary.getTotal();

        if (diary.isDone()) {
            viewHolder.diaryCardView.setCardBackgroundColor(Color.parseColor(DONE_COLOR));
        } else {
            if (diary.isHasClock()) {

                Calendar calendar = Calendar.getInstance();
                int systemMonth = calendar.get(Calendar.MONTH);
                int systemDay = calendar.get(Calendar.DAY_OF_MONTH);
                int systemHour = calendar.get(Calendar.HOUR_OF_DAY);
                int systemMinute = calendar.get(Calendar.MINUTE);

                long system_total = systemMonth * 43200 + systemDay * 1440 + systemHour * 60 + systemMinute;

                viewHolder.dateViewFirst.setText(dueMonth + "月" + dueDay + "日");
                viewHolder.dateViewLast.setText(dueHour + "时" + dueMinute + "分");
                if (system_total >= total) {
                    viewHolder.diaryCardView.setCardBackgroundColor(Color.parseColor(WITH_CLOCK_COLOR));
                } else {
                    viewHolder.diaryCardView.setCardBackgroundColor(Color.parseColor(WITHOUT_CLOCK_COLOR));
                }
            } else {

                if (diary.isHasClock()) {
                    viewHolder.dateViewFirst.setText(dueMonth + "月" + dueDay + "日");
                    viewHolder.dateViewLast.setText(dueHour + "时" + dueMinute + "分");
                } else {
                    viewHolder.dateViewFirst.setText("");
                    viewHolder.dateViewLast.setText("");
                }viewHolder.diaryCardView.setCardBackgroundColor(Color.parseColor(WITHOUT_CLOCK_COLOR));
            }
        }


    }


    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View diaryView;
        TextView titleView;
        TextView contentClippingView;
        TextView dateViewFirst;
        TextView dateViewLast;
        Button priorityButton;
        CardView diaryCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleView = itemView.findViewById(R.id.showing_diary_title);
            contentClippingView = itemView.findViewById(R.id.showing_diary_content_clipping);
            dateViewFirst = itemView.findViewById(R.id.showing_diary_deadline_first);
            dateViewLast = itemView.findViewById(R.id.showing_diary_deadline_last);
            priorityButton = itemView.findViewById(R.id.showing_diary_priority);
            diaryView = itemView;
            diaryCardView = itemView.findViewById(R.id.showing_diary_card_view);
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
