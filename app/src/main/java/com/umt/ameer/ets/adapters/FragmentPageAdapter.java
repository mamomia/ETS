package com.umt.ameer.ets.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.umt.ameer.ets.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ameer on 2/27/2016.
 */
public class FragmentPageAdapter extends SmartFragmentStatePagerAdapter {
    private Context mContext;

    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mFragmentTitles = new ArrayList<>();
    private List<Integer> mFragmentIcons = new ArrayList<>();

    public FragmentPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }

    public void addFragment(Fragment fragment, String title, int drawable) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
        mFragmentIcons.add(drawable);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }

    public View getTabView(int position) {
        View tab = LayoutInflater.from(mContext).inflate(R.layout.tabber_view, null);
        ImageView tabImage = (ImageView) tab.findViewById(R.id.tabImage);
        tabImage.setImageResource(mFragmentIcons.get(position));

        if (position == 0) {
            tab.setSelected(true);
        }

        return tab;
    }
}
