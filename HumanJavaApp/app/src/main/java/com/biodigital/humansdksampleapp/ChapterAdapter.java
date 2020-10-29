package com.biodigital.humansdksampleapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.biodigital.humansdk.HKChapter;

public class ChapterAdapter extends FragmentStatePagerAdapter {

    private HKChapter[] chapters = new HKChapter[0];

    public ChapterAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setChapters(HKChapter[] chaps) {
        chapters = chaps;
        System.out.println("got chapters");
        this.notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int i) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle args = new Bundle();
        args.putString(ChapterFragment.ARG_TITLE, chapters[i].title);
        args.putString(ChapterFragment.ARG_DESC, chapters[i].description);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return chapters.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return chapters[position].title;
    }


    public static class ChapterFragment extends Fragment {
        public static final String ARG_TITLE = "title";
        public static final String ARG_DESC = "description";

        boolean expanded = false;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.chapter_fragment, container, false);
            Bundle args = getArguments();

            String title = args.getString(ARG_TITLE);
            String description = args.getString(ARG_DESC);
            System.out.println("got chapter title " + title);

            rootView.setBackgroundColor(Color.LTGRAY);
            rootView.getBackground().setAlpha(70);

            TextView chapterTitleText = (TextView)rootView.findViewById(R.id.chapterTitle);
            chapterTitleText.setText(title);
            TextView chapterDescText = (TextView)rootView.findViewById(R.id.chapterDescription);
            chapterDescText.setText(Html.fromHtml(description));
            chapterTitleText.setMovementMethod(new ScrollingMovementMethod());
            chapterDescText.setMovementMethod(new ScrollingMovementMethod());

            chapterTitleText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((HumanActivity)getActivity()).handleChapterClick();
                }
            });
            return rootView;
        }


    }
}
