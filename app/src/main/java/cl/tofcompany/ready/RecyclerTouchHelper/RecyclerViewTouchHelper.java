package cl.tofcompany.ready.RecyclerTouchHelper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import cl.tofcompany.ready.Adapter.ToDoAdapter;
import cl.tofcompany.ready.R;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RecyclerViewTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ToDoAdapter adapter;

    public RecyclerViewTouchHelper(ToDoAdapter adapter) {
        super(0 , ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView , @NonNull RecyclerView.ViewHolder viewHolder , @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder , int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.RIGHT) {
            adapter.editItem(position);
        } else {
            adapter.editItem(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c , @NonNull RecyclerView recyclerView , @NonNull RecyclerView.ViewHolder viewHolder , float dX , float dY , int actionState , boolean isCurrentlyActive) {

        new RecyclerViewSwipeDecorator.Builder(c , recyclerView , viewHolder , dX , dY , actionState , isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(adapter.getContext() , R.color.colorPrimaryDark))
                .addSwipeLeftActionIcon(R.drawable.ic_baseline_edit)
                .addSwipeRightBackgroundColor(ContextCompat.getColor(adapter.getContext() , R.color.colorPrimaryDark))
                .addSwipeRightActionIcon(R.drawable.ic_baseline_edit)
                .create()
                .decorate();
        super.onChildDraw(c , recyclerView , viewHolder , dX , dY , actionState , isCurrentlyActive);
    }
}
