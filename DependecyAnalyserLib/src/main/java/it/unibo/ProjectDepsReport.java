package it.unibo;

import java.util.ArrayList;
import java.util.List;

public class ProjectDepsReport implements Report{

    private ArrayList<String> projectDependencies;
    @Override
    public List<String> getList() {
        return projectDependencies;
    }

    @Override
    public void setList(ArrayList<String> declarations) {
        this.projectDependencies = declarations;
    }
}
