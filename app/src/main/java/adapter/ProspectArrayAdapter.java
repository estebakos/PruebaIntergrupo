package adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import model.Prospect;
import prueba.intergrupo.com.view.AdapterListener;
import prueba.intergrupo.com.view.R;

public class ProspectArrayAdapter extends ArrayAdapter<Prospect> {
	private final Context context;
	private final List<Prospect> lProspect;
    private AdapterListener adapterListener;

	public ProspectArrayAdapter(Context context, List<Prospect> lProspect, AdapterListener adapterListener) {
		super(context, R.layout.prospect_item_list, lProspect);
		this.context = context;
		this.lProspect = lProspect;
        this.adapterListener = adapterListener;
	}


	public static float convertPixelsToDp(float px) {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	public static float convertDpToPixel(float dp) {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = null;
		
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowView = inflater.inflate(R.layout.prospect_item_list, parent, false);
		TextView tvName = (TextView) rowView.findViewById(R.id.tvName);
		TextView tvDocument = (TextView) rowView
				.findViewById(R.id.tvDocument);
		TextView tvPhoneNumber = (TextView) rowView
				.findViewById(R.id.tvPhoneNumber);
		ImageView ivStatus = (ImageView) rowView.findViewById(R.id.ivStatus);
        ImageButton ibEditProspect = (ImageButton)rowView.findViewById(R.id.btnEditProspect);
        ibEditProspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterListener.onEditClick(lProspect.get(position));
            }
        });

		RelativeLayout rlHolder = (RelativeLayout) rowView
				.findViewById(R.id.layout_item_prospect);

		rlHolder.getLayoutParams().height = (int) convertDpToPixel(100);
		parent.requestLayout();

		//Depende del estado se elige un �cono
		switch (lProspect.get(position).getEstado())
		{
			case 0:
				ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_access_alarm_black_36dp));
				break;
			case 1:
				ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_black_36dp));
				break;
			case 2:
				ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_text_format_black_36dp));
				break;
			case 3:
				ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_highlight_off_black_36dp));
				break;
			case 4:
				ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_notifications_off_black_36dp));
				break;
			default:
				ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_notifications_active_white_48dp));
				break;
		}
		tvName.setText(lProspect.get(position).getNombre() + " " + lProspect.get(position).getApellido());
		tvDocument.setText("C.C.: " +lProspect.get(position).getCedula());
		tvPhoneNumber.setText("Tel: "+lProspect.get(position).getTelefono());
		return rowView;
	}
}
