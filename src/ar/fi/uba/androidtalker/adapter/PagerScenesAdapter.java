package ar.fi.uba.androidtalker.adapter;

import java.util.List;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import ar.fi.uba.androidtalker.fragment.ScenesGridFragment;

public class PagerScenesAdapter extends FragmentStatePagerAdapter {
	
	private List<ScenesGridFragment> scenesFragments;

	public PagerScenesAdapter(FragmentManager fm, List<ScenesGridFragment> scenesFragments) {
		super(fm);
		this.scenesFragments = scenesFragments;
	}

	@Override
	public ScenesGridFragment getItem(int position) {
		return this.scenesFragments.get(position);
	}

	@Override
	public int getCount() {
		return this.scenesFragments.size();
	}
}
