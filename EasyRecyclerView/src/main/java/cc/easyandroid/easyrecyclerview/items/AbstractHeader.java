package cc.easyandroid.easyrecyclerview.items;

/**
 * Created by cgpllx on 2016/9/12.
 */
public class AbstractHeader extends AbstractEasyFlexibleItem implements IHeader {

    public AbstractHeader() {
    }

    @Override
    public boolean isSticky() {
        return false;
    }
}
