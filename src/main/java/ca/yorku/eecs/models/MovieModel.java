package ca.yorku.eecs.models;

import java.util.List;

public class MovieModel extends BaseModel {

    private String name;
    private List<ActorModel> actors;

    public MovieModel() {
        this.name = "";
        this.id = "";
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public List<ActorModel> getActors() {
        return this.actors;
    }

    public void setActors(List<ActorModel> actors) {
        this.actors = actors;
    }
}
