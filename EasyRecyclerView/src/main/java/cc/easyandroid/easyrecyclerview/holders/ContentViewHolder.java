package cc.easyandroid.easyrecyclerview.holders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter;

/**
 * This Class separates the initialization of an eventual StickyHeader ViewHolder from a Normal
 * ViewHolder. It improves code readability of FlexibleViewHolder.
 */
abstract class ContentViewHolder extends RecyclerView.ViewHolder {

	private int mBackupPosition = RecyclerView.NO_POSITION;
	private View contentView;

	/**
	 * @param view         The {@link View} being hosted in this ViewHolder
	 * @param adapter      Adapter instance of type {@link EasyFlexibleAdapter}
	 * @param stickyHeader true if the ViewHolder is a header to be sticky
	 * @since 5.0.0-b7
	 */
	public ContentViewHolder(View view, EasyFlexibleAdapter adapter, boolean stickyHeader) {
		//Since itemView is declared "final", the split is done before the View is initialized
		super(stickyHeader ? new FrameLayout(view.getContext()) : view);

		if (stickyHeader) {
			itemView.setLayoutParams(adapter.getRecyclerView().getLayoutManager()
					.generateLayoutParams(view.getLayoutParams()));
			((FrameLayout) itemView).addView(view);//Add View after setLayoutParams
			contentView = view;
		}
	}

	/*-----------------------*/
	/* STICKY HEADER METHODS */
	/*-----------------------*/

	/**
	 * In case this ViewHolder represents a Header Item, this method returns the contentView of the
	 * FrameLayout, otherwise it returns the basic itemView.
	 *
	 * @return the real contentView
	 * @since 5.0.0-b7
	 */
	public View getContentView() {
		return contentView != null ? contentView : itemView;
	}

	/**
	 换成 getAbsoluteAdapterPosition 解决adapter 嵌套问题
	 嵌套adapter onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads)  这里的position 并不是item 在列表中的位置 ,他在在他自己的adapter 中的位置
	 */
	public int getFlexibleAdapterPosition() {
		int position = getBindingAdapterPosition();
		if (position == RecyclerView.NO_POSITION) {
			position = mBackupPosition;
		}
		return position;
	}

	/**
	 * Restore the Adapter position if the original Adapter position is unknown.
	 * <p>Called by StickyHeaderHelper to support the clickListeners events.</p>
	 *
	 * @param backupPosition the known position of this ViewHolder
	 * @since 5.0.0-b6
	 */
	public void setBackupPosition(int backupPosition) {
		mBackupPosition = backupPosition;
	}

}