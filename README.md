EasyRecyclerView
==========

![License MIT](https://img.shields.io/badge/Apache-2.0-brightgreen)

## 效果图
![image](https://raw.githubusercontent.com/easyandroid-cc/EasyRecyclerView/master/images/1.gif)
![image](https://raw.githubusercontent.com/easyandroid-cc/EasyRecyclerView/master/images/2.gif)
![image](https://raw.githubusercontent.com/easyandroid-cc/EasyRecyclerView/master/images/3.gif)

![image](<img src="https://raw.githubusercontent.com/easyandroid-cc/EasyRecyclerView/master/images/1.gif" width="200" height="200"  /><br/>)
![image](<img src="https://raw.githubusercontent.com/easyandroid-cc/EasyRecyclerView/master/images/1.gif" width="200" height="200"  /><br/>)
![image](<img src="https://raw.githubusercontent.com/easyandroid-cc/EasyRecyclerView/master/images/1.gif" width="200"   /><br/>)
 
## 说明
EasyRecyclerView 包含下拉刷新，Adapter的封装,LoadingView EmptyView ErrorView三个界面的配置 

## Installation

Add it in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
         jcenter()
    }
}
```


Add the dependency
```
dependencies {
    implementation 'cc.easyandroid:EasyRecyclerView:1.5.1'
}
```

## How To Use

```java
public class YourListFragment extends Fragment {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listfragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EasyFlexibleAdapter  adapter = new EasyFlexibleAdapter();
        EasyRecyclerView recyclerView =   view.findViewById(R.id.relist);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
   
        recyclerView.setHeaderHander(new DefaultHeaderHander(getContext()));//设置刷新的view，可以自定义
        recyclerView.setFooterHander(new DefaultFooterHander(getContext()));//设置加载的view，可以自定义
       
        recyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {//刷新回调
                recyclerView.finishLoadMore(EasyRecyclerView.FooterHander.LOADSTATUS_COMPLETED);
                recyclerView.showLoadingView();
            }
        });

         recyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final EasyRecyclerView.FooterHander loadMoreView) {
                //加载回调
                recyclerView.finishRefresh(true);
            }
        });

         recyclerView.setAdapter(adapter);

    }
 
}

```

 
## 2. 调用

    //调用autoRefresh，他会立刻回调刷新方法，
    recyclerView.autoRefresh();


## 修改默认 loading empty error view

    <style name="easyRecyclerViewStyle">
        <item name="easyLoadingView">@layout/loadingview</item>
        <item name="easyEmptyView">@layout/emptyview</item>
        <item name="easyErrorView">@layout/errorview</item>
    </style>
License
-------

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


