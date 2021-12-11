package com.example.demo;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="room")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long maximumCapacity;

    @OneToMany(mappedBy = "conferenceRoom", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    //@JoinColumn(name="presentation_id", referencedColumnName = "id")
    private Set<Track> presentations;

    public Room(long maximumCapacity) {
        this.maximumCapacity = maximumCapacity;
    }

    public void update(Room room) {
        this.maximumCapacity = room.getMaximumCapacity();
    }
}
