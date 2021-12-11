package com.example.demo;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private  PersonRepository personRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private  RoomRepository roomRepository;

    @GetMapping(value = "/persons", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Person>> getPersons() {
        Collection<Person> persons = (Collection<Person>) personRepository.findAll();
        if (!persons.isEmpty()) {
            return ResponseEntity.ok(persons);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(value ="/persons/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> getPerson(@PathVariable("id") long id) {
        Optional<Person> person = personRepository.findById(id);
        if(person.isPresent()) {
            return ResponseEntity.ok(person.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(path = "/persons", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> addPerson(String firstName, String lastName) {
        Person person = new Person(firstName, lastName);
        personRepository.save(person);
        URI uri = WebMvcLinkBuilder.linkTo(MainController.class).slash("persons").slash(person.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "/persons/{id}")
    public ResponseEntity<Void> changePerson(@PathVariable("id") long id, @RequestBody Person entity) {
        Optional<Person> person = personRepository.findById(id);
        if(person.isPresent()) {
            person.get().update(entity);
            personRepository.save(person.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/persons/{id}")
    public ResponseEntity<Void> removePerson(@PathVariable("id") long id) {
        Optional<Person> person = personRepository.findById(id);
        if(person.isPresent()) {
            personRepository.delete(person.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path ="/persons/{personId}/tracks")
    public ResponseEntity<Set<Track>> getPersonTracks(@PathVariable("personId") long personId) {
        Optional<Person> person = personRepository.findById(personId);
        if(person.isPresent()) {
            Set<Track> tracks = person.get().getTracks();
            if (!tracks.isEmpty()) {
                return ResponseEntity.ok(tracks);
            } else {
                return ResponseEntity.noContent().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(path="/persons/{personId}/tracks/{trackId}")
    public ResponseEntity<List<String>> addPersonTrack(@PathVariable("personId") long personId,
                                                       @PathVariable("trackId") long trackId) {
        Optional<Person> person = personRepository.findById(personId);
        if (person.isPresent()) {
            Optional<Track> track = trackRepository.findById(trackId);
            if (track.isPresent() && track.get().getSpeaker().getId() != personId) {
                person.get().getTracks().add(track.get());
                personRepository.save(person.get());
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path="/persons/{personId}/tracks/{trackId}")
    public ResponseEntity<List<String>> removePersonTrack(@PathVariable("personId") long personId,
                                                       @PathVariable("trackId") long trackId) {
        Optional<Person> person = personRepository.findById(personId);
        if (person.isPresent()) {
            Optional<Track> track = trackRepository.findById(trackId);
            if (track.isPresent() && track.get().getSpeaker().getId() != personId) {
                person.get().removeTrack(track.get());
                personRepository.save(person.get());
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/tracks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Track>> getTracks() {
        Collection<Track> tracks = (Collection<Track>) trackRepository.findAll();
        if(!tracks.isEmpty()) {
            return ResponseEntity.ok(tracks);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping(path = "/tracks/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Track> getTrack(@PathVariable("id") long id) {
        Optional<Track> track = trackRepository.findById(id);
        if (track.isPresent()) {
            return ResponseEntity.ok(track.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(path = "/tracks", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> addTrack(String title, String description, long speakerId, long conferenceRoomId) {
        Optional<Person> speaker = personRepository.findById(speakerId);
        Optional<Room> room = roomRepository.findById(conferenceRoomId);
        if(speaker.isPresent()) {
            if(room.isPresent()) {
                Track track = new Track(title, description, speaker.get(), room.get());
                trackRepository.save(track);
                URI uri = WebMvcLinkBuilder.linkTo(MainController.class).slash("tracks").slash(track.getId()).toUri();
                return ResponseEntity.created(uri).build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/tracks/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changeTrack(@PathVariable("id") long id, @RequestBody Track track) {
        Optional<Track> existingTrack = trackRepository.findById(id);
        if(existingTrack.isPresent()) {
            existingTrack.get().update(track);
            trackRepository.save(existingTrack.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/tracks/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteTrack(@PathVariable("id") long id) {
        Optional<Track> existingTrack = trackRepository.findById(id);
        if(existingTrack.isPresent()) {
            trackRepository.delete(existingTrack.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path="/tracks/{trackId}/attendees")
    public ResponseEntity<Set<Person>> getTrackAttendees(@PathVariable("trackId") long trackId) {
        Optional<Track> existingTrack = trackRepository.findById(trackId);
        if(existingTrack.isPresent()) {
           Set<Person> attendees = existingTrack.get().getAttendees();
            if(!attendees.isEmpty()) {
                return ResponseEntity.ok(attendees);
            }
            else {
                return ResponseEntity.noContent().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Room>> getRooms() {
        Collection<Room> rooms = (Collection<Room>) roomRepository.findAll();
        if(!rooms.isEmpty()) {
            return ResponseEntity.ok(rooms);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(path = "/rooms", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> addRoom(long maximumCapacity) {
        Room room = new Room(maximumCapacity);
        roomRepository.save(room);
        URI uri = WebMvcLinkBuilder.linkTo(MainController.class).slash("rooms").slash(room.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "/rooms/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changeRoom(@PathVariable("id") long id, @RequestBody Room room) {
        Optional<Room> existingRoom = roomRepository.findById(id);
        if(existingRoom.isPresent()) {
            existingRoom.get().update(room);
            roomRepository.save(existingRoom.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/rooms/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") long id) {
        Optional<Room> existingRoom = roomRepository.findById(id);
        if(existingRoom.isPresent()) {
            roomRepository.delete(existingRoom.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping(path="/rooms/{roomId}/tracks")
    public ResponseEntity<Set<Track>> getTracksByRoom(@PathVariable("roomId") long roomId) {
        Optional<Room> existingRoom = roomRepository.findById(roomId);
        if(existingRoom.isPresent()) {
            Set<Track> tracks = existingRoom.get().getPresentations();
            tracks.forEach((track)->track.setConferenceRoom(null));
            if(!tracks.isEmpty()) {
                return ResponseEntity.ok(tracks);
            }
            else {
                return ResponseEntity.noContent().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
