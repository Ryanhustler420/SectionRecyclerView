package com.example.testing;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();

        // we can add all sorts of thing here. aka different layout for each section. not tested!
        sectionedRecyclerViewAdapter.addSection(new MySection("Top Rated Movies", getTopRatedMoviesList()));
        sectionedRecyclerViewAdapter.addSection(new MySection("Most Popular Movies", getMostPopularMoviesList()));

        recyclerView = findViewById(R.id.recycleView);

        GridLayoutManager glm = new GridLayoutManager(getApplicationContext(), 3);
        // we can change the col span above but make sure you change below as well
        // else you will see clutter layout
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (sectionedRecyclerViewAdapter.getSectionItemViewType(position)
                        == SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER) {
                    return 3;
                }
                return 1;
            }
        });

        // recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(sectionedRecyclerViewAdapter);

        Handler h = new Handler();

        // we are removing section and adding new data
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                int size = sectionedRecyclerViewAdapter.getSectionCount();
                for (int i = 0; i < size; i++) {
                    Section section = sectionedRecyclerViewAdapter.getSectionForPosition(i);
                    sectionedRecyclerViewAdapter.removeSection(section);
                    if (sectionedRecyclerViewAdapter.getSectionCount() <= 0) {
                        Toast.makeText(getApplicationContext(), "" + sectionedRecyclerViewAdapter.getSectionCount(), Toast.LENGTH_LONG).show();
                        sectionedRecyclerViewAdapter.notifyDataSetChanged();
                    }
                }
                // here we might to notify recycleViews, lets see
                sectionedRecyclerViewAdapter.addSection(new MySection("Adult Films", getAdultFilms()));
                sectionedRecyclerViewAdapter.notifyItemChanged(getAdultFilms().size() - 1);
                sectionedRecyclerViewAdapter.addSection(new MySection("Top Rated Movies", getTopRatedMoviesList()));
                sectionedRecyclerViewAdapter.notifyItemChanged(getTopRatedMoviesList().size() - 1);
                sectionedRecyclerViewAdapter.addSection(new MySection("Most Popular Movies", getMostPopularMoviesList()));
                sectionedRecyclerViewAdapter.notifyItemChanged(getMostPopularMoviesList().size() - 1);
            }
        }, 3000);

    }

    private List<Movie> getAdultFilms() {
        List<String> arrayList = new ArrayList<>(Arrays.asList(getResources()
                .getStringArray(R.array.adult_movies)));

        List<Movie> movieList = new ArrayList<>();

        for (String str : arrayList) {
            String[] array = str.split("\\|");
            movieList.add(new Movie(array[0], array[1]));
        }

        return movieList;
    }


    private List<Movie> getTopRatedMoviesList() {
        List<String> arrayList = new ArrayList<>(Arrays.asList(getResources()
                .getStringArray(R.array.top_rated_movies)));

        List<Movie> movieList = new ArrayList<>();

        for (String str : arrayList) {
            String[] array = str.split("\\|");
            movieList.add(new Movie(array[0], array[1]));
        }

        return movieList;
    }

    private List<Movie> getMostPopularMoviesList() {
        List<String> arrayList = new ArrayList<>(Arrays.asList(getResources()
                .getStringArray(R.array.most_popular_movies)));

        List<Movie> movieList = new ArrayList<>();

        for (String str : arrayList) {
            String[] array = str.split("\\|");
            movieList.add(new Movie(array[0], array[1]));
        }

        return movieList;
    }


    class MySection extends StatelessSection {

        final String title;
        final List<Movie> list;

        List<String> itmeList = Arrays.asList("Gaurav", "Saurav", "Sangeeta", "Sarita");

        MySection(String title, List<Movie> list) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.section_ex5_item)
                    .headerResourceId(R.layout.section_ex5_header)
                    .build());
            this.title = title;
            this.list = list;
        }

        @Override
        public int getContentItemsTotal() {
            return list.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            String name = list.get(position).getName();
            String category = list.get(position).getCategory();

            itemViewHolder.tvItem.setText(name);
            itemViewHolder.tvSubItem.setText(category);

            itemViewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), String.format("Clicked on position #%s of Section %s",
                            sectionedRecyclerViewAdapter.getPositionInSection(itemViewHolder.getAdapterPosition()), title),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            headerViewHolder.tvTitle.setText(title);

            headerViewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), String.format("Clicked on more button from the header of Section %s",
                            title),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final Button btnMore;

        HeaderViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tvTitle);
            btnMore = view.findViewById(R.id.btnMore);
        }
    }

    private class Movie {
        final String name;
        final String category;

        Movie(String name, String category) {
            this.name = name;
            this.category = category;
        }

        String getName() {
            return name;
        }

        String getCategory() {
            return category;
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final TextView tvItem;
        private final TextView tvSubItem;

        ItemViewHolder(View view) {
            super(view);

            rootView = view;
            tvItem = view.findViewById(R.id.tvItem);
            tvSubItem = view.findViewById(R.id.tvSubItem);
        }
    }
}
