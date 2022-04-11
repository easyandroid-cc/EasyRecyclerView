package cc.easyandroid.easyrecyclerview.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter
import cc.easyandroid.easyrecyclerview.EasyPullRefreshRecyclerView
import cc.easyandroid.easyrecyclerview.SelectableAdapter.MODE_MULTI
import cc.easyandroid.easyrecyclerview.demo.source.InMemoryByPageKeyRepository
import cc.easyandroid.easyrecyclerview.demo.viewmodel.*
import cc.easyandroid.easyrecyclerview.listener.OnEasyProgressClickListener
import com.android.example.paging.pagingwithnetwork.reddit.api.RedditApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest

class ListFragment_kt : Fragment() {
    private val viewModel: SubRedditViewModel by viewModels {
        object : AbstractSavedStateViewModelFactory(this, null) {
            override fun <T : ViewModel?> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
            ): T {
                val repo = InMemoryByPageKeyRepository(RedditApi.create())
                @Suppress("UNCHECKED_CAST")
                return SubRedditViewModel(repo, handle) as T
            }
        }
    }

    private lateinit var myPagingAdapter: PostsAdapter;// = PostsAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list_kt, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
            this.lifecycleScope.launchWhenCreated {
                viewModel.posts.collectLatest { it ->
                    myPagingAdapter.submitData(it)

                }
            }
            viewModel.showSubreddit("android")
            println("cgp onViewCreated ")

    }


    private fun initView(view: View) {
        myPagingAdapter = PostsAdapter();
        myPagingAdapter.mode = MODE_MULTI
        myPagingAdapter.initializeListeners(EasyFlexibleAdapter.OnItemClickListener { view: View, i: Int ->
             Toast.makeText(view.context, "$i", Toast.LENGTH_SHORT).show()
            true;
        })
        val list: EasyPullRefreshRecyclerView = view.findViewById(R.id.list1)
        list.setOnEasyProgressClickListener(object :OnEasyProgressClickListener{
            override fun onLoadingViewClick() {

            }

            override fun onEmptyViewClick() {

            }

            override fun onErrorViewClick() {
                myPagingAdapter.refresh()
            }

        })
//        loadState is LoadState.Loading || loadState is LoadState.Error
        myPagingAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {  //println("cgp refresh Loading")
                    list.showLoadingView()
                }
                is LoadState.NotLoading -> {//println("cgp refresh NotLoading")
                     list.finishRefresh(true)
                }
                is LoadState.Error -> {//println("cgp refresh Error")
                    list.showErrorView()
                    list.finishRefresh(false)
                }
            }
            when (it.append) {
                is LoadState.Loading -> {
                    //println("cgp append Loading")
                }
                is LoadState.NotLoading -> {
                    //println("cgp append NotLoading")
                }
                is LoadState.Error -> {
                   // println("cgp append Error")
                }
            }
            when (it.prepend) {
                is LoadState.Loading -> {
                   // println("cgp prepend Loading")
                }
                is LoadState.NotLoading -> {
                   // println("cgp prepend NotLoading")
                }
                is LoadState.Error -> {
                   // println("cgp prepend Error")
                }
            }
            when (it.prepend) {
                is LoadState.Loading -> {
                   // println("cgp prepend Loading")
                }
                is LoadState.NotLoading -> {
                    //println("cgp prepend NotLoading")
                }
                is LoadState.Error -> {
                  //  println("cgp prepend Error")
                }
            }
        }


        list.setOnRefreshListener {
            myPagingAdapter.refresh()

        }

        //list.adapter=myPagingAdapter
        val ada: ConcatAdapter = myPagingAdapter.withLoadStateHeaderAndFooter(
               header = RefreshAdapter2( ),
               footer = PostsLoadStateAdapter(myPagingAdapter)
        );
     //   myPagingAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
//        myPagingAdapter.stickySectionHeadersHolder
       //  ada.addAdapter(0, RefreshAdapter(myPagingAdapter))
//        ada.addAdapter(0, PostsLoadStateAdapter(myPagingAdapter))
        list.adapter = ada
//        list.adapter = myPagingAdapter
//        list.adapter = myPagingAdapter.withLoadStateHeaderAndFooter(
//                header = RefreshAdapter2( list),
//                footer = PostsLoadStateAdapter(myPagingAdapter)
//        )
//          .addAdapter(0,RefreshAdapter())
    }


}


