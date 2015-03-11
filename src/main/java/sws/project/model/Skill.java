package sws.project.model;
/**
 *
 */

public class Skill extends Model{
    public enum Role{
        ScrumMaster,
        Developer,
        PO,
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object object){
        return object instanceof Skill && ((Skill)object).getLongName() == getLongName();
    }
}
