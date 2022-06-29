package ru.croc.ugd.ssr.model.api.chess;

import static java.util.Objects.isNull;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@NoArgsConstructor
public class FamilyResponse {

    private String affairId;
    private String roomsNum;
    private String address;
    private String link;
    private int resStatus;
    private List<PeopleResponse> peoples;

    public String getAffairId() {
        return affairId;
    }

    public String getRoomsNum() {
        return roomsNum;
    }

    public String getAddress() {
        return address;
    }

    public String getLink() {
        return link;
    }

    public int getResStatus() {
        return resStatus;
    }

    public List<PeopleResponse> getPeoples() {
        if (isNull(peoples)) {
            peoples = new ArrayList<>();
        }
        return peoples;
    }
}
