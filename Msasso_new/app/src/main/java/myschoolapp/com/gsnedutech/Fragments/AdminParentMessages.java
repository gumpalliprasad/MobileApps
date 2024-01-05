package myschoolapp.com.gsnedutech.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.AdminMessages;
import myschoolapp.com.gsnedutech.MessageChatActivity;
import myschoolapp.com.gsnedutech.Models.AdminObj;
import myschoolapp.com.gsnedutech.Models.MsgDetails;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminParentMessages extends Fragment {
    View v;
    Unbinder unbinder;
    @BindView(R.id.search)
    SearchView search;

    @BindView(R.id.rv_msg_list)
    RecyclerView rvMsgList;
    @BindView(R.id.tv_no_messages)
    TextView tvNoMessages;
    List<MsgDetails> listMessages = new ArrayList<>();
    ChatAdapter chatAdapter;
    SharedPreferences sh_Pref;
    AdminObj adminObj;

    public AdminParentMessages() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_admin_messages, container, false);
        unbinder = ButterKnife.bind(this, v);

        sh_Pref = getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("adminObj", "");

        adminObj = gson.fromJson(json, AdminObj.class);

//        showSkeleton();
        listMessages = ((AdminMessages) getActivity()).getMessageList();
        if (listMessages.size() > 0) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rvMsgList.setVisibility(View.VISIBLE);
//                    shimmerSkeleton.setVisibility(View.GONE);
//                    skeletonLayout.setVisibility(View.GONE);
                    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                    rvMsgList.setLayoutManager(manager);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvMsgList.getContext(),
                            manager.getOrientation());
                    rvMsgList.addItemDecoration(dividerItemDecoration);
                    chatAdapter = new ChatAdapter(listMessages);
                    rvMsgList.setAdapter(chatAdapter);
                    search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            chatAdapter.getFilter().filter(query);
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            chatAdapter.getFilter().filter(newText);
                            return false;
                        }
                    });
                }
            }, 2000);
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rvMsgList.setVisibility(View.GONE);
//                    shimmerSkeleton.setVisibility(View.GONE);
//                    skeletonLayout.setVisibility(View.GONE);
                    tvNoMessages.setVisibility(View.VISIBLE);
                }
            }, 2000);
        }
        return v;
    }

//    private void showSkeleton() {
//        rvMsgList.setVisibility(View.GONE);
//        shimmerSkeleton.setVisibility(View.VISIBLE);
//        skeletonLayout.removeAllViews();
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.skeleton_msg_threads, null, false);
//        skeletonLayout.addView(view);
//        shimmerSkeleton.startShimmerAnimation();
//        skeletonLayout.setVisibility(View.VISIBLE);
//        skeletonLayout.bringToFront();
//    }

    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> implements Filterable {
        List<MsgDetails> listMsgDetails;
        List<MsgDetails> list_filtered;

        ChatAdapter(List<MsgDetails> listMsgDetails) {
            this.listMsgDetails = listMsgDetails;
            this.list_filtered = listMsgDetails;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_msg_thread, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.tvName.setText(list_filtered.get(i).getUserName());
            viewHolder.tvMessgae.setText(list_filtered.get(i).getMessage());
            String user = list_filtered.get(i).getUserName().charAt(0) + "";
            viewHolder.tvProfileImage.setText(user.toUpperCase());
            String Date = list_filtered.get(i).getCreatedDate();
            String[] datetime = Date.split(" ");
            String[] time = datetime[1].split(":");
            String t = "";
            if (Integer.parseInt(time[0]) < 12) {
                t = time[0] + ":" + time[1] + "AM";
            } else if (Integer.parseInt(time[0]) > 12) {
                int x = Integer.parseInt(time[0]) - 12;
                t = x + ":" + time[1] + "PM";
            } else {
                t = time[0] + time[1] + "PM";
            }
            viewHolder.tvDate.setText(datetime[0] + "\n" + t);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MessageChatActivity.class);
                    intent.putExtra("userName", list_filtered.get(i).getUserName());
                    intent.putExtra("threadId", list_filtered.get(i).getThreadId() + "");
                    intent.putExtra("userId", list_filtered.get(i).getUserId() + "");
                    intent.putExtra("createdBy", "" + adminObj.getUserId());
                    intent.putExtra("newMessage", "0");
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list_filtered.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String string = constraint.toString();
                    Log.v("TAG", string);
                    if (string.isEmpty()) {
                        list_filtered = listMsgDetails;
                    } else {
                        List<MsgDetails> filteredList = new ArrayList<>();
                        for (MsgDetails s : listMsgDetails) {
                            if (s.getUserName().toLowerCase().contains(string.toLowerCase()) || s.getUserName().toLowerCase().contains(string.toLowerCase())) {
                                Log.v("TAG", s.getUserName() + " " + string);
                                filteredList.add(s);
                            }
                        }
                        list_filtered = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = list_filtered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    list_filtered = (List<MsgDetails>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDate, tvProfileImage, tvMessgae;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_name);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvProfileImage = itemView.findViewById(R.id.tv_profile);
                tvMessgae = itemView.findViewById(R.id.tv_message);
            }
        }
    }
}
