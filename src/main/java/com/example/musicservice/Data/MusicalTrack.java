package com.example.musicservice.Data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MusicalTrack {
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
