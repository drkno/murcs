package sws.project.controller;

import java.util.Observable;

/**
 * Created by James on 13/03/2015.
 */
public enum ModelTypes {

    Project,
    People,
    Team,
    Skills;

    public static ModelTypes getModelType(int index) {
        return values()[index];
    }
}
