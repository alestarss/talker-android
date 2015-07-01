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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.DialogFragment;
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
import ar.uba.fi.talker.dao.ContactDAO;
import ar.uba.fi.talker.dao.ImageDAO;
import ar.uba.fi.talker.dataSource.ContactTalkerDataSource;
import ar.uba.fi.talker.dataSource.ImageTalkerDataSource;
import ar.uba.fi.talker.dataSource.TalkerDataSource;
import ar.uba.fi.talker.dto.TalkerDTO;
import ar.uba.fi.talker.fragment.ContactDialogFragment;
import ar.uba.fi.talker.fragment.ScenesGridFragment;
import ar.uba.fi.talker.utils.GridItems;
import ar.uba.fi.talker.utils.GridUtils;
import ar.uba.fi.talker.utils.ImageUtils;

import com.viewpagerindicator.PageIndicator;

public class ImageSettingsActivity extends CommonImageSettingsActiviy {

    private final ImageTalkerDataSource imageDatasource;
    private final ContactTalkerDataSource contactDatasource;
	public PageIndicator pageIndicator;
	private ViewPager viewPager;
	private GridView gridView = null;
	private PagerScenesAdapter pagerAdapter;
	private int keyId;
	private boolean isContact;
	private static int RESULT_LOAD_IMAGE = 1;
	private static int RESULT_LOAD_IMAGE_CONTACT = 3;
	
	public ImageSettingsActivity() {
		imageDatasource = new ImageTalkerDataSource(this);
		contactDatasource = new ContactTalkerDataSource(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		keyId= b.getInt("keyId");
		isContact= b.getBoolean("isContact");
		setContentView(R.layout.layout_images);
		imagesPagerSetting();

		ImageButton createImageBttn = (ImageButton) this.findViewById(R.id.new_image_gallery);
		if (isContact){
			createImageBttn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogFragment newFragment = new ContactDialogFragment();
					newFragment.show(getSupportFragmentManager(), "insert_contact");
				}
			});			
		} else {
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

		List<ImageDAO> allImages = imageDatasource.getImagesForCategory(keyId);
		List<ScenesGridFragment> gridFragments = GridUtils.setScenesGridFragments(this, allImages, imageDatasource);

		pagerAdapter = new PagerScenesAdapter(getSupportFragmentManager(), gridFragments);
		viewPager.setAdapter(pagerAdapter);
		pageIndicator.setViewPager(viewPager);
	}

	@Override
	public void onDialogPositiveClickDeleteScenarioDialogListener(
			TalkerDTO scenarioView) {
		
		boolean deleted = true;
		if (scenarioView.getPath().contains("/")) {
			File file = new File(scenarioView.getPath());
			deleted = file.delete();
		}
		if (deleted){
			ImageDAO entity = new ImageDAO();
			entity.setId(scenarioView.getId());
			imageDatasource.delete(entity.getId());
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
				ImageUtils.saveFileInternalStorage(imageName, bitmap, this.getApplicationContext(),0);
				File file = new File(this.getApplicationContext().getFilesDir(), imageName);
				imageDAO = imageDatasource.createImage(file.getPath(), imageName, keyId);
				if (gridView == null ){
					setGridViewAdapter();
				}
				GridScenesAdapter gsa = (GridScenesAdapter) gridView.getAdapter();
				TalkerDTO elementGridView = new TalkerDTO();
				elementGridView.setId(imageDAO.getId());
				elementGridView.setName(imageDAO.getName());
				elementGridView.setPath(imageDAO.getPath());
				GridItems gridItem = new GridItems(imageDAO.getId(), elementGridView);
				gsa.add(gridItem);
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
				Media.getBitmap(this.getContentResolver(), imageUri);
				data.putExtra("imageUri", imageUri);
			} catch (FileNotFoundException e) {
				Log.e("SETTING", "Imagen no encontrada", e);
			} catch (IOException e) {
				Log.e("SETTING", "Error en la imagen", e);
			}
		}
		this.imagesPagerSetting();
	}

	private void setGridViewAdapter() {
		TalkerDTO element = new TalkerDTO();
		List<TalkerDTO> imageViews = new ArrayList<TalkerDTO>();
		imageViews.add(element);
		TalkerDataSource datasource = this.isContact ? contactDatasource : imageDatasource;
		
		List<ScenesGridFragment> gridFragments = GridUtils.setScenesGridFragments(this, imageViews, datasource);
		ScenesGridFragment sgf = gridFragments.get(0);
		GridScenesAdapter mGridAdapter = new GridScenesAdapter(this, sgf.getGridItems());
		mGridAdapter.setDao(datasource);
		gridView = new GridView(this);
		gridView.setAdapter(mGridAdapter);
	}

	@Override
	public void onDialogPositiveClickTextDialogListener(DialogFragment dialog) {
		Dialog dialogView = dialog.getDialog();
		ImageView imageView = (ImageView) dialogView.findViewById(R.id.contact_image);
		
		String name = ""+imageView.getId();
		String path = ""+imageView.getId();
	    ImageDAO imagedao= imageDatasource.createImage(path, name, keyId);

		EditText inputName = (EditText) dialogView.findViewById(R.id.insert_text_input_name);
		EditText inputAddress = (EditText) dialogView.findViewById(R.id.insert_text_input_address);
		EditText inputPhone = (EditText) dialogView.findViewById(R.id.insert_text_input_phone);

		ContactDAO contactDAO = new ContactDAO();
		contactDAO.setImageId(imagedao.getId());
		contactDAO.setPath(imagedao.getPath());
		contactDAO.setName(inputName.getText().toString());
		contactDAO.setAddress(inputAddress.getText().toString());
		contactDAO.setPhone(inputPhone.getText().toString());
		contactDatasource.add(contactDAO);
	}

	public boolean isContact() {
		return isContact;
	}

}
