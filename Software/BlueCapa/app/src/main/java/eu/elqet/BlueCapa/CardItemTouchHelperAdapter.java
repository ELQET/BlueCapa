package eu.elqet.BlueCapa;

/**
 * Created by Matej Baran on 13.4.2016.
 */
public interface CardItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
