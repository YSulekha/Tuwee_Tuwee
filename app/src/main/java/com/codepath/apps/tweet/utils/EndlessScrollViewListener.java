package com.codepath.apps.tweet.utils;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessScrollViewListener extends RecyclerView.OnScrollListener {

    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;
    private int startingPageIndex = 0;
    RecyclerView.LayoutManager mLayoutManager;

    public EndlessScrollViewListener(LinearLayoutManager manager){
        this.mLayoutManager = manager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int totalItemCount = mLayoutManager.getItemCount();
        int lastVisibleItemPos = 0;

        if(mLayoutManager instanceof LinearLayoutManager){
            lastVisibleItemPos = ((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition();
        }
        if(loading && totalItemCount > previousTotalItemCount){
            previousTotalItemCount = totalItemCount;
            loading = false;
        }
        if(!loading && lastVisibleItemPos+visibleThreshold > totalItemCount){
            currentPage++;
            onLoadMore(currentPage,totalItemCount,recyclerView);
            loading = true;
        }
    }

    public void resetState() {
        this.currentPage = this.startingPageIndex;
        this.previousTotalItemCount = 0;
        this.loading = true;
    }


    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);
}
