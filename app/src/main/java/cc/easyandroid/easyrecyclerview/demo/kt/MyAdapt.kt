package cc.easyandroid.easyrecyclerview.demo.kt

import android.os.Parcel
import android.os.Parcelable
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter
import cc.easyandroid.easyrecyclerview.items.IFlexible
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

open class MyAdapt<T : Any, VH : RecyclerView.ViewHolder>(pagingDataAdapter: PagingDataAdapter<T,VH>)      {

}

