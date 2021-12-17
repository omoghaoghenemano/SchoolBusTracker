package Fragments;

import java.util.Comparator;

public class CompareDist {
    public int distance;
    public  String namesof;


    public CompareDist(int distance, String namesof) {
        this.distance = distance;
        this.namesof = namesof;
    }
    public static Comparator<CompareDist> ascendingorder = new Comparator<CompareDist>() {
        @Override
        public int compare(CompareDist compareDist, CompareDist t1) {
            return compareDist.getDistance()-t1.getDistance();
        }
    };
    @Override
    public String toString(){
        return "name"+namesof+"distance"+distance;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getNamesof() {
        return namesof;
    }

    public void setNamesof(String namesof) {
        this.namesof = namesof;
    }
}
