package com.example.demo;

import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="track")
@Setter
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="description")
    private String description;

    @OneToOne
    @JoinColumn(name="speaker_id", referencedColumnName = "id")
    private Person speaker;

    @ManyToMany
    @JoinTable(name="attendee", joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns =@JoinColumn(name = "person_id"))
    private Set<Person> attendees;

    @ManyToOne
    //@JoinColumn(name="presentation_id", referencedColumnName = "id")
    private Room conferenceRoom;

    public Track() {

    }
    public Track(String title, String description, Person speaker, Room conferenceRoom) {
        this.title = title;
        this.description = description;
        this.speaker = speaker;
        this.conferenceRoom = conferenceRoom;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Person getSpeaker() {
        return speaker;
    }

    public Set<Person> getAttendees() {
        return attendees;
    }

    public Room getConferenceRoom() {
        return conferenceRoom;
    }

    public void update(Track track) {
        this.description = track.getDescription();
        this.title = track.getTitle();
        this.speaker = track.getSpeaker();
        this.conferenceRoom = track.getConferenceRoom();
    }
}
