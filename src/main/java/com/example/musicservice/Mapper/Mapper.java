package com.example.musicservice.Mapper;

import com.example.musicservice.Data.MusicalTrack;
import com.example.musicservice.Data.MusicalTrackUpload;
import com.example.musicservice.Entity.MusicalTrackEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface Mapper {
    public static MusicalTrackEntity dataToEntity(MusicalTrack data){
        return new MusicalTrackEntity(data.getId(),
                data.getName(),
                data.getPathToImage(),
                data.getPathToFile(),
                data.getPathToLyrics(),
                data.getAuthorId(),
                data.getAlbumId(),
                data.getGenre(),
                data.getAuditions(),
                data.getReleaseDate());
    }
    public static MusicalTrack entityToData(MusicalTrackEntity entity){
        return new MusicalTrack(entity.getId(),
                entity.getName(),
                entity.getPathToImage(),
                entity.getPathToFile(),
                entity.getPathToLyrics(),
                entity.getAuthorId(),
                entity.getAlbumId(),
                entity.getGenre(),
                entity.getAuditions(),
                entity.getReleaseDate());
    }
    public static ArrayList<MusicalTrack> entityToData(ArrayList<MusicalTrackEntity> entity){
        ArrayList<MusicalTrack> data = new ArrayList<>();
        for(MusicalTrackEntity e : entity){
            data.add(entityToData(e));
        }
        return data;
    }
    public static MusicalTrack uploadToData(MusicalTrackUpload data){
        return new MusicalTrack(null,
                data.getName(),
                "",
                "",
                "",
                data.getAuthorId(),
                data.getAlbumId(),
                data.getGenre(),
                0L,
                LocalDateTime.now());
    }



    public static ArrayList<MusicalTrackEntity> dataToEntity(ArrayList<MusicalTrack> data){
        ArrayList<MusicalTrackEntity> entity = new ArrayList<>();
        for(MusicalTrack d : data){
            entity.add(dataToEntity(d));
        }
        return entity;
    }
}
