package ca.yorku.eecs.models;

import java.util.List;

public class ActorModel extends BaseModel {

    private String name;
    private List<MovieModel> movies;

    public ActorModel() {
        this.name = "";
        this.id = "";
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MovieModel> getMovies() {
        return this.movies;
    }

    public void setMovies(List<MovieModel> movies) {
        this.movies = movies;
    }

}
