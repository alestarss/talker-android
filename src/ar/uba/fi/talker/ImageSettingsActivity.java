package ar.uba.fi.talker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import ar.uba.fi.talker.adapter.GridScenesAdapter;
import ar.uba.fi.talker.adapter.PagerScenesAdapter;
import ar.uba.fi.talker.dao.ContactTalkerDataSource;
import ar.uba.fi.talker.dao.ImageDAO;
import ar.uba.fi.talker.dao.ImageTalkerDataSource;
import ar.uba.fi.talker.fragment.ContactDialogFragment;
import ar.uba.fi.talker.fragment.ContactDialogFragment.ContactDialogListener;
import ar.uba.fi.talker.fragment.DeleteScenarioConfirmationDialogFragment.DeleteScenarioDialogListener;
import ar.uba.fi.talker.fragment.ScenesGridFragment;
import ar.uba.fi.talker.utils.ElementGridView;
import ar.uba.fi.talker.utils.GridItems;
import ar.uba.fi.talker.utils.GridUtils;
import ar.uba.fi.talker.utils.ImageUtils;

import com.viewpagerindicator.PageIndicator;

public class ImageSettingsActivity extends FragmentActivity implements DeleteScenarioDialogListener, ContactDialogListener {

    private ImageTalkerDataSource imageDatasource;
    private ContactTalkerDataSource contactDatasource;
	public PageIndicator pageIndicator;
	private ViewPager viewPager;
	private GridView gridView = null;
	private PagerScenesAdapter pagerAdapter;
	private int keyId;
	private boolean isContact;
	private static int RESULT_LOAD_IMAGE = 1;
	private static int RESULT_LOAD_IMAGE_CONTACT = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		keyId= b.getInt("keyId");
		isContact= b.getBoolean("isContact");
		if (isContact){
			setContentView(R.layout.layout_contacts);
			imagesPagerSetting();

			ImageButton createContactBttn = (ImageButton) this.findViewById(R.id.add_contact);
			createContactBttn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogFragment newFragment = new ContactDialogFragment();
					newFragment.show(getSupportFragmentManager(), "insert_contact");
				}
			});			
		} else {
			setContentView(R.layout.layout_images);
			imagesPagerSetting();

			ImageButton createImageBttn = (ImageButton) this.findViewById(R.id.new_image_gallery);
			createImageBttn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						ScenesGridFragment sgf = pagerAdapter.getItem(viewPager.getCurrentItem());
						gridView = sgf.getmGridView();
					} catch (Exception e) {
						Log.i("newImage", "Agrego una imagen a una nueva categoria");
					}
					Intent i = new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
					startActivityForResult(i, RESULT_LOAD_IMAGE);
				}
			});
		}
	}

	private void imagesPagerSetting() {
		viewPager = (ViewPager) this.findViewById(R.id.pager);
		pageIndicator = (PageIndicator) this.findViewById(R.id.pagerIndicator);
		ArrayList<ElementGridView> thumbnails = new ArrayList<ElementGridView>();

		imageDatasource = new ImageTalkerDataSource(this);
	    imageDatasource.open();
		List<ImageDAO> allImages = imageDatasource.getImagesForCategory(keyId);
		ElementGridView thumbnail = null;
		for (ImageDAO imageDAO : allImages) {
			thumbnail = new ElementGridView();
			thumbnail.setId(imageDAO.getId());
			thumbnail.setName(imageDAO.getName());
			thumbnail.setPath(imageDAO.getPath());
			thumbnails.add(thumbnail);
		}
		List<ScenesGridFragment> gridFragments = GridUtils.setScenesGridFragments(this, thumbnails);

		pagerAdapter = new PagerScenesAdapter(getSupportFragmentManager(), gridFragments);
		viewPager.setAdapter(pagerAdapter);
		pageIndicator.setViewPager(viewPager);
	}

	@Override
	public void onDialogPositiveClickDeleteScenarioDialogListener(
			ElementGridView scenarioView) {
		
		boolean deleted = true;
		if (scenarioView.getPath().contains("/")) {
			File file = new File(scenarioView.getPath());
			deleted = file.delete();
		}
		if (deleted){
			imageDatasource.open();
			imageDatasource.deleteImage(scenarioView.getId());
			imageDatasource.close();
		} else {
			Toast.makeText(this, "Ocurrio un error con la imagen.",	Toast.LENGTH_SHORT).show();
			Log.e("NewScene", "Unexpected error deleting imagen.");
		}
		imagesPagerSetting();
	}
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ImageDAO imageDAO = null;
		if (requestCode == RESULT_LOAD_IMAGE && null != data) {
			Uri imageUri = data.getData();
			String imageName = imageUri.getLastPathSegment(); 
	        Bitmap bitmap = null;
			try {/*Entra al if cuando se elige una foto de google +*/
				if (imageUri != null && imageUri.getHost().contains("com.google.android.apps.photos.content")){
					InputStream is = getContentResolver().openInputStream(imageUri);
					bitmap = BitmapFactory.decodeStream(is);
					imageName = imageName.substring(35);
				} else {
					bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
				}
				ImageUtils.saveFileInternalStorage(imageName, bitmap, this.getApplicationContext());
				File file = new File(this.getApplicationContext().getFilesDir(), imageName);
				if (imageDatasource == null){
						imageDatasource = new ImageTalkerDataSource(this.getApplicationContext());
				}
				imageDatasource.open();
				imageDAO = imageDatasource.createImage(file.getPath(), imageName, keyId);
				imageDatasource.close();
				if (gridView == null ){
					setGridViewAdapter();
				}
				GridScenesAdapter gsa = (GridScenesAdapter) gridView.getAdapter();
				ElementGridView elementGridView = new ElementGridView();
				elementGridView.setId(imageDAO.getId());
				elementGridView.setName(imageDAO.getName());
				elementGridView.setPath(imageDAO.getPath());
				GridItems gridItem = new GridItems(imageDAO.getId(), elementGridView);
				gsa.addItem(gridItem);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (requestCode == RESULT_LOAD_IMAGE_CONTACT && null != data){
			Uri imageUri = data.getData();
			String imageName = imageUri.getLastPathSegment();
			if (imageUri != null && imageUri.getHost().contains("com.google.android.apps.photos.content")){
				imageName = imageName.substring(35);
			}
			try {
				Bitmap ima1 = Media.getBitmap(this.getContentResolver(), imageUri);
				data.putExtra("imageUri", imageUri);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//scenario.addImage(Bitmap.createBitmap(ima1, 0, 0, ima1.getWidth(), ima1.getHeight(), matrix, true), null);
		}
		this.imagesPagerSetting();
	}

	private void setGridViewAdapter() {
		ElementGridView element = new ElementGridView();
		List<ElementGridView> imageViews = new ArrayList<ElementGridView>();
		imageViews.add(element);
		List<ScenesGridFragment> gridFragments = GridUtils.setScenesGridFragments(this, imageViews);
		ScenesGridFragment sgf = gridFragments.get(0);
		GridScenesAdapter mGridAdapter = new GridScenesAdapter(this, sgf.getGridItems());
		gridView = new GridView(this);
		gridView.setAdapter(mGridAdapter);
	}

	@Override
	public void onDialogPositiveClickTextDialogListener(DialogFragment dialog) {
		Dialog dialogView = dialog.getDialog();
		ImageView imageView = (ImageView) dialogView.findViewById(R.id.image);
		Drawable drawable = imageView.getDrawable();
		String name = ""+imageView.getId();
		String path = ""+imageView.getId();
		if (imageDatasource == null ) {
			imageDatasource = new ImageTalkerDataSource(this);
		}
	    imageDatasource.open();
	    ImageDAO imagedao= imageDatasource.createImage(path, name, keyId);
		imageDatasource.close();
		
		EditText inputAddress = (EditText) dialogView.findViewById(R.id.insert_text_input);
		EditText inputPhone = (EditText) dialogView.findViewById(R.id.insert_text_input_phone);
		if (contactDatasource == null ) {
			contactDatasource = new ContactTalkerDataSource(this);
		}
	    contactDatasource.open();
		contactDatasource.createContact(imagedao.getId(), inputPhone.getText().toString(), inputAddress.getText().toString());
		contactDatasource.close();
	}

}
