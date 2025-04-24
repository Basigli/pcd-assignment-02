package it.unibo;

import java.util.ArrayList;
import java.util.List;

public class PackageDepsReport implements Report{
    private ArrayList<String> packageDependencies;
    @Override
    public List<String> getList() {
        return packageDependencies;
    }

    @Override
    public void setList(ArrayList<String> declarations) {
        this.packageDependencies = declarations;
    }

}
