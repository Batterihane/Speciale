package Utilities.DataObjects;

import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Reference;

/**
 * Created by Thomas on 11-03-2016.
 */
public class MASTNodeData extends Reference {
    private int miNumber;
    private PhylogenyNode twin;

    public PhylogenyNode getTwin() {
        return twin;
    }

    public void setTwin(PhylogenyNode twin) {
        this.twin = twin;
    }

    public int getMiNumber() {
        return miNumber;
    }

    public void setMiNumber(int miNumber) {
        this.miNumber = miNumber;
    }

    public MASTNodeData() {
        super("");
    }
}
