package cifradrive.fatec.br.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cifradrive.fatec.br.R;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ListViewHolder> {
    private String[] nomeDataset;
    private String[] extraDataset;
    private int[] ids;
    private GroupListHandler clicker;

    public GroupListAdapter(String[] mainText, String[] subText, int[] idsDataset, GroupListHandler handler) {
        nomeDataset = mainText;
        extraDataset = subText;
        ids = idsDataset;
        clicker = handler;
    }

    @Override
    public GroupListAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        ListViewHolder viewHolder = new ListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        holder.tv_nome.setText(nomeDataset[position]);
        holder.tv_extra.setText(extraDataset[position]);
    }

    @Override
    public int getItemCount() {
        return nomeDataset.length;
    }

    public interface GroupListHandler {
        void onClick(int i);
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_nome;
        public TextView tv_extra;

        public ListViewHolder(View view) {
            super(view);
            tv_nome = view.findViewById(R.id.list_item_nome);
            tv_extra = view.findViewById(R.id.list_item_extra);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            int id = ids[position];
            clicker.onClick(id);
        }
    }
}