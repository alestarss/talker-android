package ar.uba.fi.talker.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import ar.uba.fi.talker.CanvasActivity;
import ar.uba.fi.talker.R;
import ar.uba.fi.talker.utils.GridItems;
import ar.uba.fi.talker.utils.ScenarioView;

public class SceneActionFragment extends DialogFragment implements OnClickListener {

	private GridItems gridItem;
	private View view;
	private BaseAdapter adapter;

	public SceneActionFragment(GridItems gridItems, View view, BaseAdapter adapter) {
		this.gridItem = gridItems;
		this.view = view;
		this.adapter = adapter;
	}
	
	@Override
	public void dismiss() {
		this.view.setBackgroundColor(Color.WHITE);
		super.dismiss();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {		
		View actions = View.inflate(getActivity(), R.layout.scenario_actions, null);

		View backBttn = actions.findViewById(R.id.sceneActionBack);
		backBttn.setOnClickListener(this);
		View editNameScenarioBttn = actions.findViewById(R.id.new_scene_edit_scenario_name);
		editNameScenarioBttn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new ChangeNameDialogFragment(gridItem.getScenarioView(), adapter);
				newFragment.onAttach(getActivity());
				newFragment.show(getActivity().getSupportFragmentManager(), "insert_text");
				SceneActionFragment.this.dismiss();
			}
		});

		View startScenarioBttn = actions.findViewById(R.id.new_scene_start);
		startScenarioBttn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Bundle extras = new Bundle();
				ScenarioView scenarioView = gridItem.getScenarioView();
				if (scenarioView.getIdCode() == 0) {
					extras.putString("path", scenarioView.getPath());
				} else {
					extras.putInt("code", scenarioView.getIdCode());
				}
				Intent intent = new Intent(getActivity().getApplicationContext(), CanvasActivity.class);
				intent.putExtras(extras);
				startActivity(intent);
				SceneActionFragment.this.dismiss();
			}
		});
		
		View deleteScenarioBttn = actions.findViewById(R.id.new_scene_delete_scenario_name);
		deleteScenarioBttn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DeleteScenarioConfirmationDialogFragment(gridItem.getScenarioView());
				newFragment.onAttach(getActivity());
				newFragment.show(getActivity().getSupportFragmentManager(), "delete_scenario");
				SceneActionFragment.this.dismiss();
			}
		});

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(actions);
		return builder.create();
	}
	
	@Override
	public void onClick(View v) {
		this.dismiss();
	}
	
}
