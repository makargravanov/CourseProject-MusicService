package com.example.musicservice.Data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MusicalTrackUpload {
    private String name;
    private Long authorId;
    private Long albumId;
    private String genre;
}
