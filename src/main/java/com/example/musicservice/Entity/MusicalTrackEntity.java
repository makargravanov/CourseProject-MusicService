package com.example.musicservice.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MusicalTrackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String pathToImage;
    private String pathToFile;
    private String pathToLyrics;
    private Long authorId;
    private Long albumId;
    private String genre;
    private Long auditions;
    private LocalDateTime releaseDate;
}