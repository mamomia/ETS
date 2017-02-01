package com.umt.ameer.ets.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by Ameer on 2/27/2016.
 */
public abstract class SmartFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    // Sparse array to keep custom_track_switch of registered fragments in memory
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private Fragment mCurrentFragment;

    public SmartFragmentStatePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // Returns the fragment for the position (if instantiated)
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }
}
