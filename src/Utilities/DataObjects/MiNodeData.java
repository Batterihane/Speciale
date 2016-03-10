package Utilities.DataObjects;

import org.forester.phylogeny.data.Reference;

/**
 * Created by Nikolaj on 10-03-2016.
 */
public class MiNodeData extends Reference {
    public int getMiNumber() {
        return miNumber;
    }

    public void setMiNumber(int miNumber) {
        this.miNumber = miNumber;
    }

    private int miNumber;

    public MiNodeData() {
        super("");
    }
}