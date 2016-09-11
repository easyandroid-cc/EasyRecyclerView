/*
 * Copyright 2016 Davide Steduto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.easyandroid.easyrecyclerview.demo.text;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * 自定义的ViewHolder
 * @param <VH>
 */
  interface IFlexible<VH extends RecyclerView.ViewHolder> {

    /**
     * Returns if the Item is enabled.
     *
     * @return (default) true for enabled item, false for disabled one.
     */
    boolean isEnabled();

    /**
     * Setter to change enabled behaviour.
     *
     * @param enabled false to disable all operations on this item
     */
    void setEnabled(boolean enabled);

	@LayoutRes
	int getLayoutRes();


	VH createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent);


	void bindViewHolder(FlexibleAdapter adapter, VH holder, int position, List payloads);

}