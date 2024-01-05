package myschoolapp.com.gsnedutech.descriptive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.Collections;
import java.util.List;

import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.descriptive.models.StudentAnswer;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements ItemMoveCallback.ItemTouchHelperAdapter {

    private Context context;
    private List<StudentAnswer> arrayList;

    public RecyclerViewAdapter(Context context, List<StudentAnswer> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public  ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_desc_files, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public  void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(context).load(arrayList.get(position).getPath()).into(holder.myText);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for  (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(arrayList, i, i + 1);
            }
        } else {
            for  (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(arrayList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onRowSelected(ViewHolder holder) {

    }
    @Override
    public void onRowClear(ViewHolder holder) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView myText;
        public ViewHolder(View itemView) {
            super(itemView);
            myText = itemView.findViewById(R.id.iv_file);
        }
    }
}
