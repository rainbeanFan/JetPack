package cn.rainbean.jetpack.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import cn.rainbean.basemodule.extention.AbsPagedListAdapter;
import cn.rainbean.jetpack.module.Feed;

public class FeedAdapter extends AbsPagedListAdapter<Feed,FeedAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    protected Context mContext;
    protected String mCategory;

    protected FeedAdapter(Context context, String category) {
        super(new DiffUtil.ItemCallback<Feed>() {
            @Override
            public boolean areItemsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.equals(newItem);
            }
        });

        inflater = LayoutInflater.from(context);
        mContext = context;
        mCategory = category;
    }

    @Override
    protected int getItemViewType2(int position) {
        Feed feed = getItem(position);
        assert feed != null;
        if (feed.itemType == Feed.TYPE_IMAGE_TEXT){

        }
        return 0;
    }

    @Override
    protected ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, viewType, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    protected void onBindViewHolder2(ViewHolder holder, int position) {
        Feed feed = getItem(position);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView,ViewDataBinding binding) {
            super(itemView);
        }
    }

}
