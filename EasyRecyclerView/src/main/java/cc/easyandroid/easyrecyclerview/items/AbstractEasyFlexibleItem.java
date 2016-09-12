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
package cc.easyandroid.easyrecyclerview.items;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import cc.easyandroid.easyrecyclerview.EasyFlexibleAdapter;

/**

 */
public abstract class AbstractEasyFlexibleItem<VH extends RecyclerView.ViewHolder>
        implements IFlexible<VH> {

    private static final String MAPPING_ILLEGAL_STATE = " is not implemented. If you want EasyFlexibleAdapter creates and binds ViewHolder for you, you must override and implement the method ";

    /* Item flags recognized by the FlexibleAdapter */
    protected boolean mEnabled = true;

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public int getLayoutRes() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException if called but not implemented
     */
    @Override
    public VH createViewHolder(EasyFlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        throw new IllegalStateException("onCreateViewHolder()" + MAPPING_ILLEGAL_STATE
                + this.getClass().getSimpleName() + ".createViewHolder().");
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException if called but not implemented
     */
    @Override
    public void bindViewHolder(EasyFlexibleAdapter adapter, VH holder, int position, List payloads) {
        throw new IllegalStateException("onBindViewHolder()" + MAPPING_ILLEGAL_STATE
                + this.getClass().getSimpleName() + ".bindViewHolder().");
    }

}